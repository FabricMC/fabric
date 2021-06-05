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

import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * Helper functions to move resources between two {@link Storage}s.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public final class Movement {
	/**
	 * Move resources between two storages, matching the passed filter, and return the amount that was successfully transferred.
	 *
	 * @param from The source storage.
	 * @param to The target storage.
	 * @param filter The filter for transferred resources.
	 *               Only resources for which this filter returns {@code true} will be transferred.
	 *               This filter will never be tested with an empty resource, and filters are encouraged to throw an
	 *               exception if this guarantee is violated.
	 * @param maxAmount The maximum amount that will be transferred.
	 * @param transaction The transaction this transfer is part of, or {@code null} if a transaction should be opened just for this transfer.
	 * @param <T> The type of resources to move.
	 * @return The total amount of resources that was successfully transferred.
	 * @throws IllegalStateException If no transaction is passed and a transaction is already active on the current thread.
	 */
	public static <T> long move(Storage<T> from, Storage<T> to, Predicate<T> filter, long maxAmount, @Nullable Transaction transaction) {
		long totalMoved = 0;

		try (Transaction iterationTransaction = (transaction == null ? Transaction.openOuter() : transaction.openNested())) {
			for (StorageView<T> view : from.iterable(iterationTransaction)) {
				if (view.isEmpty()) continue;
				T resource = view.resource();
				if (!filter.test(resource)) continue;
				long maxExtracted;

				// check how much can be extracted
				try (Transaction extractionTestTransaction = iterationTransaction.openNested()) {
					maxExtracted = view.extract(resource, maxAmount - totalMoved, extractionTestTransaction);
					extractionTestTransaction.abort();
				}

				try (Transaction transferTransaction = iterationTransaction.openNested()) {
					// check how much can be inserted
					long accepted = to.insert(resource, maxExtracted, transferTransaction);

					// extract it, or rollback if the amounts don't match
					if (view.extract(resource, accepted, transferTransaction) == accepted) {
						totalMoved += accepted;
						transferTransaction.commit();
					}
				}

				if (maxAmount == totalMoved) {
					// early return if nothing can be moved anymore
					iterationTransaction.commit();
					return totalMoved;
				}
			}

			iterationTransaction.commit();
		}

		return totalMoved;
	}
}
