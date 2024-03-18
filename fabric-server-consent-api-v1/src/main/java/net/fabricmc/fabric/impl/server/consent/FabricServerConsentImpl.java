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

package net.fabricmc.fabric.impl.server.consent;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricServerConsentImpl implements DedicatedServerModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(FabricServerConsentImpl.class);

	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter()).setPrettyPrinting().create();

	public static boolean enabled;
	public static List<Identifier> illegalFlags;

	static {
		File configDir = FabricLoader.getInstance().getConfigDir().resolve("fabric").toFile();

		if (!configDir.exists()) {
			if (!configDir.mkdir()) {
				LOGGER.warn("[FabricServerConsent] Could not create configuration directory: " + configDir.getAbsolutePath());
			}
		}

		File configFile = new File(configDir, "consents.json");

		JsonObject rootObject = new JsonObject();

		if (configFile.exists()) {
			try (Reader reader = Files.newBufferedReader(configFile.toPath())) {
				rootObject = JsonParser.parseReader(reader).getAsJsonObject();
			} catch (IOException e) {
				LOGGER.warn("[FabricServerConsent] Could not read consents file '" + configFile.getAbsolutePath() + "'", e);
			}
		}

		if (!rootObject.has("enabled")) {
			rootObject.addProperty("enabled", false);
		}

		if (!rootObject.has("illegalFlags")) {
			rootObject.add("illegalFlags", new JsonArray());
		}

		enabled = rootObject.get("enabled").getAsBoolean();
		illegalFlags = GSON.fromJson(rootObject.getAsJsonArray("illegalFlags"), new TypeToken<List<Identifier>>() { }.getType());

		try (Writer writer = Files.newBufferedWriter(configFile.toPath())) {
			writer.write(GSON.toJson(rootObject));
		} catch (IOException e) {
			LOGGER.warn("[FabricServerConsent] Could not store consents file '" + configFile.getAbsolutePath() + "'", e);
		}
	}

	@Override
	public void onInitializeServer() {
		if (enabled) {
			ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
				handler.sendPacket(new CustomPayloadS2CPacket(new IllegalFlagsCustomPayload(illegalFlags)));
			});
		}
	}
}
