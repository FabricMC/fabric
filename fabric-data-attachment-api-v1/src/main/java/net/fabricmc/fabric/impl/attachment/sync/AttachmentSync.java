/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.attachment.sync;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerConfigurationTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

public class AttachmentSync implements ModInitializer {
	public static final Identifier CONFIG_PACKET_ID = Identifier.of("fabric", "accepted_attachments_v1");
	public static final Identifier CHUNK_INITIAL_PACKET_ID = Identifier.of("fabric", "attachment_initial_sync_chunk_v1");
	public static final Identifier CHUNK_CHANGE_PACKET_ID = Identifier.of("fabric", "attachment_change_chunk_v1");
	public static final Identifier ENTITY_PACKET_ID = Identifier.of("fabric", "attachment_change_entity_v1");
	public static final Identifier BLOCK_ENTITY_PACKET_ID = Identifier.of("fabric", "attachment_change_block_entity_v1");
	public static final Identifier WORLD_PACKET_ID = Identifier.of("fabric", "attachment_change_world_v1");
	public static final ThreadLocal<Set<Identifier>> CLIENT_SUPPORTED_ATTACHMENTS = new ThreadLocal<>();

	public static AcceptedAttachmentsPayloadC2S createResponsePayload() {
		return new AcceptedAttachmentsPayloadC2S(AttachmentRegistryImpl.getRegisteredAttachments());
	}

	// Assumes the type is already synchronizable
	public static void syncIfPossible(CustomPayload payload, AttachmentType<?> type, AttachmentTarget target, ServerPlayerEntity player) {
		if (ServerPlayNetworking.canSend(player, payload.getId())
				&& CLIENT_SUPPORTED_ATTACHMENTS.get().contains(type.identifier())
				&& type.syncTargetTest().test(target, player)) {
			ServerPlayNetworking.send(player, payload);
		}
	}

	@Override
	public void onInitialize() {
		// Config
		PayloadTypeRegistry.configurationC2S()
						.register(AcceptedAttachmentsPayloadC2S.ID, AcceptedAttachmentsPayloadC2S.CODEC);
		PayloadTypeRegistry.configurationS2C()
						.register(AcceptedAttachmentsPayloadS2C.ID, AcceptedAttachmentsPayloadS2C.CODEC);

		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
			if (ServerConfigurationNetworking.canSend(handler, CONFIG_PACKET_ID)) {
				handler.addTask(new AttachmentSyncTask());
			}
		});

		ServerConfigurationNetworking.registerGlobalReceiver(AcceptedAttachmentsPayloadC2S.ID, (payload, context) -> {
			context.server().submit(() -> {
				CLIENT_SUPPORTED_ATTACHMENTS.set(payload.acceptedAttachments());
			});
		});

		// Play
		PayloadTypeRegistry.playS2C().register(BlockEntityAttachmentChangePayloadS2C.ID, BlockEntityAttachmentChangePayloadS2C.CODEC);
		PayloadTypeRegistry.playS2C().register(EntityAttachmentChangePayloadS2C.ID, EntityAttachmentChangePayloadS2C.CODEC);
		PayloadTypeRegistry.playS2C().register(ChunkAttachmentInitialSyncPayloadS2C.ID, ChunkAttachmentInitialSyncPayloadS2C.CODEC);
		PayloadTypeRegistry.playS2C().register(WorldAttachmentChangePayloadS2C.ID, WorldAttachmentChangePayloadS2C.CODEC);

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (ServerPlayNetworking.canSend(handler, WORLD_PACKET_ID)) {
				ServerPlayerEntity player = handler.player;
				List<AttachmentChange> changes = ((AttachmentTargetImpl) player.getServerWorld()).fabric_getInitialAttachmentsFor(player);

				if (changes != null) {
					ServerPlayNetworking.send(player, new WorldAttachmentChangePayloadS2C(changes));
				}
			}
		});

		EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) -> {
			List<AttachmentChange> changes = ((AttachmentTargetImpl) trackedEntity).fabric_getInitialAttachmentsFor(player);

			if (changes != null) {
				ServerPlayNetworking.send(player, new EntityAttachmentChangePayloadS2C(trackedEntity.getId(), changes));
			}
		});
	}

	private record AttachmentSyncTask() implements ServerPlayerConfigurationTask {
		public static final Key KEY = new Key(CONFIG_PACKET_ID.toString());

		@Override
		public void sendPacket(Consumer<Packet<?>> sender) {
			// Send packet with 1 so the client can send us back the list of supported tags.
			// 1 is sent in case we need a different protocol later for some reason.
			sender.accept(ServerConfigurationNetworking.createS2CPacket(AcceptedAttachmentsPayloadS2C.INSTANCE));
		}

		@Override
		public Key getKey() {
			return KEY;
		}
	}
}
