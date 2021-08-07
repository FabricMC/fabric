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

package net.fabricmc.fabric.test.transfer.fluid;

import static net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class FluidTransferTest implements ModInitializer {
	public static final String MOD_ID = "fabric-transfer-api-v1-testmod";

	private static final Block INFINITE_WATER_SOURCE = new Block(AbstractBlock.Settings.of(Material.METAL));
	private static final Block INFINITE_LAVA_SOURCE = new Block(AbstractBlock.Settings.of(Material.METAL));
	private static final Block FLUID_CHUTE = new FluidChuteBlock();
	private static final Item EXTRACT_STICK = new ExtractStickItem();
	public static BlockEntityType<FluidChuteBlockEntity> FLUID_CHUTE_TYPE;

	@Override
	public void onInitialize() {
		registerBlock(INFINITE_WATER_SOURCE, "infinite_water_source");
		registerBlock(INFINITE_LAVA_SOURCE, "infinite_lava_source");
		registerBlock(FLUID_CHUTE, "fluid_chute");
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "extract_stick"), EXTRACT_STICK);

		FLUID_CHUTE_TYPE = FabricBlockEntityTypeBuilder.create(FluidChuteBlockEntity::new, FLUID_CHUTE).build();
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "fluid_chute"), FLUID_CHUTE_TYPE);

		FluidStorage.SIDED.registerForBlocks((world, pos, state, be, direction) -> CreativeFluidStorage.WATER, INFINITE_WATER_SOURCE);
		FluidStorage.SIDED.registerForBlocks((world, pos, state, be, direction) -> CreativeFluidStorage.LAVA, INFINITE_LAVA_SOURCE);

		testFluidStorage();
		testTransactionExceptions();
		ItemTests.run();
		FluidItemTests.run();
	}

	private static void registerBlock(Block block, String name) {
		Identifier id = new Identifier(MOD_ID, name);
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
	}

	private static final FluidVariant TAGGED_WATER, TAGGED_WATER_2, WATER, LAVA;
	private static int finalCommitCount = 0;

	private static SingleSlotStorage<FluidVariant> createWaterStorage() {
		return new SingleVariantStorage<>() {
			@Override
			protected FluidVariant getBlankVariant() {
				return FluidVariant.blank();
			}

			@Override
			protected long getCapacity(FluidVariant fluidVariant) {
				return BUCKET * 2;
			}

			@Override
			protected boolean canInsert(FluidVariant fluidVariant) {
				return fluidVariant.isOf(Fluids.WATER);
			}

			@Override
			protected void onFinalCommit() {
				finalCommitCount++;
			}
		};
	}

	static {
		NbtCompound tag = new NbtCompound();
		tag.putInt("test", 1);
		TAGGED_WATER = FluidVariant.of(Fluids.WATER, tag);
		TAGGED_WATER_2 = FluidVariant.of(Fluids.WATER, tag);
		WATER = FluidVariant.of(Fluids.WATER);
		LAVA = FluidVariant.of(Fluids.LAVA);
	}

	private static void testFluidStorage() {
		SingleSlotStorage<FluidVariant> waterStorage = createWaterStorage();

		// Test content
		if (!waterStorage.isResourceBlank()) throw new AssertionError("Should have been blank");

		// Test some insertions
		try (Transaction tx = Transaction.openOuter()) {
			// Should not allow lava (canInsert returns false)
			if (waterStorage.insert(LAVA, BUCKET, tx) != 0) throw new AssertionError("Lava inserted");
			// Should allow insert
			if (waterStorage.insert(TAGGED_WATER, BUCKET, tx) != BUCKET) throw new AssertionError("Tagged water insert 1 failed");
			// Variants are different, should not allow insert
			if (waterStorage.insert(WATER, BUCKET, tx) != 0) throw new AssertionError("Water inserted");
			// Should allow insert again even if the variant is different cause they are equal
			if (waterStorage.insert(TAGGED_WATER_2, BUCKET, tx) != BUCKET) throw new AssertionError("Tagged water insert 2 failed");
			// Should not allow further insertion because the storage is full
			if (waterStorage.insert(TAGGED_WATER, BUCKET, tx) != 0) throw new AssertionError("Storage full, yet something was inserted");
			// Should allow extraction
			if (waterStorage.extract(TAGGED_WATER_2, BUCKET, tx) != BUCKET) throw new AssertionError("Extraction failed");
			// Re-insert
			if (waterStorage.insert(TAGGED_WATER_2, BUCKET, tx) != BUCKET) throw new AssertionError("Tagged water insert 3 failed");
			// Test contents
			if (waterStorage.getAmount() != BUCKET * 2 || !waterStorage.getResource().equals(TAGGED_WATER_2)) throw new AssertionError("Contents are wrong");
			// No commit -> will abort
		}

		// Test content again to make sure the rollback worked as expected
		if (!waterStorage.isResourceBlank()) throw new AssertionError("Should have been blank");

		// Test highly nested commit
		try (Transaction tx = Transaction.openOuter()) {
			if (waterStorage.getAmount() != 0) throw new AssertionError("Initial amount is wrong");
			if (waterStorage.insert(WATER, BUCKET, tx) != BUCKET) throw new AssertionError("Water insertion failed");

			try (Transaction nested1 = tx.openNested()) {
				try (Transaction nested2 = nested1.openNested()) {
					if (waterStorage.insert(WATER, BUCKET, nested2) != BUCKET) throw new AssertionError("Nested insertion failed");
					if (waterStorage.getAmount() != 2 * BUCKET) throw new AssertionError("Two buckets have been inserted");
					nested2.commit();
				}

				if (waterStorage.getAmount() != 2 * BUCKET) throw new AssertionError("Nested no 1 was committed, so we should still have two buckets");
				nested1.commit();
			}

			if (waterStorage.getAmount() != 2 * BUCKET) throw new AssertionError("Nested no 1 was committed, so we should still have two buckets");
		}

		if (waterStorage.getAmount() != 0) throw new AssertionError("Amount should have been reverted to zero");

		// Test nested commit to make sure it behaves as expected

		// Without outer commit
		insertWaterWithNesting(waterStorage, false);
		if (waterStorage.getAmount() != 0) throw new AssertionError("Amount should have been reverted to zero");
		if (finalCommitCount != 0) throw new AssertionError("Nothing should have called onFinalCommit() yet (no outer commit)");

		// With outer commit
		insertWaterWithNesting(waterStorage, true);
		if (waterStorage.getAmount() != 2 * BUCKET) throw new AssertionError("Outer was committed, so we should still have two buckets");
		if (finalCommitCount != 1) throw new AssertionError("onFinalCommit() should have been called exactly once.");
	}

	private static void insertWaterWithNesting(SingleSlotStorage<FluidVariant> waterStorage, boolean doOuterCommit) {
		try (Transaction tx = Transaction.openOuter()) {
			if (waterStorage.getAmount() != 0) throw new AssertionError("Initial amount is wrong");
			if (waterStorage.insert(WATER, BUCKET, tx) != BUCKET) throw new AssertionError("Water insertion failed");

			try (Transaction nested = tx.openNested()) {
				if (waterStorage.insert(WATER, BUCKET, nested) != BUCKET) throw new AssertionError("Nested insertion failed");
				if (waterStorage.getAmount() != 2 * BUCKET) throw new AssertionError("Two buckets have been inserted");
				nested.commit();
			}

			if (waterStorage.getAmount() != 2 * BUCKET) throw new AssertionError("Nested was committed, so we should still have two buckets");

			if (doOuterCommit) {
				tx.commit();
			}
		}
	}

	private static int callbacksInvoked = 0;

	/**
	 * Make sure that transaction global state stays valid in case of exceptions.
	 */
	private static void testTransactionExceptions() {
		// Test exception inside the try.
		ensureException(() -> {
			try (Transaction tx = Transaction.openOuter()) {
				tx.addCloseCallback((t, result) -> {
					callbacksInvoked++; throw new RuntimeException("Close.");
				});
				throw new RuntimeException("Inside try.");
			}
		}, "Exception should have propagated through the transaction.");
		if (callbacksInvoked != 1) throw new AssertionError("Callback should have been invoked.");

		// Test exception inside the close.
		callbacksInvoked = 0;
		ensureException(() -> {
			try (Transaction tx = Transaction.openOuter()) {
				tx.addCloseCallback((t, result) -> {
					callbacksInvoked++; throw new RuntimeException("Close 1.");
				});
				tx.addCloseCallback((t, result) -> {
					callbacksInvoked++; throw new RuntimeException("Close 2.");
				});
				tx.addOuterCloseCallback(result -> {
					callbacksInvoked++; throw new RuntimeException("Outer close 1.");
				});
				tx.addOuterCloseCallback(result -> {
					callbacksInvoked++; throw new RuntimeException("Outer close 2.");
				});
			}
		}, "Exceptions in close callbacks should be propagated through the transaction.");
		if (callbacksInvoked != 4) throw new AssertionError("All 4 callbacks should have been invoked, only so many were: " + callbacksInvoked);

		// Test that transaction state is still OK after these exceptions.
		try (Transaction tx = Transaction.openOuter()) {
			tx.commit();
		}
	}

	private static void ensureException(Runnable runnable, String message) {
		boolean failed = false;

		try {
			runnable.run();
		} catch (Throwable t) {
			failed = true;
		}

		if (!failed) {
			throw new AssertionError(message);
		}
	}
}
