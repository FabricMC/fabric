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
import java.util.function.Supplier;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A base {@link Storage} implementation that delegates every call to another storage,
 * except that it only allows insertion or extraction if {@link #canInsert} or {@link #canExtract} allows it respectively.
 * This can for example be used to wrap the internal storage of some device behind additional insertion or extraction checks.
 * If one of these two functions is overridden to always return false, implementors may also wish to override
 * {@link #supportsInsertion} and/or {@link #supportsExtraction}.
 *
 * <p>The static functions can be used when insertion or/and extraction should be blocked entirely.
 *
 * @param <T> The type of the stored resources.
 *
 * <b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public abstract class FilteringStorage<T> implements Storage<T> {
	/**
	 * Return a wrapper over the passed storage that prevents extraction.
	 */
	public static <T> Storage<T> insertOnlyOf(Storage<T> backingStorage) {
		return of(backingStorage, true, false);
	}

	/**
	 * Return a wrapper over the passed storage that prevents insertion.
	 */
	public static <T> Storage<T> extractOnlyOf(Storage<T> backingStorage) {
		return of(backingStorage, false, true);
	}

	/**
	 * Return a wrapper over the passed storage that prevents insertion and extraction.
	 */
	public static <T> Storage<T> readOnlyOf(Storage<T> backingStorage) {
		return of(backingStorage, false, false);
	}

	/**
	 * Return a wrapper over the passed storage that may prevent insertion or extraction, depending on the boolean parameters.
	 * For more fine-grained control, a custom subclass of {@link FilteringStorage} should be used.
	 *
	 * @param backingStorage Storage to wrap.
	 * @param allowInsert True to allow insertion, false to block insertion.
	 * @param allowExtract True to allow extraction, false to block extraction.
	 */
	public static <T> Storage<T> of(Storage<T> backingStorage, boolean allowInsert, boolean allowExtract) {
		if (allowInsert && allowExtract) {
			return backingStorage;
		}

		return new FilteringStorage<>(backingStorage) {
			@Override
			protected boolean canInsert(T resource) {
				return allowInsert;
			}

			@Override
			protected boolean canExtract(T resource) {
				return allowExtract;
			}

			@Override
			public boolean supportsInsertion() {
				return allowInsert && super.supportsInsertion();
			}

			@Override
			public boolean supportsExtraction() {
				return allowExtract && super.supportsExtraction();
			}
		};
	}

	protected final Supplier<Storage<T>> backingStorage;

	/**
	 * Create a new filtering storage, with a fixed backing storage.
	 */
	public FilteringStorage(Storage<T> backingStorage) {
		this(() -> backingStorage);
	}

	/**
	 * Create a new filtering storage, with a supplier for the backing storage.
	 * This allows the backing storage to change without having to create a new filtering storage.
	 * If that is unnecessary, the other overload can be used for convenience.
	 */
	public FilteringStorage(Supplier<Storage<T>> backingStorage) {
		this.backingStorage = backingStorage;
	}

	/**
	 * Return true if insertion of the passed resource should be forwarded to the backing storage, or false if it should fail.
	 */
	protected boolean canInsert(T resource) {
		return true;
	}

	/**
	 * Return true if extraction of the passed resource should be forwarded to the backing storage, or false if it should fail.
	 */
	protected boolean canExtract(T resource) {
		return true;
	}

	@Override
	public boolean supportsInsertion() {
		return backingStorage.get().supportsInsertion();
	}

	@Override
	public long insert(T resource, long maxAmount, TransactionContext transaction) {
		if (canInsert(resource)) {
			return backingStorage.get().insert(resource, maxAmount, transaction);
		} else {
			return 0;
		}
	}

	@Override
	public boolean supportsExtraction() {
		return backingStorage.get().supportsExtraction();
	}

	@Override
	public long extract(T resource, long maxAmount, TransactionContext transaction) {
		if (canExtract(resource)) {
			return backingStorage.get().extract(resource, maxAmount, transaction);
		} else {
			return 0;
		}
	}

	@Override
	public Iterator<StorageView<T>> iterator() {
		return Iterators.transform(backingStorage.get().iterator(), FilteringStorageView::new);
	}

	@Override
	@Nullable
	public StorageView<T> exactView(T resource) {
		StorageView<T> exact = backingStorage.get().exactView(resource);

		if (exact != null) {
			return new FilteringStorageView(exact);
		} else {
			return null;
		}
	}

	@Deprecated(forRemoval = true)
	@Override
	@Nullable
	public StorageView<T> exactView(TransactionContext transaction, T resource) {
		StorageView<T> exact = backingStorage.get().exactView(transaction, resource);

		if (exact != null) {
			return new FilteringStorageView(exact);
		} else {
			return null;
		}
	}

	@Override
	public long getVersion() {
		return backingStorage.get().getVersion();
	}

	/**
	 * This is used to ensure extractions through storage views of the backing stored also get checked by {@link #canExtract}.
	 */
	private class FilteringStorageView implements StorageView<T> {
		private final StorageView<T> backingView;

		private FilteringStorageView(StorageView<T> backingView) {
			this.backingView = backingView;
		}

		@Override
		public long extract(T resource, long maxAmount, TransactionContext transaction) {
			if (canExtract(resource)) {
				return backingView.extract(resource, maxAmount, transaction);
			} else {
				return 0;
			}
		}

		@Override
		public boolean isResourceBlank() {
			return backingView.isResourceBlank();
		}

		@Override
		public T getResource() {
			return backingView.getResource();
		}

		@Override
		public long getAmount() {
			return backingView.getAmount();
		}

		@Override
		public long getCapacity() {
			return backingView.getCapacity();
		}

		@Override
		public StorageView<T> getUnderlyingView() {
			return backingView.getUnderlyingView();
		}
	}
}
