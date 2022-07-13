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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class BuiltinResourcePackTestMod implements ModInitializer {
	public static final String MODID = "fabric-resource-loader-v0-testmod";

	private static final Logger LOGGER = LoggerFactory.getLogger(BuiltinResourcePackTestMod.class);

	@Override
	public void onInitialize() {
		ModContainer mod = FabricLoader.getInstance().getModContainer(MODID).orElseThrow();

		// Test built-in resource packs.
		if (!ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MODID, "test"), mod, "Fabric Resource Loader Test Pack", ResourcePackActivationType.NORMAL)) {
			LOGGER.warn("Could not register built-in resource pack with custom name.");
		}

		if (!ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MODID, "test2"), mod, ResourcePackActivationType.NORMAL)) {
			LOGGER.warn("Could not register built-in resource pack.");
		}

		// Test fabric separate pack format.
		try (BufferedReader reader = Files.newBufferedReader(mod.findPath("resourcepacks/test2/pack.mcmeta").orElseThrow(), StandardCharsets.UTF_8)) {
			JsonObject packObject = JsonHelper.deserialize(reader).getAsJsonObject(PackResourceMetadata.READER.getKey());
			PackResourceMetadata metadata = PackResourceMetadata.READER.fromJson(packObject);
			Preconditions.checkState(ResourcePackCompatibility.from(metadata, ResourceType.CLIENT_RESOURCES).isCompatible(), "fabric:resource_pack_format is not compatible!");
			Preconditions.checkState(ResourcePackCompatibility.from(metadata, ResourceType.SERVER_DATA).isCompatible(), "fabric:data_pack_format is not compatible!");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
