package net.fabricmc.fabric.impl.attachment.sync;

import java.util.Set;

import net.minecraft.network.handler.EncoderHandler;
import net.minecraft.util.Identifier;

/**
 * Implemented on {@link EncoderHandler} to store which custom ingredients the client supports.
 */
public interface SupportedAttachmentsPacketEncoder {
	void fabric_setSupportedAttachments(Set<Identifier> supportedAttachments);
}
