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

/**
 * A view of a single stored resource in a {@link Storage}, for use with {@link Storage.Visitor#accept}.
 *
 * <p>Note that views passed to {@link Storage.Visitor#accept} may never be empty.
 * @param <T> The type of the stored resource.
 */
public interface StorageView<T> {
	/**
	 * Return a {@link StorageFunction} that can directly extract from this view.
	 * If the view is available, it is expected that extracting directly from the view will be much faster than
	 * through {@link Storage#extractionFunction}.
	 */
	default StorageFunction<T> extractionFunction() {
		return StorageFunction.empty();
	}

	/**
	 * @return The resource stored in this view.
	 */
	T resource();

	/**
	 * @return The amount of {@link #resource} stored in this view.
	 */
	long amount();
}
