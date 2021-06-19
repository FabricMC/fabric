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

package net.fabricmc.fabric.api.transfer.v1.fluid.base;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * A storage that can store a single fluid key at any given time.
 * Implementors should at least override {@link #getCapacity}, and probably {@link #markDirty} as well.
 *
 * <p>{@link #canInsert} and {@link #canExtract} can be used for more precise control over which fluids may be inserted or
 * extracted.
 * If one of these two functions is overridden to always return false, implementors may also wish to override
 * {@link #supportsInsertion} and/or {@link #supportsExtraction}.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public abstract class SingleFluidStorage extends SnapshotParticipant<ResourceAmount<FluidKey>> implements SingleSlotStorage<FluidKey> {
	public FluidKey fluidKey;
	public long amount;

	/**
	 * Implement if you want.
	 */
	protected void markDirty() {
	}

	/**
	 * @return {@code true} if the passed non-empty fluid key can be inserted, {@code false} otherwise.
	 */
	protected boolean canInsert(FluidKey fluidKey) {
		return true;
	}

	/**
	 * @return {@code true} if the passed non-empty fluid key can be extracted, {@code false} otherwise.
	 */
	protected boolean canExtract(FluidKey fluidKey) {
		return true;
	}

	/**
	 * @return The maximum capacity of this storage for the passed non-empty fluid key.
	 */
	protected abstract long getCapacity(FluidKey fluidKey);

	@Override
	public final boolean isEmpty() {
		return fluidKey.isEmpty();
	}

	@Override
	public final FluidKey resource() {
		return fluidKey;
	}

	@Override
	public final long amount() {
		return fluidKey.isEmpty() ? 0 : amount;
	}

	@Override
	public final long capacity() {
		if (isEmpty()) {
			return 0;
		} else {
			return getCapacity(fluidKey);
		}
	}

	@Override
	public final long insert(FluidKey insertedFluid, long maxAmount, Transaction transaction) {
		StoragePreconditions.notEmptyNotNegative(insertedFluid, maxAmount);

		if ((insertedFluid == fluidKey || fluidKey.isEmpty()) && canInsert(insertedFluid)) {
			long insertedAmount = Math.min(maxAmount, getCapacity(insertedFluid) - amount);

			if (insertedAmount > 0) {
				updateSnapshots(transaction);

				// Just in case.
				if (fluidKey.isEmpty()) {
					amount = 0;
				}

				amount += insertedAmount;
				fluidKey = insertedFluid;
			}

			return insertedAmount;
		}

		return 0;
	}

	@Override
	public final long extract(FluidKey extractedFluid, long maxAmount, Transaction transaction) {
		StoragePreconditions.notEmptyNotNegative(extractedFluid, maxAmount);

		if (extractedFluid == fluidKey && canExtract(extractedFluid)) {
			long extractedAmount = Math.min(maxAmount, amount);

			if (extractedAmount > 0) {
				updateSnapshots(transaction);
				amount -= extractedAmount;

				if (amount == 0) {
					fluidKey = FluidKey.empty();
				}
			}

			return extractedAmount;
		}

		return 0;
	}

	@Override
	protected final ResourceAmount<FluidKey> createSnapshot() {
		return new ResourceAmount<>(fluidKey, amount);
	}

	@Override
	protected final void readSnapshot(ResourceAmount<FluidKey> snapshot) {
		this.fluidKey = snapshot.resource();
		this.amount = snapshot.amount();
	}

	@Override
	protected final void onFinalCommit() {
		markDirty();
	}
}
