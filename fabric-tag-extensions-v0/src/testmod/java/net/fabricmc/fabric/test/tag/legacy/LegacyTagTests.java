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

package net.fabricmc.fabric.test.tag.legacy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class LegacyTagTests implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("LegacyTagTests");
	public static final Tag<Block> FALLING = TagRegistry.block(new Identifier("testmod", "falling"));
	public static final Tag<Item> METALS = TagRegistry.item(new Identifier("testmod", "metals"));

	@Override
	public void onInitialize() {
		LOGGER.info("Loading legacy tag testmod");

		// This tests an existing tag from another datapack (tags-v1)
		ServerLifecycleEvents.SERVER_STARTED.register(this::testFallingTag);
		// Test one from our own datapack, also tests items
		ServerLifecycleEvents.SERVER_STARTED.register(this::testMetals);
	}

	private void testFallingTag(MinecraftServer server) {
		if (FALLING.values().isEmpty()) {
			throw new AssertionError("Falling tag should not be empty. (Hint: check resource loader)");
		}

		if (!FALLING.contains(Blocks.SAND)) {
			throw new AssertionError("Falling tag did not contain SAND");
		}

		// Lets only test two powders rather than all 16
		if (!FALLING.contains(Blocks.WHITE_CONCRETE_POWDER)) {
			throw new AssertionError("Falling tag did not contain WHITE_CONCRETE_POWDER");
		}

		if (!FALLING.contains(Blocks.ORANGE_CONCRETE_POWDER)) {
			throw new AssertionError("Falling tag did not contain ORANGE_CONCRETE_POWDER");
		}

		LOGGER.info("Legacy Block tag tests passed");
	}

	private void testMetals(MinecraftServer server) {
		if (METALS.values().isEmpty()) {
			throw new AssertionError("Metals tag should not be empty. (Hint: check resource loader)");
		}

		if (!METALS.contains(Items.IRON_INGOT)) {
			throw new AssertionError("Boss tag did not contain IRON_INGOT");
		}

		if (!METALS.contains(Items.GOLD_INGOT)) {
			throw new AssertionError("Boss tag did not contain GOLD_INGOT");
		}

		LOGGER.info("Legacy Item tag tests passed");
	}
}
