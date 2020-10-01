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

package net.fabricmc.fabric.test.tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.v1.Tags;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class TagTests implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("TagTests");
	public static final Tag.Identified<Block> FALLING = Tags.requiredBlock(new Identifier("testmod", "falling"));
	public static final Tag.Identified<EntityType<?>> BOSS = Tags.entityType(new Identifier("testmod", "boss"));

	@Override
	public void onInitialize() {
		LOGGER.info("Loading tag testmod");
		// Test two types to verify we don't have a fluke on one test
		ServerLifecycleEvents.SERVER_STARTED.register(this::testFallingTag);
		ServerLifecycleEvents.SERVER_STARTED.register(this::testBossTag);
	}

	// Verify the custom block tag is working
	private void testFallingTag(MinecraftServer server) {
		// This is a required tag, so it will fail if not bound
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

		LOGGER.info("Block tag tests passed");
	}

	// Verify the custom entity tag is working
	private void testBossTag(MinecraftServer server) {
		if (BOSS.values().isEmpty()) {
			throw new AssertionError("Boss tag should not be empty. (Hint: check resource loader)");
		}

		if (!BOSS.contains(EntityType.ENDER_DRAGON)) {
			throw new AssertionError("Boss tag did not contain ENDER_DRAGON");
		}

		if (!BOSS.contains(EntityType.WITHER)) {
			throw new AssertionError("Boss tag did not contain WITHER");
		}

		LOGGER.info("Entity type tag tests passed");
	}
}
