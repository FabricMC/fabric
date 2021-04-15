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

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * A storage that can store a single fluid at any given time.
 * Implementors should at least override {@link #getCapacity}, and probably {@link #markDirty} as well.
 *
 * <p>{@link #canInsert} and {@link #canExtract} can be used for more precise control over which fluids may be inserted or
 * extracted.
 * If one of these two functions is overridden to always return false, implementors may also wish to override
 * {@link #supportsInsertion} and/or {@link #supportsExtraction}.
 */
public abstract class SingleFluidStorage extends SnapshotParticipant<SingleFluidStorage.State> implements Storage<Fluid>, StorageView<Fluid> {
	public Fluid fluid;
	public long amount;
	// Current version of the storage.
	private int version = 0;
	// True when an iterator is active.
	private boolean iterating = false;

	/**
	 * Implement if you want.
	 */
	protected void markDirty() {
	}

	/**
	 * @return True if the passed non-empty fluid can be inserted, false otherwise.
	 */
	protected boolean canInsert(Fluid fluid) {
		return true;
	}

	/**
	 * @return True if the passed non-empty fluid can be extracted, false otherwise.
	 */
	protected boolean canExtract(Fluid fluid) {
		return true;
	}

	/**
	 * @return The maximum capacity of this storage for the passed non-empty fluid.
	 */
	protected abstract long getCapacity(Fluid fluid);

	@Override
	public final Fluid resource() {
		return fluid;
	}

	@Override
	public final long amount() {
		return fluid == Fluids.EMPTY ? 0 : amount;
	}

	@Override
	public final long insert(Fluid insertedFluid, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(insertedFluid, maxAmount);

		if ((insertedFluid == fluid || fluid == Fluids.EMPTY) && canInsert(insertedFluid)) {
			long insertedAmount = Math.min(maxAmount, getCapacity(insertedFluid) - amount);

			if (insertedAmount > 0) {
				updateSnapshots(transaction);

				// Just in case.
				if (fluid == Fluids.EMPTY) {
					amount = 0;
				}

				amount += insertedAmount;
				fluid = insertedFluid;
			}

			return insertedAmount;
		}

		return 0;
	}

	@Override
	public final long extract(Fluid extractedFluid, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(extractedFluid, maxAmount);

		if (extractedFluid == fluid && canExtract(extractedFluid)) {
			long extractedAmount = Math.min(maxAmount, amount);

			if (extractedAmount > 0) {
				updateSnapshots(transaction);
				amount -= extractedAmount;

				if (amount == 0) {
					fluid = Fluids.EMPTY;
				}
			}

			return extractedAmount;
		}

		return 0;
	}

	@Override
	public Iterator<StorageView<Fluid>> iterator(Transaction transaction) {
		if (iterating) {
			throw new IllegalStateException("An iterator is already active for this storage.");
		}

		iterating = true;
		return new SingleFluidIterator();
	}

	@Override
	public final int getVersion() {
		return version;
	}

	@Override
	protected final State createSnapshot() {
		return new State(fluid, amount);
	}

	@Override
	protected final void readSnapshot(State snapshot) {
		this.fluid = snapshot.fluid;
		this.amount = snapshot.amount;
	}

	@Override
	protected final void onFinalCommit() {
		version++;
		markDirty();
	}

	protected static class State {
		final Fluid fluid;
		final long amount;

		State(Fluid fluid, long amount) {
			this.fluid = fluid;
			this.amount = amount;
		}
	}

	private class SingleFluidIterator implements Iterator<StorageView<Fluid>>, Transaction.CloseCallback {
		private boolean open = true;
		private boolean hasNext = true;

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
			return SingleFluidStorage.this;
		}

		@Override
		public void onClose(Transaction transaction, Transaction.Result result) {
			open = false;
			iterating = false;
		}
	}
}
