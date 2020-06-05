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

package net.fabricmc.fabric.api.transaction.v1;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link TransactionDataKey} that implements a simple change list. Each
 * transaction holds parts of that change list, the items of the list describe
 * how to modify the backing object, e.g: "insert 10 buckets of water"
 *
 * @param <T> the change list item type
 */
public interface ChangelistDataKey<T> extends TransactionDataKey<List<T>> {
	/**
	 * Insert a change list item at the end of the list.
	 *
	 * @param ta     the transaction to insert into
	 * @param change the change list item to insert
	 */
	default void insert(Transaction ta, T change) {
		List<T> changes = ta.get(this);

		if (changes == null) {
			changes = new ArrayList<>();
			ta.put(this, changes);
		}

		changes.add(change);
	}

	@Override
	default void applyChangesToTransaction(Transaction ta, List<T> changes) {
		ta.computeIfAbsent(this, _k -> new ArrayList<>()).addAll(changes);
	}
}
