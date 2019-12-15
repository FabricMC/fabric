package net.fabricmc.fabric.impl.networking.handshake;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.util.version.VersionParsingException;

public final class HardcodedHandshakeTextFactory implements HandshakeModTextFactory {
	@Override
	public Text getErrorsHeader() {
		return new LiteralText("Mod mismatch has been detected:");
	}

	@Override
	public Text getBadVersionsMessage(String modId, String badVersion) {
		return new LiteralText(String.format("Mod \"%1$s\" has unrecognized version \"%2$s\"", modId, badVersion));
	}

	@Override
	public Text getVersionCheckErrorMessage(String modId, Version v, VersionParsingException ex) {
		return new LiteralText(String.format("Mod \"%1$s\" with version \"%2$s\" has encountered an error in version checking: \"%3$s\"", modId, v.getFriendlyString(), ex.getMessage()));
	}

	@Override
	public Text getVersionPredicateFailedMessage(String modId, Version v, String st) {
		return new LiteralText(String.format("Mod \"%1$s\" requires version \"%3$s\", but detected version \"%2$s\"", modId, v.getFriendlyString(), st));
	}

	@Override
	public Text getAbsentMessage(String modId) {
		return new LiteralText(String.format("Mod \"%1$s\" is missing", modId));
	}

	@Override
	public Text getVersionMismatchMessage(String modId, Version remoteVersion) {
		return new LiteralText(String.format("Mod \"%1$s\" with version \"%2$s\" is rejected", modId, remoteVersion));
	}
}
