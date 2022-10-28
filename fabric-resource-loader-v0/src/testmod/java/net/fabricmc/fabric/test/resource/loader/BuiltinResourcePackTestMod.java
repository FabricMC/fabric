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

package net.fabricmc.fabric.test.resource.loader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;
import net.fabricmc.loader.api.FabricLoader;

public class BuiltinResourcePackTestMod implements ModInitializer {
	public static final String MODID = "fabric-resource-loader-v0-testmod";

	private static final Logger LOGGER = LoggerFactory.getLogger(BuiltinResourcePackTestMod.class);

	private static final Gson GSON = new Gson();

	@Override
	public void onInitialize() {
		// Should always be present as it's **this** mod.
		FabricLoader.getInstance().getModContainer(MODID)
				.map(container -> ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MODID, "test"),
						container, Text.literal("Fabric Resource Loader Test Pack"), ResourcePackActivationType.DEFAULT_ENABLED))
				.filter(success -> !success).ifPresent(success -> LOGGER.warn("Could not register built-in resource pack with custom name."));
		FabricLoader.getInstance().getModContainer(MODID)
				.map(container -> ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MODID, "test2"),
						container, ResourcePackActivationType.NORMAL))
				.filter(success -> !success).ifPresent(success -> LOGGER.warn("Could not register built-in resource pack."));

		// Test various metadata serialization issues (#2407)
		testMetadataSerialization("");
		testMetadataSerialization("Quotes: \"\" \"");
		testMetadataSerialization("Backslash: \\ \\\\");
	}

	private void testMetadataSerialization(String description) {
		String metadata = ModResourcePackUtil.serializeMetadata(1, description);
		JsonObject json;

		try {
			json = GSON.fromJson(metadata, JsonObject.class);
		} catch (JsonParseException exc) {
			throw new AssertionError("Metadata parsing test for description \"%s\" failed".formatted(description), exc);
		}

		String parsedDescription = json.get("pack").getAsJsonObject().get("description").getAsString();

		if (!description.equals(parsedDescription)) {
			throw new AssertionError("Metadata parsing test for description failed: expected \"%s\", got \"%s\"".formatted(description, parsedDescription));
		}
	}
}
