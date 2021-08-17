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

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.MapMaker;
import com.google.common.primitives.Ints;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * Standard implementation of {@code Storage<FluidVariant>}.
 *
 * <p>Implementation notes:
 * <ul>
 *     <li>To make sure multiple access to the same cauldron return the same wrapper, we maintain a {@code (World, BlockPos) -> Wrapper} cache.</li>
 *     <li>The wrapper mutates the world directly with setBlockState, but updates are suppressed.
 *     On final commit, a block update is sent by reverting to {@linkplain #lastReleasedSnapshot the initial block state} with updates suppressed,
 *     then setting the final block state again, without suppressing updates.</li>
 * </ul>
 */
public class CauldronStorage extends SnapshotParticipant<BlockState> implements SingleSlotStorage<FluidVariant> {
	// Record is used for convenient constructor, hashcode and equals implementations.
	private static final class WorldLocation {
		private final World world;
		private final BlockPos pos;

		WorldLocation(World world, BlockPos pos) {
			this.world = world;
			this.pos = pos;
		}

		public World world() {
			return world;
		}

		public BlockPos pos() {
			return pos;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			WorldLocation that = (WorldLocation) o;
			return Objects.equals(world, that.world) && Objects.equals(pos, that.pos);
		}

		@Override
		public int hashCode() {
			return Objects.hash(world, pos);
		}
	}

	// Weak values to make sure wrappers are cleaned up after use, thread-safe.
	private static final Map<WorldLocation, CauldronStorage> CAULDRONS = new MapMaker().concurrencyLevel(1).weakValues().makeMap();

	public static CauldronStorage get(World world, BlockPos pos) {
		WorldLocation location = new WorldLocation(world, pos.toImmutable());
		return CAULDRONS.computeIfAbsent(location, CauldronStorage::new);
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

	private int getLevel() {
		BlockState state = createSnapshot();

		if (!state.isOf(Blocks.CAULDRON)) {
			throw new IllegalStateException("Unexpected error: no cauldron at location " + location);
		}

		return state.get(CauldronBlock.LEVEL);
	}

	// Called by insert and extract to update the block state.
	private void updateLevel(int level, TransactionContext transaction) {
		updateSnapshots(transaction);
		BlockState newState = Blocks.CAULDRON.getDefaultState().with(CauldronBlock.LEVEL, level);

		// Set block state without updates.
		location.world.setBlockState(location.pos, newState, 0);
	}

	@Override
	public long insert(FluidVariant fluidVariant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(fluidVariant, maxAmount);

		if (fluidVariant.isOf(Fluids.WATER)) {
			int maxLevelsInserted = Ints.saturatedCast(maxAmount / FluidConstants.BOTTLE);
			int currentLevel = getLevel();
			int levelsInserted = Math.min(maxLevelsInserted, 3 - currentLevel);

			if (levelsInserted > 0) {
				updateLevel(currentLevel + levelsInserted, transaction);
			}

			return levelsInserted * FluidConstants.BOTTLE;
		}

		return 0;
	}

	@Override
	public long extract(FluidVariant fluidVariant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(fluidVariant, maxAmount);

		if (fluidVariant.isOf(Fluids.WATER)) {
			int maxLevelsExtracted = Ints.saturatedCast(maxAmount / FluidConstants.BOTTLE);
			int currentLevel = getLevel();
			int levelsExtracted = Math.min(maxLevelsExtracted, currentLevel);

			if (levelsExtracted > 0) {
				updateLevel(currentLevel - levelsExtracted, transaction);
			}

			return levelsExtracted * FluidConstants.BOTTLE;
		}

		return 0;
	}

	@Override
	public boolean isResourceBlank() {
		return getResource().isBlank();
	}

	@Override
	public FluidVariant getResource() {
		return FluidVariant.of(Fluids.WATER);
	}

	@Override
	public long getAmount() {
		return getLevel() * FluidConstants.BOTTLE;
	}

	@Override
	public long getCapacity() {
		return FluidConstants.BUCKET;
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
