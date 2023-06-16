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

package net.fabricmc.fabric.impl.permissions;

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

import net.minecraft.util.Identifier;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.permissions.v1.IllegalModResponsePolicy;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricPermissionsImpl implements DedicatedServerModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger(FabricPermissionsImpl.class);

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static final String MOD_ID = "fabric-permissions-api-v1";

	public static final Identifier PERMISSIONS_CHANNEL = new Identifier(MOD_ID, "permissions");

	public static boolean enabled;
	public static IllegalModResponsePolicy illegalModResponsePolicy;
	public static List<String> illegalMods;
	public static List<String> illegalFeatures;

	static {
		File configDir = FabricLoader.getInstance().getConfigDir().resolve("fabric").toFile();

		if (!configDir.exists()) {
			if (!configDir.mkdir()) {
				LOGGER.warn("[FabricPermissions] Could not create configuration directory: " + configDir.getAbsolutePath());
			}
		}

		File configFile = new File(configDir, "permissions.json");

		JsonObject rootObject = new JsonObject();
		if (configFile.exists()) {
			try (Reader reader = Files.newBufferedReader(configFile.toPath())) {
				rootObject = JsonParser.parseReader(reader).getAsJsonObject();
			} catch (IOException e) {
				LOGGER.warn("[FabricPermissions] Could not read permissions file '" + configFile.getAbsolutePath() + "'", e);
			}
		}

		if (!rootObject.has("enabled")) {
			rootObject.addProperty("enabled", false);
		}
		if (!rootObject.has("illegalModResponsePolicy")) {
			rootObject.addProperty("illegalModResponsePolicy", "warn");
		}
		if (!rootObject.has("illegalMods")) {
			rootObject.add("illegalMods", new JsonArray());
		}
		if (!rootObject.has("illegalFeatures")) {
			rootObject.add("illegalFeatures", new JsonArray());
		}

		enabled = rootObject.get("enabled").getAsBoolean();
		illegalModResponsePolicy = GSON.fromJson(rootObject.get("illegalModResponsePolicy"), new TypeToken<IllegalModResponsePolicy>() {}.getType());
		illegalMods = GSON.fromJson(rootObject.getAsJsonArray("illegalMods"), new TypeToken<List<String>>() {}.getType());
		illegalFeatures = GSON.fromJson(rootObject.getAsJsonArray("illegalFeatures"), new TypeToken<List<String>>() {}.getType());

		try (Writer writer = Files.newBufferedWriter(configFile.toPath())) {
			writer.write(GSON.toJson(rootObject));
		} catch (IOException e) {
			LOGGER.warn("[FabricPermissions] Could not store permissions file '" + configFile.getAbsolutePath() + "'", e);
		}
	}

	@Override
	public void onInitializeServer() {
		ServerPlayNetworking.registerGlobalReceiver(PERMISSIONS_CHANNEL, new ModListHandler());
	}
}
