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

package net.fabricmc.fabric.test.tag.client.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEnchantmentTags;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ClientTagTest implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientTagTest.class);
	private static final String MODID = "fabric-clients-tags-api-v1-testmod";

	@Override
	public void onInitializeClient() {
		final ModContainer container = FabricLoader.getInstance().getModContainer(MODID).get();

		if (!ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MODID, "test2"),
				container, ResourcePackActivationType.ALWAYS_ENABLED)) {
			throw new IllegalStateException("Could not register built-in resource pack.");
		}

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			if (ClientTags.getOrCreateLocalTag(ConventionalEnchantmentTags.INCREASES_BLOCK_DROPS) == null) {
				throw new AssertionError("Expected to load c:fortune, but it was not found!");
			}

			if (!ClientTags.isInWithLocalFallback(ConventionalBlockTags.ORES, Blocks.DIAMOND_ORE)) {
				throw new AssertionError("Expected to find diamond ore in c:ores, but it was not found!");
			}

			if (ClientTags.isInWithLocalFallback(ConventionalBlockTags.ORES, Blocks.DIAMOND_BLOCK)) {
				throw new AssertionError("Did not expect to find diamond block in c:ores, but it was found!");
			}

			if (!ClientTags.isInLocal(ConventionalBiomeTags.FOREST, BiomeKeys.FOREST)) {
				throw new AssertionError("Expected to find forest in c:forest, but it was not found!");
			}

			if (ClientTags.isInWithLocalFallback(TagKey.of(Registries.BLOCK.getKey(),
					new Identifier("fabric", "sword_efficient")), Blocks.DIRT)) {
				throw new AssertionError("Expected not to find dirt in fabric:sword_efficient, but it was found!");
			}

			// Success!
			LOGGER.info("The tests for client tags passed!");
		});

		ClientTickEvents.END_WORLD_TICK.register(client -> {
			if (!ClientTags.isInWithLocalFallback(TagKey.of(Registries.BLOCK.getKey(),
					new Identifier("fabric", "sword_efficient")), Blocks.DIRT)) {
				throw new AssertionError("Expected to find dirt in fabric:sword_efficient, but it was not found!");
			}
		});
	}
}
