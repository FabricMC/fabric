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

package net.fabricmc.fabric.api.transfer.v1.storage.base;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.base.Preconditions;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * A {@link Storage} wrapping multiple storages.
 *
 * <p>The storages passed to {@linkplain CombinedStorage#CombinedStorage the constructor} will be iterated in order.
 *
 * @param <T> The type of the stored resources.
 * @param <S> The class of every part. {@code ? extends Storage<T>} can be used if the parts are of different types.
 */
public class CombinedStorage<T, S extends Storage<T>> implements Storage<T> {
	public final List<S> parts;

	public CombinedStorage(List<S> parts) {
		this.parts = parts;
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(T resource, long maxAmount, Transaction transaction) {
		Preconditions.checkArgument(maxAmount >= 0);
		long amount = 0;

		for (S part : parts) {
			amount += part.insert(resource, maxAmount - amount, transaction);
			if (amount == maxAmount) break;
		}

		return amount;
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(T resource, long maxAmount, Transaction transaction) {
		Preconditions.checkArgument(maxAmount >= 0);
		long amount = 0;

		for (S part : parts) {
			amount += part.extract(resource, maxAmount - amount, transaction);
			if (amount == maxAmount) break;
		}

		return amount;
	}

	@Override
	public Iterator<StorageView<T>> iterator(Transaction transaction) {
		return new CombinedIterator(transaction);
	}

	/**
	 * The combined iterator for multiple storages.
	 */
	private class CombinedIterator implements Iterator<StorageView<T>>, Transaction.CloseCallback {
		boolean open = true;
		final Transaction transaction;
		final Iterator<S> partIterator = parts.iterator();
		// Always holds the next StorageView<T>, except during next() while the iterator is being advanced.
		Iterator<StorageView<T>> currentPartIterator = null;

		CombinedIterator(Transaction transaction) {
			this.transaction = transaction;
			advanceCurrentPartIterator();
			transaction.addCloseCallback(this);
		}

		@Override
		public boolean hasNext() {
			return open && currentPartIterator != null && currentPartIterator.hasNext();
		}

		@Override
		public StorageView<T> next() {
			if (!open) {
				throw new NoSuchElementException("The transaction for this iterator was closed.");
			}

			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			StorageView<T> returned = currentPartIterator.next();

			// Advance the current part iterator
			if (!currentPartIterator.hasNext()) {
				advanceCurrentPartIterator();
			}

			return returned;
		}

		private void advanceCurrentPartIterator() {
			while (partIterator.hasNext()) {
				this.currentPartIterator = partIterator.next().iterator(transaction);

				if (this.currentPartIterator.hasNext()) {
					break;
				}
			}
		}

		@Override
		public void onClose(Transaction transaction, Transaction.Result result) {
			// As soon as the transaction is closed, this iterator is not valid anymore.
			open = false;
		}
	}
}
