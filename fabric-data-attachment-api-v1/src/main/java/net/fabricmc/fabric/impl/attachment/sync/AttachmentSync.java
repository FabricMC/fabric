package net.fabricmc.fabric.impl.attachment.sync;

import java.util.Set;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

public class AttachmentSync {
	public static final Identifier CONFIG_PACKET_ID = Identifier.of("fabric", "accepted_attachments_v1");
	public static final Identifier PACKET_ID = Identifier.of("fabric", "attachment_sync_v1");
	public static final ThreadLocal<Set<Identifier>> CLIENT_SUPPORTED_ATTACHMENTS = new ThreadLocal<>();

	public static AcceptedAttachmentsPayloadC2S createResponsePayload() {
		return new AcceptedAttachmentsPayloadC2S(AttachmentRegistryImpl.getRegisteredAttachments());
	}
}
