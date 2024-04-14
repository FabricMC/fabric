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

import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

public class ContentRegistryGameTest {
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testCompostingChanceRegistry(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.COMPOSTER);
		ItemStack obsidian = new ItemStack(Items.OBSIDIAN, 64);
		// If on level 0, composting always increases composter level
		context.useStackOnBlock(context.createMockPlayer(GameMode.SURVIVAL), obsidian, pos, Direction.DOWN);
		context.expectBlockProperty(pos, ComposterBlock.LEVEL, 1);
		context.assertEquals(obsidian.getCount(), 63, "obsidian stack count");
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testFlattenableBlockRegistry(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.RED_WOOL);
		ItemStack shovel = new ItemStack(Items.NETHERITE_SHOVEL);
		context.useStackOnBlock(context.createMockPlayer(GameMode.SURVIVAL), shovel, pos, Direction.DOWN);
		context.expectBlock(Blocks.YELLOW_WOOL, pos);
		context.assertEquals(shovel.getDamage(), 1, "shovel damage");
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testFuelRegistry(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		// Use blast furnace to make it cook faster (100 ticks / 200 ticks)
		context.setBlockState(pos, Blocks.BLAST_FURNACE);

		if (context.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity furnace) {
			furnace.setStack(0, new ItemStack(Items.RAW_IRON, 1));
		} else {
			throw new AssertionError("Furnace was not placed");
		}

		// Ensure hopper inserts fuel to the furnace
		context.setBlockState(pos.east(), Blocks.HOPPER.getDefaultState().with(HopperBlock.FACING, Direction.WEST));

		if (context.getBlockEntity(pos.east()) instanceof HopperBlockEntity hopper) {
			// 100 ticks/1 smelted item worth of fuel.
			hopper.setStack(0, new ItemStack(Items.OBSIDIAN, 2));
			hopper.setStack(1, new ItemStack(Items.DIRT));
		} else {
			throw new AssertionError("Hopper was not placed");
		}

		// 1 tick for hopper to transfer, 100 ticks to cook
		context.createTimedTaskRunner().expectMinDurationAndRun(101, () -> {
			context.assertTrue(hopper.isEmpty(), "fuel hopper should have been emptied");
			context.assertTrue(ItemStack.areEqual(hopper.getStack(2), new ItemStack(Items.IRON_INGOT, 1)), "one iron ingot should have been smelted");
			context.complete();
		});
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testStrippableBlockRegistry(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.QUARTZ_PILLAR);
		ItemStack axe = new ItemStack(Items.NETHERITE_AXE);
		context.useStackOnBlock(context.createMockPlayer(GameMode.SURVIVAL), axe, pos, Direction.DOWN);
		context.expectBlock(Blocks.HAY_BLOCK, pos);
		context.assertEquals(axe.getDamage(), 1, "axe damage");
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testTillableBlockRegistry(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.GREEN_WOOL);
		ItemStack hoe = new ItemStack(Items.NETHERITE_HOE);
		context.useStackOnBlock(context.createMockPlayer(GameMode.SURVIVAL), hoe, pos, Direction.DOWN);
		context.expectBlock(Blocks.LIME_WOOL, pos);
		context.assertEquals(hoe.getDamage(), 1, "hoe damage");
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testOxidizableBlocksRegistry(TestContext context) {
		// Test de-oxidation. (the registry does not make the blocks oxidize.)
		PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.DIAMOND_ORE);
		ItemStack axe = new ItemStack(Items.NETHERITE_AXE);
		context.useStackOnBlock(player, axe, pos, Direction.DOWN);
		context.expectBlock(Blocks.GOLD_ORE, pos);
		context.assertEquals(axe.getDamage(), 1, "axe damage");
		context.useStackOnBlock(player, axe, pos, Direction.DOWN);
		context.expectBlock(Blocks.IRON_ORE, pos);
		context.useStackOnBlock(player, axe, pos, Direction.DOWN);
		context.expectBlock(Blocks.COPPER_ORE, pos);
		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testWaxableBlocksRegistry(TestContext context) {
		PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.DIAMOND_ORE);
		ItemStack honeycomb = new ItemStack(Items.HONEYCOMB, 64);
		context.useStackOnBlock(player, honeycomb, pos, Direction.DOWN);
		context.expectBlock(Blocks.DEEPSLATE_DIAMOND_ORE, pos);
		context.assertEquals(honeycomb.getCount(), 63, "honeycomb count");
		ItemStack axe = new ItemStack(Items.NETHERITE_AXE);
		context.useStackOnBlock(player, axe, pos, Direction.DOWN);
		context.expectBlock(Blocks.DIAMOND_ORE, pos);
		context.assertEquals(axe.getDamage(), 1, "axe damage");
		context.complete();
	}
}
