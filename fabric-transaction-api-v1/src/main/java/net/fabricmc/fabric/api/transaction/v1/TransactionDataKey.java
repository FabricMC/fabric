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

/**
 * Classes implementing this interface should implement equals/hashCode accordingly.
 *
 * @param <T> the type of data the {@link Transaction} object stores for this instance
 */
public interface TransactionDataKey<T> {
	/**
	 * Apply changes to the parent transaction. This gets called when a nested
	 * transaction gets committed.
	 *
	 * @param ta      the parent transaction
	 * @param changes the changes to apply
	 */
	void applyChangesToTransaction(Transaction ta, T changes);

	/**
	 * Apply changes to the target. This gets called when a top level
	 * transaction gets committed.
	 *
	 * @param changes the changes to apply to the target
	 */
	void applyChanges(T changes);
}
