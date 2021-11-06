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

import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
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
import net.fabricmc.fabric.test.tool.attribute.item.TestDynamicCancelItem;
import net.fabricmc.fabric.test.tool.attribute.item.TestDynamicSwordItem;
import net.fabricmc.fabric.test.tool.attribute.item.TestDynamicToolItem;
import net.fabricmc.fabric.test.tool.attribute.item.TestNullableItem;

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
	Item testSword;

	Item testStoneLevelTater;
	Item testStoneDynamicLevelTater;
	Item testDiamondLevelTater;
	Item testDiamondDynamicLevelTater;
	Block taterEffectiveBlock;

	// Simple blocks that only need a tool without a specific mining level (legacy technique using block settings)
	Block needsShears;
	Block needsSword;
	Block needsPickaxe;
	Block needsAxe;
	Block needsHoe;
	Block needsShovel;

	// Simple blocks that only need a tool without a specific mining level (mineable tags)
	Block needsShearsTagged;
	Block needsSwordTagged;
	Block needsPickaxeTagged;
	Block needsAxeTagged;
	Block needsHoeTagged;
	Block needsShovelTagged;

	// These items are only tagged, but are not actual ToolItems or DynamicAttributeTools.
	Item fakeShears;
	Item fakeSword;
	Item fakePickaxe;
	Item fakeAxe;
	Item fakeHoe;
	Item fakeShovel;

	@Override
	public void onInitialize() {
		// Register a custom shovel that has a mining level of 2 (iron) dynamically.
		testShovel = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_shovel"), new TestTool(new Item.Settings(), FabricToolTags.SHOVELS, 2));
		//Register a custom pickaxe that has a mining level of 2 (iron) dynamically.
		testPickaxe = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_pickaxe"), new TestTool(new Item.Settings(), FabricToolTags.PICKAXES, 2));
		//Register a custom sword that has a mining level of 2 (iron) dynamically.
		testSword = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "test_sword"), new TestTool(new Item.Settings(), FabricToolTags.SWORDS, 2));
		// Register a block that requires a shovel that is as strong or stronger than an iron one.
		gravelBlock = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "hardened_gravel_block"),
				new Block(FabricBlockSettings.of(new FabricMaterialBuilder(MapColor.PALE_YELLOW).build(), MapColor.STONE_GRAY)
						.breakByTool(FabricToolTags.SHOVELS, 2)
						.requiresTool()
						.strength(0.6F)
						.sounds(BlockSoundGroup.GRAVEL)));
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "hardened_gravel_block"), new BlockItem(gravelBlock, new Item.Settings()));
		// Register a block that requires a pickaxe that is as strong or stronger than an iron one.
		stoneBlock = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "hardened_stone_block"),
				new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY)
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
				new Block(FabricBlockSettings.of(Material.ORGANIC_PRODUCT, MapColor.ORANGE)
						.breakByTool(TATER, 2) // requires iron tater
						.requiresTool()
						.strength(0.6F)
						.sounds(BlockSoundGroup.CROP)));
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "tater_effective_block"), new BlockItem(taterEffectiveBlock, new Item.Settings()));

		// DYNAMIC ATTRIBUTE MODIFIERS
		// The Dynamic Sword tests to make sure standard vanilla attributes can co-exist with dynamic attributes.
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "dynamic_sword"), new TestDynamicSwordItem(new Item.Settings()));
		// The Dynamic Tool ensures a tool can have dynamic attributes (with no vanilla atributes). It applies 2 layers of speed reduction to the player.
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "dynamic_tool"), new TestDynamicToolItem(new Item.Settings()));
		// Test cancels-out attributes
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "cancel_item"), new TestDynamicCancelItem(new Item.Settings()));
		// Test parameter nullability
		Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "null_test"), new TestNullableItem(new Item.Settings()));

		needsShears = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_shears"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1).breakByTool(FabricToolTags.SHEARS)));
		needsSword = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_sword"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1).breakByTool(FabricToolTags.SWORDS)));
		needsPickaxe = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_pickaxe"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1).breakByTool(FabricToolTags.PICKAXES)));
		needsAxe = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_axe"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1).breakByTool(FabricToolTags.AXES)));
		needsHoe = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_hoe"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1).breakByTool(FabricToolTags.HOES)));
		needsShovel = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_shovel"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1).breakByTool(FabricToolTags.SHOVELS)));

		needsShearsTagged = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_shears_tagged"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1)));
		needsSwordTagged = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_sword_tagged"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1)));
		needsPickaxeTagged = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_pickaxe_tagged"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1)));
		needsAxeTagged = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_axe_tagged"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1)));
		needsHoeTagged = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_hoe_tagged"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1)));
		needsShovelTagged = Registry.register(Registry.BLOCK, new Identifier("fabric-tool-attribute-api-v1-testmod", "needs_shovel_tagged"), new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1, 1)));

		// "Fake" tools, see explanation above
		fakeShears = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "fake_shears"), new Item(new Item.Settings()));
		fakeSword = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "fake_sword"), new Item(new Item.Settings()));
		fakePickaxe = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "fake_pickaxe"), new Item(new Item.Settings()));
		fakeAxe = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "fake_axe"), new Item(new Item.Settings()));
		fakeHoe = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "fake_hoe"), new Item(new Item.Settings()));
		fakeShovel = Registry.register(Registry.ITEM, new Identifier("fabric-tool-attribute-api-v1-testmod", "fake_shovel"), new Item(new Item.Settings()));

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
		testToolOnBlock(new ItemStack(Items.STONE_SHOVEL), Blocks.GRAVEL, true, ((ToolItem) Items.STONE_SHOVEL).getMaterial().getMiningSpeedMultiplier());

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
		testToolOnBlock(new ItemStack(testShovel), Blocks.GRAVEL, true, TOOL_BREAK_SPEED);
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

		//Test vanilla tools on blocks
		testToolOnBlock(new ItemStack(Items.SHEARS), needsShears, true, DEFAULT_BREAK_SPEED);
		testToolOnBlock(new ItemStack(Items.IRON_SWORD), needsSword, true, ToolMaterials.IRON.getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(Items.IRON_AXE), needsAxe, true, ToolMaterials.IRON.getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(Items.IRON_PICKAXE), needsPickaxe, true, ToolMaterials.IRON.getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(Items.IRON_HOE), needsHoe, true, ToolMaterials.IRON.getMiningSpeedMultiplier());
		testToolOnBlock(new ItemStack(Items.IRON_SHOVEL), needsShovel, true, ToolMaterials.IRON.getMiningSpeedMultiplier());

		//Test fake tools on corresponding and invalid blocks
		// Note: using LinkedHashMultimap to ensure the same order (this makes it more predictable when debugging)
		Multimap<Item, Block> fakeToolsToEffectiveBlocks = LinkedHashMultimap.create(6, 2);
		fakeToolsToEffectiveBlocks.put(fakeShears, needsShears);
		fakeToolsToEffectiveBlocks.put(fakeShears, needsShearsTagged);
		fakeToolsToEffectiveBlocks.put(fakeSword, needsSword);
		fakeToolsToEffectiveBlocks.put(fakeSword, needsSwordTagged);
		fakeToolsToEffectiveBlocks.put(fakeAxe, needsAxe);
		fakeToolsToEffectiveBlocks.put(fakeAxe, needsAxeTagged);
		fakeToolsToEffectiveBlocks.put(fakePickaxe, needsPickaxe);
		fakeToolsToEffectiveBlocks.put(fakePickaxe, needsPickaxeTagged);
		fakeToolsToEffectiveBlocks.put(fakeHoe, needsHoe);
		fakeToolsToEffectiveBlocks.put(fakeHoe, needsHoeTagged);
		fakeToolsToEffectiveBlocks.put(fakeShovel, needsShovel);
		fakeToolsToEffectiveBlocks.put(fakeShovel, needsShovelTagged);
		testExclusivelyEffective(fakeToolsToEffectiveBlocks, (tool, block) -> {
			if (tool == fakeShears && block == needsShearsTagged) {
				// The mining level API gives the tagged block the speed 5.0
				// when mined with shears (see ShearsItemMixin in that module),
				// and ShearsVanillaBlocksToolHandler gets the speeds from the vanilla shears item.
				return 5.0f;
			}

			return DEFAULT_BREAK_SPEED;
		});

		//Test fake tools on corresponding and invalid blocks
		Multimap<Item, Block> dynamicToolsToEffectiveBlocks = LinkedHashMultimap.create(3, 2);
		dynamicToolsToEffectiveBlocks.put(testSword, needsSword);
		dynamicToolsToEffectiveBlocks.put(testSword, needsSwordTagged);
		dynamicToolsToEffectiveBlocks.put(testPickaxe, needsPickaxe);
		dynamicToolsToEffectiveBlocks.put(testPickaxe, needsPickaxeTagged);
		dynamicToolsToEffectiveBlocks.put(testShovel, needsShovel);
		dynamicToolsToEffectiveBlocks.put(testShovel, needsShovelTagged);
		testExclusivelyEffective(dynamicToolsToEffectiveBlocks, (tool, block) -> TOOL_BREAK_SPEED);
	}

	private void testExclusivelyEffective(Multimap<Item, Block> itemsToEffectiveBlocks, BiFunction<Item, Block, Float> effectiveSpeed) {
		for (List<ItemConvertible> pair : Sets.cartesianProduct(itemsToEffectiveBlocks.keySet(), new HashSet<>(itemsToEffectiveBlocks.values()))) {
			Item item = (Item) pair.get(0);
			Block block = (Block) pair.get(1);

			if (itemsToEffectiveBlocks.get(item).contains(block)) {
				testToolOnBlock(new ItemStack(item), block, true, effectiveSpeed.apply(item, block));
			} else {
				testToolOnBlock(new ItemStack(item), block, false, DEFAULT_BREAK_SPEED);
			}
		}
	}

	private void testToolOnBlock(ItemStack item, Block block, boolean inEffective, float inSpeed) {
		boolean effective = item.isSuitableFor(block.getDefaultState());
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
