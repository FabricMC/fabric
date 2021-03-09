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
import java.util.WeakHashMap;

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
public class CauldronWrapper extends SnapshotParticipant<Integer> implements Storage<Fluid>, StorageView<Fluid> {
	private static final Map<WorldLocation, CauldronWrapper> WRAPPERS = new WeakHashMap<>();

	public static CauldronWrapper get(World world, BlockPos pos) {
		WorldLocation location = new WorldLocation(world, pos.toImmutable());
		WRAPPERS.computeIfAbsent(location, CauldronWrapper::new);
		return WRAPPERS.get(location);
	}

	private final WorldLocation location;

	CauldronWrapper(WorldLocation location) {
		this.location = location;
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
		}

		return 0;
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
	public boolean forEach(Visitor<Fluid> visitor, Transaction transaction) {
		if (amount() > 0) {
			return visitor.accept(this);
		} else {
			return false;
		}
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
			// TODO: what should we do? crash? warn?
		}
	}

	@Override
	public void onFinalCommit() {
		BlockState state = location.world.getBlockState(location.pos);

		// Only send the update if the cauldron is still there
		if (state.isOf(Blocks.CAULDRON)) {
			location.world.updateNeighbors(location.pos, null);
		}
	}
}
