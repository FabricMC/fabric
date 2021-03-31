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

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * Helper functions to move resources between two {@link Storage}s.
 */
public final class Movement {
	public static <T> long move(Storage<T> from, Storage<T> to, Predicate<T> filter, long maxAmount) {
		try (Transaction moveTransaction = Transaction.openOuter()) {
			long result = move(from, to, filter, maxAmount, moveTransaction);
			moveTransaction.commit();
			return result;
		}
	}

	public static <T> long move(Storage<T> from, Storage<T> to, Predicate<T> filter, long maxAmount, Transaction transaction) {
		long[] totalMoved = new long[] { 0 };
		from.forEach(view -> {
			T resource = view.resource();
			if (!filter.test(resource)) return false; // keep iterating
			long maxExtracted;

			// check how much can be extracted
			try (Transaction extractionTestTransaction = transaction.openNested()) {
				maxExtracted = view.extract(resource, maxAmount - totalMoved[0], extractionTestTransaction);
				extractionTestTransaction.abort();
			}

			try (Transaction transferTransaction = transaction.openNested()) {
				// check how much can be inserted
				long accepted = to.insert(resource, maxExtracted, transferTransaction);

				// extract it, or rollback if the amounts don't match
				if (view.extract(resource, accepted, transferTransaction) == accepted) {
					totalMoved[0] += accepted;
					transferTransaction.commit();
				}
			}

			return maxAmount == totalMoved[0]; // stop iteration if nothing can be moved anymore
		}, transaction);
		return totalMoved[0];
	}
}
