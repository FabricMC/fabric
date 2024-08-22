package net.fabricmc.fabric.impl.attachment.sync;

import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerConfigurationTask;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

public class AttachmentSync implements ModInitializer {
	public static final Identifier CONFIG_PACKET_ID = Identifier.of("fabric", "accepted_attachments_v1");
	public static final Identifier PACKET_ID = Identifier.of("fabric", "attachment_sync_v1");
	public static final ThreadLocal<Set<Identifier>> CLIENT_SUPPORTED_ATTACHMENTS = new ThreadLocal<>();

	public static AcceptedAttachmentsPayloadC2S createResponsePayload() {
		return new AcceptedAttachmentsPayloadC2S(AttachmentRegistryImpl.getRegisteredAttachments());
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.configurationC2S()
				.register(AcceptedAttachmentsPayloadC2S.ID, AcceptedAttachmentsPayloadC2S.CODEC);
		PayloadTypeRegistry.configurationS2C()
				.register(AcceptedAttachmentsPayloadS2C.ID, AcceptedAttachmentsPayloadS2C.CODEC);

		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
			if (ServerConfigurationNetworking.canSend(handler, PACKET_ID)) {
				handler.addTask(new AttachmentSyncTask());
			}
		});

		ServerConfigurationNetworking.registerGlobalReceiver(AcceptedAttachmentsPayloadC2S.ID, (payload, context) -> {
			context.server().submit(() -> {
				CLIENT_SUPPORTED_ATTACHMENTS.set(payload.acceptedAttachments());
			});
		});
	}

	private record AttachmentSyncTask() implements ServerPlayerConfigurationTask {
		public static final Key KEY = new Key(PACKET_ID.toString());

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
