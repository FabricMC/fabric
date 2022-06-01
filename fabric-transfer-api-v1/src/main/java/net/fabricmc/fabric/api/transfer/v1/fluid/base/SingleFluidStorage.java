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

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * @deprecated Superseded by {@link SingleVariantStorage}. Will be removed in a future iteration of the API.
 */
@ApiStatus.Experimental
@ApiStatus.ScheduledForRemoval
@Deprecated
public abstract class SingleFluidStorage extends SnapshotParticipant<ResourceAmount<FluidVariant>> implements SingleSlotStorage<FluidVariant> {
	public FluidVariant fluidVariant = FluidVariant.blank();
	public long amount;

	/**
	 * Implement if you want.
	 */
	protected void markDirty() {
	}

	/**
	 * @return {@code true} if the passed non-blank fluid variant can be inserted, {@code false} otherwise.
	 */
	protected boolean canInsert(FluidVariant fluidVariant) {
		return true;
	}

	/**
	 * @return {@code true} if the passed non-blank fluid variant can be extracted, {@code false} otherwise.
	 */
	protected boolean canExtract(FluidVariant fluidVariant) {
		return true;
	}

	/**
	 * @return The maximum capacity of this storage for the passed fluid variant.
	 * If the passed fluid variant is blank, an estimate should be returned.
	 */
	protected abstract long getCapacity(FluidVariant fluidVariant);

	@Override
	public final boolean isResourceBlank() {
		return fluidVariant.isBlank();
	}

	@Override
	public final FluidVariant getResource() {
		return fluidVariant;
	}

	@Override
	public final long getAmount() {
		return fluidVariant.isBlank() ? 0 : amount;
	}

	@Override
	public final long getCapacity() {
		return getCapacity(fluidVariant);
	}

	@Override
	public final long insert(FluidVariant insertedFluid, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(insertedFluid, maxAmount);

		if ((insertedFluid.equals(fluidVariant) || fluidVariant.isBlank()) && canInsert(insertedFluid)) {
			long insertedAmount = Math.min(maxAmount, getCapacity(insertedFluid) - amount);

			if (insertedAmount > 0) {
				updateSnapshots(transaction);

				// Just in case.
				if (fluidVariant.isBlank()) {
					amount = 0;
				}

				amount += insertedAmount;
				fluidVariant = insertedFluid;
			}

			return insertedAmount;
		}

		return 0;
	}

	@Override
	public final long extract(FluidVariant extractedFluid, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(extractedFluid, maxAmount);

		if (extractedFluid.equals(fluidVariant) && canExtract(extractedFluid)) {
			long extractedAmount = Math.min(maxAmount, amount);

			if (extractedAmount > 0) {
				updateSnapshots(transaction);
				amount -= extractedAmount;

				if (amount == 0) {
					fluidVariant = FluidVariant.blank();
				}
			}

			return extractedAmount;
		}

		return 0;
	}

	@Override
	protected final ResourceAmount<FluidVariant> createSnapshot() {
		return new ResourceAmount<>(fluidVariant, amount);
	}

	@Override
	protected final void readSnapshot(ResourceAmount<FluidVariant> snapshot) {
		this.fluidVariant = snapshot.resource();
		this.amount = snapshot.amount();
	}

	@Override
	protected final void onFinalCommit() {
		markDirty();
	}
}
