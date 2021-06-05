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

package net.fabricmc.fabric.api.transfer.v1.storage;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;

/**
 * An object that can store resources.
 *
 * <p><ul>
 *     <li>{@link #supportsInsertion} and {@link #supportsExtraction} can be used to tell if insertion and extraction
 *     functionality are possibly supported by this storage.</li>
 *     <li>{@link #insert} and {@link #extract} can be used to insert or extract resources from this storage.</li>
 *     <li>{@link #iterator}, {@link #anyView} and {@link #exactView} can be used to inspect the contents of this storage.</li>
 *     <li>{@link #getVersion()} can be used to quickly check if a storage has changed, without having to rescan its contents.</li>
 * </ul>
 *
 * <p>Users that wish to implement this interface can use the helpers in the {@code base} package:
 * <ul>
 *     <li>{@link CombinedStorage} can be used to combine multiple instances, for example to combine multiple "slots" in one big storage.</li>
 *     <li>{@link ExtractionOnlyStorage} and {@link InsertionOnlyStorage} can be used when only extraction or insertion is needed.</li>
 *     <li>{@link SingleViewIterator} can be used to wrap a single view for use with {@link #iterator}.</li>
 *     <li>Resource-specific base implementations may also be available.
 *     For example, Fabric API providers {@link SingleFluidStorage} to accelerate implementations of {@code Storage<FluidKey>}.</li>
 * </ul>
 *
 * <p><b>Important note:</b> Unless otherwise specified, all transfer functions take a non-empty resource
 * and a non-negative maximum amount as parameters.
 * Implementations are encouraged to throw an exception if these preconditions are violated.
 *
 * <p>For transfer functions, the returned amount must be non-negative, and smaller than the passed maximum amount.
 * Consumers of these functions are encourage to throw an exception if these postconditions are violated.
 *
 * @param <T> The type of the stored resources.
 * @see Transaction
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public interface Storage<T> {
	/**
	 * Return false if calling {@link #insert} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by pipes or other devices that can transfer resources to know if
	 * they should render a visual connection to this storage.
	 */
	default boolean supportsInsertion() {
		return true;
	}

	/**
	 * Try to insert up to some amount of a resource into this storage.
	 *
	 * @param resource The resource to insert. May not be empty.
	 * @param maxAmount The maximum amount of resource to insert. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was inserted.
	 */
	long insert(T resource, long maxAmount, Transaction transaction);

	/**
	 * Return false if calling {@link #extract} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by pipes or other devices that can transfer resources to know if
	 * they should render a visual connection to this storage.
	 */
	default boolean supportsExtraction() {
		return true;
	}

	/**
	 * Try to extract up to some amount of a resource from this storage.
	 *
	 * @param resource The resource to extract. May not be empty.
	 * @param maxAmount The maximum amount of resource to extract. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was extracted.
	 */
	long extract(T resource, long maxAmount, Transaction transaction);

	/**
	 * Iterate through the contents of this storage, for the scope of the passed transaction.
	 * Every visited {@link StorageView} represents a stored resource and an amount.
	 *
	 * <p>The returned iterator and any view it returns are only valid for the scope of to the passed transaction.
	 * They should not be used once that transaction is closed.
	 *
	 * <p>More precisely, as soon as the transaction is closed,
	 * {@link Iterator#hasNext hasNext()} must return {@code false},
	 * and any call to {@link Iterator#next next()} must throw a {@link NoSuchElementException}.
	 *
	 * <p>{@link #insert(Object, long, Transaction) insert()} and {@link #extract(Object, long, Transaction) extract()}
	 * may be called safely during iteration.
	 * Extractions should be visible to an open iterator, but insertions are not required to.
	 * In particular, inventories with a fixed amount of slots may wish to make insertions visible to iterators,
	 * but inventories with a dynamic or very large amount of slots should not do that to ensure timely termination of
	 * the iteration.
	 *
	 * @param transaction The transaction to which the scope of the returned iterator is tied.
	 * @return An iterator over the contents of this storage.
	 */
	Iterator<StorageView<T>> iterator(Transaction transaction);

	/**
	 * Iterate through the contents of this storage, for the scope of the passed transaction.
	 * This function follows the semantics of {@link #iterator}, but returns an {@code Iterable} for use in {@code for} loops.
	 *
	 * @param transaction The transaction to which the scope of the returned iterator is tied.
	 * @return An iterable over the contents of this storage.
	 * @see #iterator
	 */
	default Iterable<StorageView<T>> iterable(Transaction transaction) {
		return () -> iterator(transaction);
	}

	/**
	 * Return any view over this storage, or {@code null} if none is available.
	 *
	 * <p>The returned view is tied to the passed transaction,
	 * and may never be used once the passed transaction has been closed.
	 *
	 * @param transaction The transaction to which the scope of the returned storage view is tied.
	 * @return A view over this storage, or {@code null} if none is available.
	 */
	@Nullable
	default StorageView<T> anyView(Transaction transaction) {
		Iterator<StorageView<T>> iterator = iterator(transaction);

		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			return null;
		}
	}

	/**
	 * Return a view over this storage, for a specific resource, or {@code null} if none is quickly available.
	 *
	 * <p>This function should only return a non-null view if this storage can provide it quickly,
	 * for example with a hashmap lookup.
	 * If returning the requested view would require iteration through a potentially large number of views,
	 * {@code null} should be returned instead.
	 *
	 * <p>The returned view is tied to the passed transaction,
	 * and may never be used once the passed transaction has been closed.
	 *
	 * @param transaction The transaction to which the scope of the returned storage view is tied.
	 * @param resource The resource for which a storage view is requested. May be empty.
	 * @return A view over this storage for the passed resource, or {@code null} if none is quickly available.
	 */
	@Nullable
	default StorageView<T> exactView(Transaction transaction, T resource) {
		return null;
	}

	/**
	 * Return an integer representing the current version of the storage.
	 * It <b>must</b> change if the state of the storage has changed,
	 * but it may also change even if the state of the storage hasn't changed.
	 *
	 * <p>Note: It is not valid to call this during a transaction,
	 * and implementations are encouraged to throw an exception if that happens.
	 */
	default int getVersion() {
		if (Transaction.isOpen()) {
			throw new IllegalStateException("getVersion() may not be called during a transaction.");
		}

		return TransferApiImpl.version++;
	}

	/**
	 * Return a class instance of this interface with the desired generic type,
	 * to be used for easier registration with API lookups.
	 */
	@SuppressWarnings("unchecked")
	static <T> Class<Storage<T>> asClass() {
		return (Class<Storage<T>>) (Object) Storage.class;
	}
}
