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
import net.minecraft.world.biome.BiomeKeys;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEnchantmentTags;

public class ClientTagTest implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientTagTest.class);

	@Override
	public void onInitializeClient() {
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

			// Success!
			LOGGER.info("The tests for client tags passed!");
		});
	}
}
