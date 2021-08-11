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

package net.fabricmc.fabric.test.mininglevel;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

// This test must pass without the tool attribute API present.
// It has its own handlers for mining levels, which might "hide" this module
// not working on its own.
public final class MiningLevelTest implements ModInitializer {
	private static final String ID = "fabric-mining-level-api-v1-testmod";
	private static final Logger LOGGER = LogManager.getLogger();

	/// Tagged blocks
	// sword + dynamic mining level tag
	public static final Block NEEDS_NETHERITE_SWORD = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());
	// sword + vanilla mining level tag
	public static final Block NEEDS_STONE_SWORD = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());
	// any sword
	public static final Block NEEDS_ANY_SWORD = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());
	// shears
	public static final Block NEEDS_SHEARS = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());
	// vanilla mineable tag + dynamic mining level tag
	public static final Block NEEDS_NETHERITE_PICKAXE = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());
	// vanilla mineable tag, requires tool (this type of block doesn't exist in vanilla)
	public static final Block NEEDS_AXE = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());
	// vanilla mineable tag, requires tool (this type of block doesn't exist in vanilla)
	public static final Block NEEDS_HOE = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());
	// vanilla mineable tag, requires tool (this type of block doesn't exist in vanilla)
	public static final Block NEEDS_SHOVEL = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());

	@Override
	public void onInitialize() {
		register("needs_netherite_sword", NEEDS_NETHERITE_SWORD);
		register("needs_stone_sword", NEEDS_STONE_SWORD);
		register("needs_any_sword", NEEDS_ANY_SWORD);
		register("needs_shears", NEEDS_SHEARS);
		register("needs_netherite_pickaxe", NEEDS_NETHERITE_PICKAXE);
		register("needs_axe", NEEDS_AXE);
		register("needs_hoe", NEEDS_HOE);
		register("needs_shovel", NEEDS_SHOVEL);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> test());
	}

	private static void register(String id, Block block) {
		Identifier identifier = new Identifier(ID, id);
		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
	}

	private static void test() {
		List<AssertionError> errors = new ArrayList<>();
		test(errors, () -> checkMiningLevel(NEEDS_NETHERITE_SWORD, List.of(Items.NETHERITE_SWORD), List.of(Items.NETHERITE_PICKAXE, Items.STONE_SWORD)));
		test(errors, () -> checkMiningLevel(NEEDS_STONE_SWORD, List.of(Items.STONE_SWORD, Items.IRON_SWORD), List.of(Items.STONE_PICKAXE, Items.WOODEN_SWORD)));
		test(errors, () -> checkMiningLevel(NEEDS_ANY_SWORD, List.of(Items.WOODEN_SWORD), List.of()));
		test(errors, () -> checkMiningLevel(NEEDS_SHEARS, List.of(Items.SHEARS), List.of()));
		test(errors, () -> checkMiningLevel(NEEDS_NETHERITE_PICKAXE, List.of(Items.NETHERITE_PICKAXE), List.of(Items.DIAMOND_PICKAXE, Items.NETHERITE_AXE)));
		test(errors, () -> checkMiningLevel(Blocks.STONE, List.of(Items.WOODEN_PICKAXE), List.of(Items.STICK)));
		test(errors, () -> checkMiningLevel(NEEDS_AXE, List.of(Items.WOODEN_AXE), List.of(Items.STICK)));
		test(errors, () -> checkMiningLevel(NEEDS_HOE, List.of(Items.WOODEN_HOE), List.of(Items.STICK)));
		test(errors, () -> checkMiningLevel(NEEDS_SHOVEL, List.of(Items.WOODEN_SHOVEL), List.of(Items.STICK)));

		if (errors.isEmpty()) {
			LOGGER.info("Mining level tests passed!");
		} else {
			AssertionError error = new AssertionError("Mining level tests failed!");
			errors.forEach(error::addSuppressed);
			throw error;
		}
	}

	private static void test(List<AssertionError> errors, Runnable runnable) {
		try {
			runnable.run();
		} catch (AssertionError e) {
			errors.add(e);
		}
	}

	private static void checkMiningLevel(Block block, List<Item> successfulItems, List<Item> failingItems) {
		BlockState state = block.getDefaultState();

		for (Item success : successfulItems) {
			ItemStack successStack = new ItemStack(success);

			if (!successStack.isSuitableFor(state)) {
				throw new AssertionError(success + " is not suitable for " + block);
			}

			if (successStack.getMiningSpeedMultiplier(state) == 1f) {
				throw new AssertionError(success + " returns default mining speed for " + block);
			}
		}

		for (Item failing : failingItems) {
			if (new ItemStack(failing).isSuitableFor(state)) {
				throw new AssertionError(failing + " is suitable for " + block);
			}
		}
	}
}
