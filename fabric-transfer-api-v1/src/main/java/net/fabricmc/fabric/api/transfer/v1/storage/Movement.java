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

public class Movement {
	public static <T> long move(Storage<T> from, Storage<T> to, Predicate<T> filter, long maxAmount, long denominator) {
		return move(from, to.insertionFunction(), filter, maxAmount, denominator);
	}

	public static <T> long move(Storage<T> from, StorageFunction<T> to, Predicate<T> filter, long maxAmount, long denominator) {
		long[] totalMoved = new long[] { 0 };
		from.forEach(view -> {
			T resource = view.resource();
			if (!filter.test(resource)) return false; // keep iterating
			long maxExtracted;

			// check how much can be extracted
			try (Transaction tx = Transaction.open()) {
				maxExtracted = view.extractionFunction().apply(resource, maxAmount - totalMoved[0], denominator, tx);
				tx.rollback();
			}

			try (Transaction tx = Transaction.open()) {
				// check how much can be inserted
				long accepted = to.apply(resource, maxExtracted, tx);

				// extract it, or rollback if the amounts don't match
				if (from.extractionFunction().apply(resource, accepted, denominator, tx) == accepted) {
					totalMoved[0] += accepted;
					tx.commit();
				}
			}

			return maxAmount == totalMoved[0]; // stop iteration if nothing can be moved anymore
		});
		return totalMoved[0];
	}
}
