package net.fabricmc.fabric.impl.networking.handshake;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.util.version.VersionParsingException;

public final class TranslatedHandshakeTextFactory implements HandshakeModTextFactory {
	@Override
	public Text getAbsentMessage(String modId) {
		return new TranslatableText("fabric-networking-handshake-v1.missing", modId);
	}

	@Override
	public Text getVersionMismatchMessage(String modId, Version remoteVersion) {
		return new TranslatableText("fabric-networking-handshake-v1.version_rejection", modId, remoteVersion.getFriendlyString());
	}

	@Override
	public Text getErrorsHeader() {
		return new TranslatableText("fabric-networking-handshake-v1.errors");
	}

	@Override
	public Text getBadVersionsMessage(String modId, String badVersion) {
		return new TranslatableText("fabric-networking-handshake-v1.bad_version", modId, badVersion);
	}

	@Override
	public Text getVersionCheckErrorMessage(String modId, Version v, VersionParsingException ex) {
		return new TranslatableText("fabric-networking-handshake-v1.version_check_error", modId, v.getFriendlyString(), ex.getMessage());
	}

	@Override
	public Text getVersionPredicateFailedMessage(String modId, Version v, String st) {
		return new TranslatableText("fabric-networking-handshake-v1.version_predicate_failed", modId, v.getFriendlyString(), st);
	}
}
