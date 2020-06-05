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

import java.util.List;

/**
 * An implementation of {@link ChangeListDataKey} that provides prebuilt
 * methods for applying changelists to the target object.
 *
 * @param <T> the change list item type
 * @param <R> the target object type
 */
public interface SimpleChangeListDataKey<T, R> extends ChangeListDataKey<T> {
	/**
	 * Gets the current state of the target object.
	 *
	 * @param ta the transaction to retrieve the object state from
	 * @return the current state of the object
	 */
	default R getCurrentState(Transaction ta) {
		R inv = this.copyState();

		for (List<T> l : ta.collectData(this)) {
			for (T change : l) {
				this.apply(inv, change);
			}
		}

		return inv;
	}

	/**
	 * Create a copy of the persistent state.
	 *
	 * @return
	 */
	R copyState();

	/**
	 * Gets the persistent state (the state outside of the transaction) of the
	 * target object.
	 *
	 * @return the persistent object state
	 */
	R getPersistentState();

	/**
	 * Apply a single change onto the receiver.
	 *
	 * @param receiver the receiver
	 * @param change   the change to apply
	 */
	void apply(R receiver, T change);

	@Override
	default void applyChanges(List<T> changes) {
		for (T change : changes) {
			this.apply(this.getPersistentState(), change);
		}
	}
}
