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

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;

/**
 * An object that can store resources.
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
 */
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
	 * Iterate through the contents of this storage.
	 *
	 * <p>Note: This function is not re-entrant: the implementation may throw an exception if
	 * {@code forEach} is called from within the visitor.
	 *
	 * <p>Note: This function should not call {@link Transaction#openNested},
	 * as the visitor might use the passed transaction directly for transfer operations.
	 *
	 * @return True if the visit was stopped by the visitor, or false otherwise.
	 * @see Visitor
	 */
	boolean forEach(Visitor<T> visitor, Transaction transaction);

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
	 * A storage visitor, for use with {@link #forEach}.
	 */
	@FunctionalInterface
	interface Visitor<T> {
		/**
		 * Read and modify the target view if necessary, and return whether to stop the visit.
		 * References to {@code StorageView}s should never be retained.
		 *
		 * @return True to stop the visit, false to keep visiting.
		 */
		boolean accept(StorageView<T> storageView);
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
