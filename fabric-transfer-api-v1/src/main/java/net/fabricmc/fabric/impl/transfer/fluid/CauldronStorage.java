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

package net.fabricmc.fabric.impl.transfer.fluid;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.collect.MapMaker;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

// Maintainer note: this will need updating for 1.17 to allow registering modded cauldrons.
public class CauldronStorage extends SnapshotParticipant<Integer> implements Storage<Fluid>, StorageView<Fluid> {
	private static final Map<WorldLocation, CauldronStorage> CAULDRONS = new MapMaker().concurrencyLevel(1).weakValues().makeMap();

	public static CauldronStorage get(World world, BlockPos pos) {
		WorldLocation location = new WorldLocation(world, pos.toImmutable());
		CAULDRONS.computeIfAbsent(location, CauldronStorage::new);
		return CAULDRONS.get(location);
	}

	private final WorldLocation location;
	// this is the last released snapshot, which means it's the first snapshot ever saved when onFinalCommit() is called.
	private int lastReleasedSnapshot;
	// True when an iterator is active.
	private boolean iterating = false;

	CauldronStorage(WorldLocation location) {
		this.location = location;
	}

	@Override
	protected void releaseSnapshot(Integer snapshot) {
		lastReleasedSnapshot = snapshot;
	}

	@Override
	public long insert(Fluid fluid, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(fluid, maxAmount);

		BlockState state = location.world.getBlockState(location.pos);

		if (state.isOf(Blocks.CAULDRON)) {
			int level = state.get(CauldronBlock.LEVEL);
			int levelsInserted = (int) Math.min(maxAmount / FluidConstants.BOTTLE, 3 - level);

			if (levelsInserted > 0) {
				updateSnapshots(transaction);
				location.world.setBlockState(location.pos, state.with(CauldronBlock.LEVEL, level + levelsInserted), 0);
			}

			return levelsInserted * FluidConstants.BOTTLE;
		}

		return 0;
	}

	@Override
	public long extract(Fluid fluid, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(fluid, maxAmount);

		BlockState state = location.world.getBlockState(location.pos);

		if (state.isOf(Blocks.CAULDRON)) {
			int level = state.get(CauldronBlock.LEVEL);
			int levelsExtracted = (int) Math.min(maxAmount / FluidConstants.BOTTLE, level);

			if (levelsExtracted > 0) {
				updateSnapshots(transaction);
				location.world.setBlockState(location.pos, state.with(CauldronBlock.LEVEL, level - levelsExtracted), 0);
			}

			return levelsExtracted * FluidConstants.BOTTLE;
		}

		return 0;
	}

	@Override
	public boolean isEmpty() {
		return amount() > 0;
	}

	@Override
	public Fluid resource() {
		return Fluids.WATER;
	}

	@Override
	public long amount() {
		BlockState state = location.world.getBlockState(location.pos);

		if (state.isOf(Blocks.CAULDRON)) {
			return state.get(CauldronBlock.LEVEL) * FluidConstants.BOTTLE;
		} else {
			return 0;
		}
	}

	@Override
	public long capacity() {
		return FluidConstants.BUCKET;
	}

	@Override
	public Iterator<StorageView<Fluid>> iterator(Transaction transaction) {
		if (iterating) {
			throw new IllegalStateException("An iterator is already active for this storage.");
		}

		iterating = true;
		CauldronIterator iterator = new CauldronIterator();
		transaction.addCloseCallback(iterator);
		return iterator;
	}

	@Override
	public Integer createSnapshot() {
		return (int) (amount() / FluidConstants.BOTTLE);
	}

	@Override
	public void readSnapshot(Integer savedLevel) {
		BlockState state = location.world.getBlockState(location.pos);

		if (state.isOf(Blocks.CAULDRON)) {
			location.world.setBlockState(location.pos, state.with(CauldronBlock.LEVEL, savedLevel), 0);
		} else {
			String errorMessage = String.format(
					"Expected block state at position %s in world %s to be a cauldron, but it's %s instead.",
					location.pos,
					location.world.getRegistryKey(),
					state);
			throw new RuntimeException(errorMessage);
		}
	}

	@Override
	public void onFinalCommit() {
		BlockState state = location.world.getBlockState(location.pos);
		BlockState originalState = state.with(CauldronBlock.LEVEL, lastReleasedSnapshot);

		// Only send the update if the cauldron is still there
		if (state.isOf(Blocks.CAULDRON) && originalState != state) {
			// Revert change
			location.world.setBlockState(location.pos, originalState, 0);
			// Then do the actual change with normal block updates
			location.world.setBlockState(location.pos, state);
		}
	}

	private class CauldronIterator implements Iterator<StorageView<Fluid>>, Transaction.CloseCallback {
		boolean open = true;
		boolean hasNext = true;

		@Override
		public boolean hasNext() {
			return open && hasNext && amount() > 0;
		}

		@Override
		public StorageView<Fluid> next() {
			if (!open) {
				throw new NoSuchElementException("The transaction for this iterator was closed.");
			}

			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			hasNext = false;
			return CauldronStorage.this;
		}

		@Override
		public void onClose(Transaction transaction, Transaction.Result result) {
			open = false;
			iterating = false;
		}
	}
}
