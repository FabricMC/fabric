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

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class CreativeFluidStorage implements ExtractionOnlyStorage<Fluid>, StorageView<Fluid> {
	public static final CreativeFluidStorage WATER = new CreativeFluidStorage(Fluids.WATER);
	public static final CreativeFluidStorage LAVA = new CreativeFluidStorage(Fluids.LAVA);

	private final Fluid infiniteFluid;
	// True when an iterator is active.
	private boolean iterating = false;

	private CreativeFluidStorage(Fluid infiniteFluid) {
		this.infiniteFluid = infiniteFluid;
	}

	@Override
	public boolean isEmpty() {
		return infiniteFluid == Fluids.EMPTY;
	}

	@Override
	public Fluid resource() {
		return infiniteFluid;
	}

	@Override
	public long amount() {
		return Long.MAX_VALUE;
	}

	@Override
	public long capacity() {
		return amount();
	}

	@Override
	public long extract(Fluid resource, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(resource, maxAmount);

		if (resource == infiniteFluid) {
			return maxAmount;
		} else {
			return 0;
		}
	}

	@Override
	public Iterator<StorageView<Fluid>> iterator(Transaction transaction) {
		if (iterating) {
			throw new IllegalStateException("An iterator is already active for this storage.");
		}

		iterating = true;
		CreativeFluidIterator iterator = new CreativeFluidIterator();
		transaction.addCloseCallback(iterator);
		return iterator;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	private class CreativeFluidIterator implements Iterator<StorageView<Fluid>>, Transaction.CloseCallback {
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
			return CreativeFluidStorage.this;
		}

		@Override
		public void onClose(Transaction transaction, Transaction.Result result) {
			open = false;
			iterating = false;
		}
	}
}
