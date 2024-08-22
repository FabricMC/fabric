package net.fabricmc.fabric.impl.attachment.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.impl.attachment.sync.AcceptedAttachmentsPayloadS2C;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;

public class AttachmentSyncClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientConfigurationNetworking.registerGlobalReceiver(AcceptedAttachmentsPayloadS2C.ID, (payload, context) -> {
			context.responseSender().sendPacket(AttachmentSync.createResponsePayload());
		});
	}
}
