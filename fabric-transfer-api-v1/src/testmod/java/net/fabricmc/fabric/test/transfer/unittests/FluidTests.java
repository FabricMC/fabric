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

package net.fabricmc.fabric.test.transfer.unittests;

import static net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;

import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

class FluidTests {
	public static void run() {
		testFluidStorage();
	}

	private static final FluidVariant TAGGED_WATER, TAGGED_WATER_2, WATER, LAVA;
	private static int finalCommitCount = 0;

	private static SingleSlotStorage<FluidVariant> createWaterStorage() {
		return new SingleVariantStorage<FluidVariant>() {
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
		CompoundTag tag = new CompoundTag();
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
			// Should allow insert, but without mutating the storage.
			if (waterStorage.simulateInsert(WATER, BUCKET, tx) != BUCKET) throw new AssertionError("Simulated insert failed");
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
			// Simulated extraction should succeed but do nothing
			if (waterStorage.simulateExtract(TAGGED_WATER, Long.MAX_VALUE, tx) != BUCKET) throw new AssertionError("Simulated extraction failed");
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
}
