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

package net.fabricmc.fabric.test.content.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Blocks;
import net.minecraft.item.HoeItem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.fabricmc.fabric.api.registry.WaxableBlocksRegistry;
import net.fabricmc.fabric.api.util.OxidizableFamily;

public final class ContentRegistryTest implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		// Expected behavior:
		//  - red wool is flattenable to yellow wool
		//  - quartz pillars are strippable to hay blocks
		//  - green wool is tillable to lime wool
		//  - copper ore, iron ore, gold ore, and diamond ore can be waxed into their deepslate variants and scraped back again
		//  - aforementioned ores can be scraped from diamond -> gold -> iron -> copper
		//  - quartz blocks can be waxed into smooth quartz and scraped back again

		FlattenableBlockRegistry.register(Blocks.RED_WOOL, Blocks.YELLOW_WOOL.getDefaultState());
		StrippableBlockRegistry.register(Blocks.QUARTZ_PILLAR, Blocks.HAY_BLOCK);

		// assert that StrippableBlockRegistry throws when the blocks don't have 'axis'
		try {
			StrippableBlockRegistry.register(Blocks.BLUE_WOOL, Blocks.OAK_LOG);
			StrippableBlockRegistry.register(Blocks.HAY_BLOCK, Blocks.BLUE_WOOL);
			throw new AssertionError("StrippableBlockRegistry didn't throw when blocks were missing the 'axis' property!");
		} catch (IllegalArgumentException e) {
			// expected behavior
			LOGGER.info("StrippableBlockRegistry test passed!");
		}

		TillableBlockRegistry.register(Blocks.GREEN_WOOL, context -> true, HoeItem.createTillAction(Blocks.LIME_WOOL.getDefaultState()));

		// note that since ores do not implement Oxidizable that a warning will be shown in the log
		OxidizableFamily testFamily = new OxidizableFamily.Builder()
				.unaffected(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE)
				.exposed(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE)
				.weathered(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE)
				.oxidized(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE)
				.build();

		OxidizableBlocksRegistry.registerFamily(testFamily);

		// assert that OxidizableFamily.Builder throws when a family is missing blocks
		try {
			new OxidizableFamily.Builder() // Has a null entry
					.unaffected(Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE)
					.exposed(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE)
					.weathered(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE)
					.oxidized(Blocks.NETHER_GOLD_ORE, null)
					.build();

			new OxidizableFamily.Builder() // Is missing Oxidization levels
					.unaffected(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE)
					.build();

			throw new AssertionError("OxidizableFamily.Builder didn't throw when blocks were missing in a family!");
		} catch (NullPointerException e) {
			// expected behavior
			LOGGER.info("OxidizableFamily test passed!");
		}

		WaxableBlocksRegistry.registerWaxablePair(Blocks.QUARTZ_BLOCK, Blocks.SMOOTH_QUARTZ);

		try {
			WaxableBlocksRegistry.registerWaxablePair(null, Blocks.DEAD_BRAIN_CORAL);
			WaxableBlocksRegistry.registerWaxablePair(Blocks.BRAIN_CORAL, null);

			throw new AssertionError("WaxableBlocksRegistry didn't throw when blocks were missing in a pair!");
		} catch (NullPointerException e) {
			// expected behavior
			LOGGER.info("WaxableBlocksRegistry test passed!");
		}
	}
}
