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

package net.fabricmc.fabric.test.transfer.gametests;

import net.minecraft.block.Blocks;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.test.transfer.mixin.AbstractFurnaceBlockEntityAccessor;

public class VanillaStorageTests {
	/**
	 * Regression test for https://github.com/FabricMC/fabric/issues/1972.
	 * Ensures that furnace cook time is only reset when extraction is actually committed.
	 */
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testFurnaceCookTime(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.FURNACE.getDefaultState());
		FurnaceBlockEntity furnace = (FurnaceBlockEntity) context.getBlockEntity(pos);
		AbstractFurnaceBlockEntityAccessor accessor = (AbstractFurnaceBlockEntityAccessor) furnace;

		ItemVariant rawIron = ItemVariant.of(Items.RAW_IRON);
		furnace.setStack(0, rawIron.toStack(64));
		furnace.setStack(1, new ItemStack(Items.COAL, 64));
		InventoryStorage furnaceWrapper = InventoryStorage.of(furnace, null);

		context.runAtTick(5, () -> {
			if (accessor.getCookTime() <= 0) {
				throw new GameTestException("Furnace should have started cooking.");
			}

			try (Transaction transaction = Transaction.openOuter()) {
				if (furnaceWrapper.extract(rawIron, 64, transaction) != 64) {
					throw new GameTestException("Failed to extract 64 raw iron.");
				}
			}

			if (accessor.getCookTime() <= 0) {
				throw new GameTestException("Furnace should still cook after simulation.");
			}

			try (Transaction transaction = Transaction.openOuter()) {
				if (furnaceWrapper.extract(rawIron, 64, transaction) != 64) {
					throw new GameTestException("Failed to extract 64 raw iron.");
				}

				transaction.commit();
			}

			if (accessor.getCookTime() != 0) {
				throw new GameTestException("Furnace should have reset cook time after being emptied.");
			}

			context.complete();
		});
	}

	/**
	 * Tests that containers such as chests don't update adjacent comparators until the very end of a committed transaction.
	 */
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testChestComparator(TestContext context) {
		World world = context.getWorld();

		BlockPos pos = new BlockPos(0, 2, 0);
		context.setBlockState(pos, Blocks.CHEST.getDefaultState());
		ChestBlockEntity chest = (ChestBlockEntity) context.getBlockEntity(pos);
		InventoryStorage storage = InventoryStorage.of(chest, null);

		BlockPos comparatorPos = new BlockPos(1, 2, 0);
		// support block under the comparator
		context.setBlockState(comparatorPos.offset(Direction.DOWN), Blocks.GREEN_WOOL.getDefaultState());
		// comparator
		context.setBlockState(comparatorPos, Blocks.COMPARATOR.getDefaultState().with(ComparatorBlock.FACING, Direction.WEST));

		try (Transaction transaction = Transaction.openOuter()) {
			storage.insert(ItemVariant.of(Items.DIAMOND), 1000000, transaction);

			// uncommitted insert should not schedule an update
			if (world.getBlockTickScheduler().isQueued(context.getAbsolutePos(comparatorPos), Blocks.COMPARATOR)) {
				throw new GameTestException("Comparator should not have a tick scheduled.");
			}

			transaction.commit();

			// committed insert should schedule an update
			if (!world.getBlockTickScheduler().isQueued(context.getAbsolutePos(comparatorPos), Blocks.COMPARATOR)) {
				throw new GameTestException("Comparator should have a tick scheduled.");
			}
		}

		context.complete();
	}

	/**
	 * Tests that shulker boxes cannot be inserted into other shulker boxes.
	 */
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testShulkerNoInsert(TestContext context) {
		BlockPos pos = new BlockPos(0, 2, 0);
		context.setBlockState(pos, Blocks.SHULKER_BOX);
		ShulkerBoxBlockEntity shulker = (ShulkerBoxBlockEntity) context.getBlockEntity(pos);
		InventoryStorage storage = InventoryStorage.of(shulker, null);

		if (storage.simulateInsert(ItemVariant.of(Items.SHULKER_BOX), 1, null) > 0) {
			context.throwPositionedException("Expected shulker box to be rejected", pos);
		}

		context.complete();
	}

	/**
	 * {@link Inventory#isValid(int, ItemStack)} is supposed to be independent of the stack size.
	 * However, to limit some stackable inputs to a size of 1, brewing stands and furnaces don't follow this rule in all cases.
	 * This test ensures that the Transfer API works around this issue for furnaces.
	 */
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testBadFurnaceIsValid(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.FURNACE.getDefaultState());
		FurnaceBlockEntity furnace = (FurnaceBlockEntity) context.getBlockEntity(pos);
		InventoryStorage furnaceWrapper = InventoryStorage.of(furnace, null);

		try (Transaction tx = Transaction.openOuter()) {
			if (furnaceWrapper.getSlot(1).insert(ItemVariant.of(Items.BUCKET), 2, tx) != 1) {
				throw new GameTestException("Exactly 1 bucket should have been inserted");
			}
		}

		context.complete();
	}

	/**
	 * Same as {@link #testBadFurnaceIsValid(TestContext)}, but for brewing stands.
	 */
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testBadBrewingStandIsValid(TestContext context) {
		BlockPos pos = new BlockPos(0, 1, 0);
		context.setBlockState(pos, Blocks.BREWING_STAND.getDefaultState());
		BrewingStandBlockEntity brewingStand = (BrewingStandBlockEntity) context.getBlockEntity(pos);
		InventoryStorage brewingStandWrapper = InventoryStorage.of(brewingStand, null);

		try (Transaction tx = Transaction.openOuter()) {
			for (int bottleSlot = 0; bottleSlot < 3; ++bottleSlot) {
				if (brewingStandWrapper.getSlot(bottleSlot).insert(ItemVariant.of(Items.GLASS_BOTTLE), 2, tx) != 1) {
					throw new GameTestException("Exactly 1 glass bottle should have been inserted");
				}
			}

			if (brewingStandWrapper.getSlot(3).insert(ItemVariant.of(Items.REDSTONE), 2, tx) != 2) {
				throw new GameTestException("Brewing ingredient insertion should not be limited");
			}
		}

		context.complete();
	}
}
