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

import com.google.common.collect.MapMaker;

import com.google.common.primitives.Ints;
import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public class CauldronStorage extends SnapshotParticipant<BlockState> implements Storage<FluidKey>, StorageView<FluidKey> {
	private static final Map<WorldLocation, CauldronStorage> CAULDRONS = new MapMaker().concurrencyLevel(1).weakValues().makeMap();

	public static CauldronStorage get(World world, BlockPos pos) {
		WorldLocation location = new WorldLocation(world, pos.toImmutable());
		CAULDRONS.computeIfAbsent(location, CauldronStorage::new);
		return CAULDRONS.get(location);
	}

	private final WorldLocation location;
	// this is the last released snapshot, which means it's the first snapshot ever saved when onFinalCommit() is called.
	private BlockState lastReleasedSnapshot;

	CauldronStorage(WorldLocation location) {
		this.location = location;
	}

	@Override
	protected void releaseSnapshot(BlockState snapshot) {
		lastReleasedSnapshot = snapshot;
	}

	private void updateLevel(CauldronFluidContent data, int level, Transaction transaction) {
		updateSnapshots(transaction);
		BlockState newState = data.block().getDefaultState();

		if (data.levelProperty() != null) {
			newState = newState.with(data.levelProperty(), level);
		}

		location.world.setBlockState(location.pos, newState, 0);
	}

	@Override
	public long insert(FluidKey fluidKey, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(fluidKey, maxAmount);

		CauldronFluidContent insertData = CauldronFluidContent.getForFluid(fluidKey.getFluid());

		if (insertData != null) {
			int maxLevelsInserted = Ints.saturatedCast(maxAmount / insertData.amountPerLevel());

			if (amount() == 0) {
				// Currently empty, so we can accept any fluid.
				int levelsInserted = Math.min(maxLevelsInserted, insertData.maxLevel());

				if (levelsInserted > 0) {
					updateLevel(insertData, levelsInserted, transaction);
				}

				return levelsInserted * insertData.amountPerLevel();
			}

			CauldronFluidContent currentData = getData();

			if (fluidKey.isOf(currentData.fluid())) {
				// Otherwise we can only accept the same fluid as the current one.
				int currentLevel = currentData.currentLevel(createSnapshot());
				int levelsInserted = Math.min(maxLevelsInserted, currentData.maxLevel() - currentLevel);

				if (levelsInserted > 0) {
					updateLevel(currentData, currentLevel + levelsInserted, transaction);
				}

				return levelsInserted * currentData.amountPerLevel();
			}
		}

		return 0;
	}

	@Override
	public long extract(FluidKey fluidKey, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(fluidKey, maxAmount);

		CauldronFluidContent currentData = getData();

		if (fluidKey.isOf(currentData.fluid())) {
			int maxLevelsExtracted = Ints.saturatedCast(maxAmount / currentData.amountPerLevel());
			int currentLevel = currentData.currentLevel(createSnapshot());
			int levelsExtracted = Math.min(maxLevelsExtracted, currentLevel);

			if (levelsExtracted > 0) {
				if (levelsExtracted == currentLevel) {
					// Fully extract -> back to empty cauldron
					updateSnapshots(transaction);
					location.world.setBlockState(location.pos, Blocks.CAULDRON.getDefaultState(), 0);
				} else {
					// Otherwise just decrease levels
					updateLevel(currentData, currentLevel - levelsExtracted, transaction);
				}
			}

			return levelsExtracted * currentData.amountPerLevel();
		}

		return 0;
	}

	@Override
	public boolean isEmpty() {
		return resource().isEmpty();
	}

	@Override
	public FluidKey resource() {
		return FluidKey.of(getData().fluid());
	}

	@Override
	public long amount() {
		CauldronFluidContent data = getData();
		return data.currentLevel(createSnapshot()) * data.amountPerLevel();
	}

	@Override
	public long capacity() {
		CauldronFluidContent data = getData();
		return data.maxLevel() * data.amountPerLevel();
	}

	private CauldronFluidContent getData() {
		CauldronFluidContent data = CauldronFluidContent.getForBlock(createSnapshot().getBlock());

		if (data == null) {
			throw new IllegalStateException(); // not a cauldron!
		}

		return data;
	}

	@Override
	public Iterator<StorageView<FluidKey>> iterator(Transaction transaction) {
		return SingleViewIterator.create(this, transaction);
	}

	@Override
	public BlockState createSnapshot() {
		return location.world.getBlockState(location.pos);
	}

	@Override
	public void readSnapshot(BlockState savedState) {
		location.world.setBlockState(location.pos, savedState, 0);
	}

	@Override
	public void onFinalCommit() {
		BlockState state = createSnapshot();
		BlockState originalState = lastReleasedSnapshot;

		if (originalState != state) {
			// Revert change
			location.world.setBlockState(location.pos, originalState, 0);
			// Then do the actual change with normal block updates
			location.world.setBlockState(location.pos, state);
		}
	}
}
