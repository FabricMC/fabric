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

/**
 * A view of a single stored resource in a {@link Storage}, for use with {@link Storage#iterator}.
 *
 * <p>Note that views returned by {@link Storage#iterator} may never be empty.
 *
 * @param <T> The type of the stored resource.
 */
public interface StorageView<T> {
	/**
	 * Try to extract a resource from this view.
	 * @return The amount that was extracted.
	 */
	long extract(T resource, long maxAmount, Transaction transaction);

	/**
	 * @return The resource stored in this view.
	 */
	T resource();

	/**
	 * @return The amount of {@link #resource} stored in this view.
	 */
	long amount();
}
