package net.fabricmc.fabric.api.networking.handshake.v1;

import net.minecraft.text.Text;

import net.fabricmc.loader.api.Version;

/**
 * A provider of text messages based on whether a client accepts translated messages.
 */
public interface HandshakeTextFactory {
	/**
	 * Returns the default message that is sent when a mod is absent on the client but
	 * required by the server.
	 *
	 * @param modId the mod checked
	 * @return the message
	 */
	Text getAbsentMessage(String modId);

	/**
	 * Returns the default message that is sent when a mod on the client has a version
	 * that is rejected by the server.
	 *
	 * @param modId the mod checked
	 * @param remoteVersion the version on the client
	 * @return the message
	 */
	Text getVersionMismatchMessage(String modId, Version remoteVersion);
}
