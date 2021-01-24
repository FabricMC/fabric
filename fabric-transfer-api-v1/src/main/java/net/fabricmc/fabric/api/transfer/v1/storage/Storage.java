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

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookupRegistry;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookupRegistry;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;

/**
 * An object that can store resources.
 * @param <T> The type of the stored resources.
 */
public interface Storage<T> {
	default StorageFunction<T> insertionFunction() {
		return StorageFunction.empty();
	}
	default StorageFunction<T> extractionFunction() {
		return StorageFunction.empty();
	}

	/**
	 * Iterate through the contents of this storage.
	 *
	 * <p>Note that this function is not re-entrant: the implementation may throw an exception if
	 * {@code forEach} is called from within the visitor.
	 * @return True if the visit was stopped by the visitor.
	 * @see Visitor
	 */
	boolean forEach(Visitor<T> visitor);

	/**
	 * An integer representing the current version of the storage.
	 * It <em>must</em> change if the state of the storage has changed,
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
		 * @return True to stop the visit, false to keep visiting.
		 */
		boolean accept(StorageView<T> storageView);
	}

	/**
	 * Return a class instance of this interface with the desired generic type,
	 * to be used for easier registration in {@link BlockApiLookupRegistry} and {@link ItemApiLookupRegistry}.
	 */
	@SuppressWarnings("unchecked")
	static <T> Class<Storage<T>> asClass() {
		return (Class<Storage<T>>) (Object) Storage.class;
	}
}
