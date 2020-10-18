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

package net.fabricmc.fabric.test.tool.attribute;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

public class ToolAttributeTest implements ModInitializer {
	private static final float DEFAULT_BREAK_SPEED = 1.0F;
	private static final float TOOL_BREAK_SPEED = 10.0F;
	// A custom tool type, taters
	private static final Tag<Item> TATER = TagRegistry.item(new Identifier("fabric-tool-attribute-api-v1-testmod", "taters"));

	private boolean hasValidated = false;

	Block gravelBlock;
	Block stoneBlock;
	Item testShovel;
	Item testPickaxe;

	Item testStoneLevelTater;
	Item testStoneDynamicLevelTater;
	Item testDiamondLevelTater;
	Item testDiamondDynamicLevelTater;
	Block taterEffectiveBlock;

	@Override
	public void onInitialize() {
		// Register a custom shovel that has a mining level of 2 (iron) dynamically.
		testShovel = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_shovel"), new TestTool(new Item.Settings(), FabricToolTags.SHOVELS, 2));
		//Register a custom pickaxe that has a mining level of 2 (iron) dynamically.
		testPickaxe = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_pickaxe"), new TestTool(new Item.Settings(), FabricToolTags.PICKAXES, 2));
		// Register a block that requires a shovel that is as strong or stronger than an iron one.
		gravelBlock = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "hardened_gravel_block"),
				new Block(FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.SAND).build(), MaterialColor.STONE)
						.breakByTool(FabricToolTags.SHOVELS, 2)
						.requiresTool()
						.strength(0.6F)
						.sounds(BlockSoundGroup.GRAVEL)));
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "hardened_gravel_block"), new BlockItem(gravelBlock, new Item.Settings()));
		// Register a block that requires a pickaxe that is as strong or stronger than an iron one.
		stoneBlock = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "hardened_stone_block"),
				new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE)
						.breakByTool(FabricToolTags.PICKAXES, 2)
						.requiresTool()
						.strength(0.6F)
						.sounds(BlockSoundGroup.STONE)));
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "hardened_stone_block"), new BlockItem(stoneBlock, new Item.Settings()));

		// Register a tater that has a mining level of 1 (stone).
		testStoneLevelTater = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_stone_level_tater"), new ToolItem(ToolMaterials.STONE, new Item.Settings()));
		// Register a tater that has a mining level of 1 (stone) dynamically.
		testStoneDynamicLevelTater = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_stone_dynamic_level_tater"), new TestTool(new Item.Settings(), TATER, 1));
		//Register a tater that has a mining level of 3 (diamond).
		testDiamondLevelTater = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_diamond_level_tater"), new ToolItem(ToolMaterials.DIAMOND, new Item.Settings()));
		//Register a tater that has a mining level of 3 (diamond) dynamically.
		testDiamondDynamicLevelTater = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_diamond_dynamic_level_tater"), new TestTool(new Item.Settings(), TATER, 3));

		taterEffectiveBlock = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "tater_effective_block"),
				new Block(FabricBlockSettings.of(Material.ORGANIC_PRODUCT, MaterialColor.ORANGE)
						.breakByTool(TATER, 2) // requires iron tater
						.requiresTool()
						.strength(0.6F)
						.sounds(BlockSoundGroup.CROP)));
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "tater_effective_block"), new BlockItem(taterEffectiveBlock, new Item.Settings()));

		ServerTickEvents.START_SERVER_TICK.register(this::validate);
	}

	private void validate(MinecraftServer server) {
		if (hasValidated) {
			return;
		}

		hasValidated = true;

		if (FabricToolTags.PICKAXES.values().isEmpty()) {
			throw new AssertionError("Failed to load tool tags");
		}

		//Test we haven't broken vanilla behavior
		testToolOnBlock(new ItemStack(Items.STONE_PICKAXE), Blocks.GRAVEL, false, 1.0F);
		testToolOnBlock(new ItemStack(Items.IRON_PICKAXE), Blocks.STONE, true, ((ToolItem) Items.IRON_PICKAXE).getMaterial().getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(Items.IRON_PICKAXE), Blocks.OBSIDIAN, false, ((ToolItem) Items.IRON_PICKAXE).getMaterial().getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(Items.STONE_SHOVEL), Blocks.STONE, false, 1.0F);
		testToolOnBlock(new ItemStack(Items.STONE_SHOVEL), Blocks.GRAVEL, false, ((ToolItem) Items.STONE_SHOVEL).getMaterial().getMiningSpeedMultiplier());

		//Test vanilla tools don't bypass fabric mining levels
		testToolOnBlock(new ItemStack(Items.STONE_PICKAXE), stoneBlock, false, ((ToolItem) Items.STONE_PICKAXE).getMaterial().getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(Items.IRON_PICKAXE), stoneBlock, true, ((ToolItem) Items.IRON_PICKAXE).getMaterial().getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(Items.STONE_SHOVEL), gravelBlock, false, ((ToolItem) Items.STONE_SHOVEL).getMaterial().getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(Items.IRON_SHOVEL), gravelBlock, true, ((ToolItem) Items.IRON_SHOVEL).getMaterial().getMiningSpeedMultiplier());

		//Test vanilla tools respect fabric mining tags
		testToolOnBlock(new ItemStack(Items.IRON_PICKAXE), gravelBlock, false, DEFAULT_BREAK_SPEED);
		testToolOnBlock(new ItemStack(Items.IRON_SHOVEL), stoneBlock, false, DEFAULT_BREAK_SPEED);

		//Test dynamic tools don't bypass mining level
		testToolOnBlock(new ItemStack(testPickaxe), Blocks.OBSIDIAN, false, TOOL_BREAK_SPEED);

		//Test dynamic tools respect fabric mining tags
		testToolOnBlock(new ItemStack(testPickaxe), gravelBlock, false, DEFAULT_BREAK_SPEED);
		testToolOnBlock(new ItemStack(testShovel), stoneBlock, false, DEFAULT_BREAK_SPEED);

		//Test dynamic tools on vanilla blocks
		testToolOnBlock(new ItemStack(testShovel), Blocks.STONE, false, DEFAULT_BREAK_SPEED);
		testToolOnBlock(new ItemStack(testShovel), Blocks.GRAVEL, false, TOOL_BREAK_SPEED);
		testToolOnBlock(new ItemStack(testPickaxe), Blocks.GRAVEL, false, DEFAULT_BREAK_SPEED);
		testToolOnBlock(new ItemStack(testPickaxe), Blocks.STONE, true, TOOL_BREAK_SPEED);

		//Test taters respect our tater block
		testToolOnBlock(new ItemStack(testDiamondDynamicLevelTater), taterEffectiveBlock, true, TOOL_BREAK_SPEED);
		testToolOnBlock(new ItemStack(testDiamondLevelTater), taterEffectiveBlock, true, ToolMaterials.DIAMOND.getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(testStoneDynamicLevelTater), taterEffectiveBlock, false, TOOL_BREAK_SPEED);
		testToolOnBlock(new ItemStack(testStoneLevelTater), taterEffectiveBlock, false, ToolMaterials.STONE.getMiningSpeedMultiplier());

		//Test other tools on our tater block
		testToolOnBlock(new ItemStack(testPickaxe), taterEffectiveBlock, false, DEFAULT_BREAK_SPEED);
		testToolOnBlock(new ItemStack(testShovel), taterEffectiveBlock, false, DEFAULT_BREAK_SPEED);
		testToolOnBlock(new ItemStack(Items.IRON_PICKAXE), taterEffectiveBlock, false, DEFAULT_BREAK_SPEED);
		testToolOnBlock(new ItemStack(Items.IRON_SHOVEL), taterEffectiveBlock, false, DEFAULT_BREAK_SPEED);
	}

	private void testToolOnBlock(ItemStack item, Block block, boolean inEffective, float inSpeed) {
		boolean effective = item.isEffectiveOn(block.getDefaultState());
		float speed = item.getMiningSpeedMultiplier(block.getDefaultState());

		if (inEffective != effective) {
			throw new AssertionError("Effective check incorrect for " + Registry.ITEM.getId(item.getItem()) + " breaking " + Registry.BLOCK.getId(block) + " got " + effective);
		} else if (inSpeed != speed) {
			throw new AssertionError("Speed check incorrect for " + Registry.ITEM.getId(item.getItem()) + " breaking " + Registry.BLOCK.getId(block) + " got " + speed);
		}
	}

	private static class TestTool extends Item implements DynamicAttributeTool {
		final Tag<Item> toolType;
		final int miningLevel;

		private TestTool(Settings settings, Tag<Item> toolType, int miningLevel) {
			super(settings);
			this.toolType = toolType;
			this.miningLevel = miningLevel;
		}

		@Override
		public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (tag.equals(toolType)) {
				return this.miningLevel;
			}

			return 0;
		}

		@Override
		public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (tag.equals(toolType)) {
				return TOOL_BREAK_SPEED;
			}

			return DEFAULT_BREAK_SPEED;
		}
	}
}
