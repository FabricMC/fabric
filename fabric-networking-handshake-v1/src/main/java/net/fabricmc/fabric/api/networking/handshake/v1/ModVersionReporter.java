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

package net.fabricmc.fabric.api.networking.handshake.v1;

import java.util.function.Predicate;

import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import net.fabricmc.loader.api.Version;

/**
 * A custom handshake handler that reports an error message to the client on mod rejections.
 *
 * @see ModHandshakeRegistrar
 */
@FunctionalInterface
public interface ModVersionReporter {
	/**
	 * Create a mod version reporter based on a simple version checker.
	 *
	 * <p>This mod version reporter sends default messages to the client for version
	 * mismatches/missing mod.
	 *
	 * @param checker the mod version checker
	 * @return the created reporter
	 */
	static ModVersionReporter fromChecker(Predicate<Version> checker) {
		return (networkHandler, modId, remoteVersion) ->
				checker.test(remoteVersion) ? null : remoteVersion == null ? getDefaultAbsentMessage(modId) : getDefaultVersionMismatchMessage(modId, remoteVersion);
	}

	/**
	 * Converts a translated text to a literal one so that it's ready to be sent to
	 * vanilla clients.
	 *
	 * @param original the original text
	 * @return the literal text
	 */
	static Text toLiteral(Text original) {
		return new LiteralText(original.getString());
	}

	/**
	 * Returns the default message that is sent when a mod is absent on the client but
	 * required by the server.
	 *
	 * @param modId the mod checked
	 * @return the message
	 */
	static Text getDefaultAbsentMessage(String modId) {
		return new TranslatableText("fabric-networking-handshake-v1.missing", modId);
	}

	/**
	 * Returns the default message that is sent when a mod on the client has a version
	 * that is rejected by the server.
	 *
	 * @param modId the mod checked
	 * @param remoteVersion the version on the client
	 * @return the message
	 */
	static Text getDefaultVersionMismatchMessage(String modId, Version remoteVersion) {
		return new TranslatableText("fabric-networking-handshake-v1.version_rejection", modId, remoteVersion.getFriendlyString());
	}

	/**
	 * Reports an error message for the client mod version information.
	 *
	 * <p><b>Note</b>: the reporter is run off thread and should not rely on checking
	 * from server thread. Preferably, it should check from fields etc. previously set
	 * up when the game was initialized.
	 *
	 * <p>The reporter should probably return the default message offered by the
	 * handshaking mod rather than a mod's custom translated message if a mod is
	 * absent on the client side. If the translation is not available, the
	 * translation key instead of the translated text will appear to the client.
	 *
	 * @param networkHandler the login network handler
	 * @param modId the mod's id
	 * @param remoteVersion the client's mod version
	 * @return an error message, or {@code null} if the mod is okay
	 */
	/* Nullable */ Text report(ServerLoginNetworkHandler networkHandler, String modId, /* Nullable */ Version remoteVersion);
}
