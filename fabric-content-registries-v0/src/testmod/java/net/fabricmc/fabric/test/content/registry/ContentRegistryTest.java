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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.fabricmc.fabric.api.registry.LandPathNodeTypesRegistry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.registry.SculkSensorFrequencyRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.fabricmc.fabric.api.registry.VillagerInteractionRegistries;
import net.fabricmc.fabric.api.registry.VillagerPlantableRegistry;

public final class ContentRegistryTest implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ContentRegistryTest.class);

	public static final Identifier TEST_EVENT_ID = new Identifier("fabric-content-registries-v0-testmod", "test_event");
	public static final GameEvent TEST_EVENT = new GameEvent(TEST_EVENT_ID.toString(), GameEvent.DEFAULT_RANGE);

	@Override
	public void onInitialize() {
		// Expected behavior:
		//  - sand is now flammable
		//  - red wool is flattenable to yellow wool
		//  - quartz pillars are strippable to hay blocks
		//  - green wool is tillable to lime wool
		//  - copper ore, iron ore, gold ore, and diamond ore can be waxed into their deepslate variants and scraped back again
		//  - aforementioned ores can be scraped from diamond -> gold -> iron -> copper
		//  - villagers can now collect, consume (at the same level of bread) and compost apples
		//  - villagers can now collect and plant oak saplings
		//  - assign a loot table to the nitwit villager type
		//  - right-clicking a 'test_event' block will emit a 'test_event' game event, which will have a sculk sensor frequency of 2
		//  - dead bush is now considered as a dangerous block like sweet berry bushes (all entities except foxes should avoid it)

		LandPathNodeTypesRegistry.register(Blocks.DEAD_BUSH, PathNodeType.DAMAGE_OTHER, PathNodeType.DANGER_OTHER);

		FlammableBlockRegistry.getDefaultInstance().add(BlockTags.SAND, 4, 4);
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

		OxidizableBlocksRegistry.registerOxidizableBlockPair(Blocks.COPPER_ORE, Blocks.IRON_ORE);
		OxidizableBlocksRegistry.registerOxidizableBlockPair(Blocks.IRON_ORE, Blocks.GOLD_ORE);
		OxidizableBlocksRegistry.registerOxidizableBlockPair(Blocks.GOLD_ORE, Blocks.DIAMOND_ORE);

		OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE);
		OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE);
		OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);
		OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE);

		// assert that OxidizableBlocksRegistry throws when registered blocks are null
		try {
			OxidizableBlocksRegistry.registerOxidizableBlockPair(Blocks.EMERALD_ORE, null);
			OxidizableBlocksRegistry.registerOxidizableBlockPair(null, Blocks.COAL_ORE);

			OxidizableBlocksRegistry.registerWaxableBlockPair(null, Blocks.DEAD_BRAIN_CORAL);
			OxidizableBlocksRegistry.registerWaxableBlockPair(Blocks.BRAIN_CORAL, null);

			throw new AssertionError("OxidizableBlocksRegistry didn't throw when blocks were null!");
		} catch (NullPointerException e) {
			// expected behavior
			LOGGER.info("OxidizableBlocksRegistry test passed!");
		}

		VillagerInteractionRegistries.registerCollectable(Items.APPLE);
		VillagerInteractionRegistries.registerFood(Items.APPLE, 4);
		VillagerInteractionRegistries.registerCompostable(Items.APPLE);

		VillagerInteractionRegistries.registerCollectable(Items.OAK_SAPLING);
		VillagerPlantableRegistry.register(Items.OAK_SAPLING);

		// assert that VillagerPlantablesRegistry throws when getting a non-BlockItem
		try {
			VillagerPlantableRegistry.register(Items.STICK);

			throw new AssertionError("VillagerPlantablesRegistry didn't throw when item is not BlockItem!");
		} catch (Exception e) {
			// expected behavior
			LOGGER.info("VillagerPlantablesRegistry test passed!");
		}

		VillagerInteractionRegistries.registerGiftLootTable(VillagerProfession.NITWIT, new Identifier("fake_loot_table"));

		Registry.register(Registry.GAME_EVENT, TEST_EVENT_ID, TEST_EVENT);
		Registry.register(Registry.BLOCK, TEST_EVENT_ID, new TestEventBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
		SculkSensorFrequencyRegistry.register(TEST_EVENT, 2);

		// assert that SculkSensorFrequencyRegistry throws when registering a frequency outside the allowed range
		try {
			SculkSensorFrequencyRegistry.register(GameEvent.SHRIEK, 18);

			throw new AssertionError("SculkSensorFrequencyRegistry didn't throw when frequency was outside allowed range!");
		} catch (IllegalArgumentException e) {
			// expected behavior
			LOGGER.info("SculkSensorFrequencyRegistry test passed!");
		}
	}

	public static class TestEventBlock extends Block {
		public TestEventBlock(Settings settings) {
			super(settings);
		}

		@Override
		public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
			// Emit the test event
			world.emitGameEvent(player, TEST_EVENT, pos);
			return ActionResult.SUCCESS;
		}
	}
}
