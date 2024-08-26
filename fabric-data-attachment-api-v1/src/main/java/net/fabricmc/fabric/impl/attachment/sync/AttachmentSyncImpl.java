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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import io.netty.channel.ChannelHandler;

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
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;
import net.fabricmc.fabric.mixin.attachment.ClientConnectionAccessor;
import net.fabricmc.fabric.mixin.networking.accessor.ServerCommonNetworkHandlerAccessor;

public class AttachmentSyncImpl implements ModInitializer {
	public static final Identifier CONFIG_PACKET_ID = Identifier.of("fabric", "accepted_attachments_v1");
	public static final Identifier PACKET_ID = Identifier.of("fabric", "attachment_sync_v1");
	public static final Identifier REFRESH_PACKET_ID = Identifier.of("fabric", "attachment_refresh_v1");
	public static final ThreadLocal<Set<Identifier>> CLIENT_SUPPORTED_ATTACHMENTS = ThreadLocal.withInitial(Set::of);

	public static AcceptedAttachmentsPayloadC2S createResponsePayload() {
		return new AcceptedAttachmentsPayloadC2S(AttachmentRegistryImpl.getRegisteredAttachments());
	}

	public static void trySync(AttachmentSyncPayload payload, ServerPlayerEntity player) {
		if (ServerPlayNetworking.canSend(player, PACKET_ID)) {
			ServerPlayNetworking.send(player, payload);
		}
	}

	private static Set<Identifier> decodeResponsePayload(AcceptedAttachmentsPayloadC2S payload) {
		Set<Identifier> atts = payload.acceptedAttachments();
		atts.retainAll(AttachmentRegistryImpl.getRegisteredAttachments());
		return atts;
	}

	public static void refreshAttachments(ServerPlayerEntity player) {
		if (ServerPlayNetworking.canSend(player, REFRESH_PACKET_ID)) {
			ServerPlayNetworking.send(player, new AttachmentRefreshPayloadS2C(Optional.empty()));
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
			// copied over from custom ingredient sync
			Set<Identifier> supportedAttachments = decodeResponsePayload(payload);
			ChannelHandler packetEncoder = ((ClientConnectionAccessor) ((ServerCommonNetworkHandlerAccessor) context.networkHandler()).getConnection())
					.getChannel()
					.pipeline()
					.get("encoder");

			if (packetEncoder != null) { // Null in singleplayer
				((SupportedAttachmentsPacketEncoder) packetEncoder).fabric_setSupportedAttachments(supportedAttachments);
			}

			context.networkHandler().completeTask(AttachmentSyncTask.KEY);
		});

		// Play
		PayloadTypeRegistry.playS2C().register(AttachmentSyncPayload.ID, AttachmentSyncPayload.CODEC);

		PayloadTypeRegistry.playS2C().register(AttachmentRefreshPayloadS2C.ID, AttachmentRefreshPayloadS2C.CODEC);
		PayloadTypeRegistry.playC2S().register(AttachmentRefreshPayloadC2S.ID, AttachmentRefreshPayloadC2S.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(AttachmentRefreshPayloadC2S.ID, (payload, context) -> {
			List<AttachmentChange> refreshed = new ArrayList<>();

			for (Map.Entry<AttachmentType<?>, AttachmentTargetInfo<?>> attachment : payload.attachments()) {
				AttachmentTypeImpl<?> type = (AttachmentTypeImpl<?>) attachment.getKey();
				AttachmentTarget target = attachment.getValue().getTarget(context.player().getServerWorld());

				if (type.syncType() == SyncType.CUSTOM) {
					if (!type.customSyncTargetTest().test(target, context.player())) {
						continue;
					}
				}

				// unnecessary in theory, but might as well send back the correct value while we're at it
				refreshed.add(new AttachmentChange(
						attachment.getValue(),
						attachment.getKey(),
						target.getAttached(type)
				));
			}

			context.responseSender().sendPacket(new AttachmentRefreshPayloadS2C(Optional.of(refreshed)));
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (ServerPlayNetworking.canSend(handler, PACKET_ID)) {
				ServerPlayerEntity player = handler.player;
				AttachmentSyncPayload payload =
						((AttachmentTargetImpl) player.getServerWorld()).fabric_getInitialSyncPayload(player);

				if (payload != null) {
					ServerPlayNetworking.send(player, payload);
				}
			}
		});

		EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) -> {
			AttachmentSyncPayload payload = ((AttachmentTargetImpl) trackedEntity).fabric_getInitialSyncPayload(player);

			if (payload != null) {
				ServerPlayNetworking.send(player, payload);
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
