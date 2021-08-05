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
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

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
	// vanilla mineable tag + dynamic mining level tag
	public static final Block NEEDS_AXE = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());
	// vanilla mineable tag + dynamic mining level tag
	public static final Block NEEDS_HOE = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());
	// vanilla mineable tag + dynamic mining level tag
	public static final Block NEEDS_SHOVEL = new Block(AbstractBlock.Settings.of(Material.STONE).strength(2, 3).requiresTool());

	/// Legacy blocks (block settings, NOT declared in tags!)
	// TODO: Move everything from below here into tool attribute test mod
	/// This test really belongs to tool-attribute-api-v1 instead, but it's easier to keep them centralised.
	public static final Block NEEDS_SWORD_LEGACY = new Block(FabricBlockSettings.of(Material.STONE).strength(2, 3).requiresTool().breakByTool(FabricToolTags.SWORDS));
	public static final Block NEEDS_SHEARS_LEGACY = new Block(FabricBlockSettings.of(Material.STONE).strength(2, 3).requiresTool().breakByTool(FabricToolTags.SHEARS));
	public static final Block NEEDS_PICKAXE_LEGACY = new Block(FabricBlockSettings.of(Material.STONE).strength(2, 3).requiresTool().breakByTool(FabricToolTags.PICKAXES));
	public static final Block NEEDS_AXE_LEGACY = new Block(FabricBlockSettings.of(Material.STONE).strength(2, 3).requiresTool().breakByTool(FabricToolTags.AXES));
	public static final Block NEEDS_HOE_LEGACY = new Block(FabricBlockSettings.of(Material.STONE).strength(2, 3).requiresTool().breakByTool(FabricToolTags.HOES));
	public static final Block NEEDS_SHOVEL_LEGACY = new Block(FabricBlockSettings.of(Material.STONE).strength(2, 3).requiresTool().breakByTool(FabricToolTags.SHOVELS));

	/// Tagged tools (tool attribute API compat)
	// shears tag
	public static final Item FAKE_SHEARS = new Item(new Item.Settings().group(ItemGroup.TOOLS));
	// sword tag
	public static final Item FAKE_SWORD = new Item(new Item.Settings().group(ItemGroup.TOOLS));
	// pickaxe tag
	public static final Item FAKE_PICKAXE = new Item(new Item.Settings().group(ItemGroup.TOOLS));
	// axe tag
	public static final Item FAKE_AXE = new Item(new Item.Settings().group(ItemGroup.TOOLS));
	// hoe tag
	public static final Item FAKE_HOE = new Item(new Item.Settings().group(ItemGroup.TOOLS));
	// shovel tag
	public static final Item FAKE_SHOVEL = new Item(new Item.Settings().group(ItemGroup.TOOLS));

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
		register("needs_sword_legacy", NEEDS_SWORD_LEGACY);
		register("needs_shears_legacy", NEEDS_SHEARS_LEGACY);
		register("needs_pickaxe_legacy", NEEDS_PICKAXE_LEGACY);
		register("needs_axe_legacy", NEEDS_AXE_LEGACY);
		register("needs_hoe_legacy", NEEDS_HOE_LEGACY);
		register("needs_shovel_legacy", NEEDS_SHOVEL_LEGACY);
		register("fake_shears", FAKE_SHEARS);
		register("fake_sword", FAKE_SWORD);
		register("fake_pickaxe", FAKE_PICKAXE);
		register("fake_axe", FAKE_AXE);
		register("fake_hoe", FAKE_HOE);
		register("fake_shovel", FAKE_SHOVEL);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> test());
	}

	private static void register(String id, Block block) {
		Identifier identifier = new Identifier(ID, id);
		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
	}

	private static void register(String id, Item item) {
		Registry.register(Registry.ITEM, new Identifier(ID, id), item);
	}

	private static void test() {
		List<AssertionError> errors = new ArrayList<>();
		test(errors, () -> checkMiningLevel(NEEDS_NETHERITE_SWORD, List.of(Items.NETHERITE_SWORD), List.of(Items.NETHERITE_PICKAXE, Items.STONE_SWORD)));
		test(errors, () -> checkMiningLevel(NEEDS_STONE_SWORD, List.of(Items.STONE_SWORD, Items.IRON_SWORD), List.of(Items.STONE_PICKAXE, Items.WOODEN_SWORD)));
		test(errors, () -> checkMiningLevel(NEEDS_ANY_SWORD, List.of(Items.WOODEN_SWORD, FAKE_SWORD), List.of()));
		test(errors, () -> checkMiningLevel(NEEDS_SHEARS, List.of(Items.SHEARS, FAKE_SHEARS), List.of()));
		test(errors, () -> checkMiningLevel(NEEDS_NETHERITE_PICKAXE, List.of(Items.NETHERITE_PICKAXE), List.of(Items.DIAMOND_PICKAXE, Items.NETHERITE_AXE)));
		test(errors, () -> checkMiningLevel(Blocks.STONE, List.of(Items.WOODEN_PICKAXE, FAKE_PICKAXE), List.of(Items.STICK)));
		test(errors, () -> checkMiningLevel(NEEDS_AXE, List.of(Items.WOODEN_AXE, FAKE_AXE), List.of(Items.STICK)));
		test(errors, () -> checkMiningLevel(NEEDS_HOE, List.of(Items.WOODEN_HOE, FAKE_HOE), List.of(Items.STICK)));
		test(errors, () -> checkMiningLevel(NEEDS_SHOVEL, List.of(Items.WOODEN_SHOVEL, FAKE_SHOVEL), List.of(Items.STICK)));

		// Legacy blocks
		test(errors, () -> checkMiningLevel(NEEDS_SWORD_LEGACY, List.of(Items.WOODEN_SWORD, FAKE_SWORD), List.of()));
		test(errors, () -> checkMiningLevel(NEEDS_SHEARS_LEGACY, List.of(Items.SHEARS, FAKE_SHEARS), List.of()));
		test(errors, () -> checkMiningLevel(NEEDS_AXE_LEGACY, List.of(Items.WOODEN_AXE, FAKE_AXE), List.of()));
		test(errors, () -> checkMiningLevel(NEEDS_PICKAXE_LEGACY, List.of(Items.WOODEN_PICKAXE, FAKE_PICKAXE), List.of()));
		test(errors, () -> checkMiningLevel(NEEDS_HOE_LEGACY, List.of(Items.WOODEN_HOE, FAKE_HOE), List.of()));
		test(errors, () -> checkMiningLevel(NEEDS_SHOVEL_LEGACY, List.of(Items.WOODEN_SHOVEL, FAKE_SHOVEL), List.of()));

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
