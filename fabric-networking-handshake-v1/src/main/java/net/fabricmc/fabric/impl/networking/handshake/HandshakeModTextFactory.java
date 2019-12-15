package net.fabricmc.fabric.impl.networking.handshake;

import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.handshake.v1.HandshakeTextFactory;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.util.version.VersionParsingException;

public interface HandshakeModTextFactory extends HandshakeTextFactory {
	Text getErrorsHeader();

	Text getBadVersionsMessage(String modId, String badVersion);

	Text getVersionCheckErrorMessage(String modId, Version v, VersionParsingException ex);

	Text getVersionPredicateFailedMessage(String modId, Version v, String st);
}
