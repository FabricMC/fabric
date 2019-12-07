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

package net.fabricmc.fabric.impl.networking.handshake;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.networking.handshake.v1.ModHandshakeRegistrar;
import net.fabricmc.fabric.api.networking.handshake.v1.ModVersionReporter;
import net.fabricmc.fabric.api.networking.v1.event.LoginQueryStartCallback;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerLoginQueryResponsePacketContext;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPacketReceiverRegistries;
import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.sender.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.CustomValue.CvType;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.fabricmc.loader.util.version.VersionPredicateParser;

/**
 * The entrypoint for the handshake mod.
 */
public final class HandshakeMod implements ModInitializer {
	static final String CUSTOM_KEY = "fabric-networking-handshake";
	static final String ID = "fabric-networking-handshake-v1";
	static final Identifier HELLO_CHANNEL = id("hello");
	private static final Logger LOGGER = LogManager.getLogger();
	// todo future: add a list of legacy handlers which warns when legacy is detected
	private Multimap<String, ModVersionReporter> remoteVersionRequirements;

	/**
	 * Creates an id with the mod's id as its namespace.
	 *
	 * @param name the name
	 * @return the created id
	 */
	public static Identifier id(String name) {
		return new Identifier(ID, name);
	}

	@Override
	public void onInitialize() {
		FabricLoader loader = FabricLoader.getInstance();
		Multimap<String, ModVersionReporter> remoteVersionRequirements = HashMultimap.create();

		for (ModContainer mod : loader.getAllMods()) {
			ModMetadata metadata = mod.getMetadata();
			CustomValue value = metadata.getCustomValue(CUSTOM_KEY);

			if (value != null && value.getType() == CvType.OBJECT) {
				CustomValue.CvObject cvObject = value.getAsObject();

				CustomValue versionRange = cvObject.get("versionRange");

				if (versionRange != null && versionRange.getType() == CvType.STRING) {
					String st = versionRange.getAsString();
					ModVersionReporter networkRequirement = (handler, modId, v) -> {
						if (v == null) {
							return ModVersionReporter.getDefaultAbsentMessage(modId);
						}

						try {
							if (VersionPredicateParser.matches(v, st)) {
								return null;
							}

							return new TranslatableText("fabric-networking-handshake-v1.version_predicate_failed", modId, v.getFriendlyString(), st);
						} catch (VersionParsingException ex) {
							return new TranslatableText("fabric-networking-handshake-v1.version_check_error", modId, v.getFriendlyString(), ex.getLocalizedMessage());
						}
					};
					remoteVersionRequirements.put(metadata.getId(), networkRequirement);
					continue;
				}

				CustomValue requireExactVersion = cvObject.get("requireExactVersion");

				if (requireExactVersion != null && requireExactVersion.getType() == CvType.BOOLEAN) {
					Predicate<Version> networkRequirement = requireExactVersion.getAsBoolean() ? Predicate.isEqual(metadata.getVersion()) : Objects::nonNull;
					remoteVersionRequirements.put(metadata.getId(), ModVersionReporter.fromChecker(networkRequirement));
				}
			}
		}

		for (ModHandshakeRegistrar registrar : loader.getEntrypoints(CUSTOM_KEY, ModHandshakeRegistrar.class)) {
			registrar.registerTo(remoteVersionRequirements);
		}

		this.remoteVersionRequirements = ImmutableMultimap.copyOf(remoteVersionRequirements);

		ServerStartCallback.EVENT.register(this::onServerStart);
		LoginQueryStartCallback.EVENT.register(this::onLoginQueryStart);
		ServerPacketReceiverRegistries.LOGIN_QUERY_RESPONSE.register(HELLO_CHANNEL, this::onLoginQueryResponse);
	}

	private void onServerStart(MinecraftServer server) {
		((FabricExtraMetaHolder) server.getServerMetadata()).setFabricExtraMeta(new FabricExtraMeta(remoteVersionRequirements.keySet()));
	}

	private void onLoginQueryStart(MinecraftServer server, ServerLoginNetworkHandler networkHandler, PacketSender sender) {
		PacketByteBuf data = PacketByteBufs.create();
		data.writeVarInt(remoteVersionRequirements.size());

		for (String modId : remoteVersionRequirements.keySet()) {
			data.writeString(modId);
		}

		sender.send(HELLO_CHANNEL, data);
	}

	private void onLoginQueryResponse(ServerLoginQueryResponsePacketContext context, PacketByteBuf buffer) {
		Multimap<String, ModVersionReporter> allCheckers = HashMultimap.create(remoteVersionRequirements);
		ServerLoginNetworkHandler networkHandler = context.getNetworkHandler();
		List<Text> errorMessages = new ArrayList<>();

		if (context.isUnderstood()) {
			int n = buffer.readVarInt();

			for (int i = 1; i <= n; i++) {
				String modId = buffer.readString(32767);
				String versionString = buffer.readString(32767);

				final Version version;

				if (versionString.isEmpty()) {
					version = null;
				} else {
					try {
						version = Version.parse(versionString);
					} catch (VersionParsingException ex) {
						errorMessages.add(new TranslatableText("fabric-networking-handshake-v1.bad_version", modId, versionString));
						continue;
					}
				}

				for (ModVersionReporter checker : allCheckers.removeAll(modId)) {
					Text errorMessage = checker.report(networkHandler, modId, version);

					if (errorMessage != null) {
						errorMessages.add(errorMessage);
					}
				}
			}
		}

		for (Entry<String, ModVersionReporter> entry : allCheckers.entries()) {
			Text errorMessage = entry.getValue().report(networkHandler, entry.getKey(), null);

			if (errorMessage != null) {
				errorMessages.add(errorMessage);
			}
		}

		if (!errorMessages.isEmpty()) {
			Text disconnectReason = new TranslatableText("fabric-networking-handshake-v1.errors");

			for (Text errorMessage : errorMessages) {
				disconnectReason.append("\n").append(errorMessage);
			}

			// Send raw English to clients without handshake mod
			if (!context.isUnderstood()) {
				disconnectReason = ModVersionReporter.toLiteral(disconnectReason);
			}

			if (context.getNetworkHandler().getConnection().isLocal()) {
				LOGGER.error("Tried to reject singleplayer connection for reason:\n{}", disconnectReason.getString());
			} else {
				context.getNetworkHandler().disconnect(disconnectReason);
			}
		}
	}
}
