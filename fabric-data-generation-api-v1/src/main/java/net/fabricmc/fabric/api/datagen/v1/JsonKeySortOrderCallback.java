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

package net.fabricmc.fabric.api.datagen.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The callback for when the sort orders for keys are registered for data generation.
 */
public interface JsonKeySortOrderCallback {
	Event<JsonKeySortOrderCallback> EVENT = EventFactory.createArrayBacked(JsonKeySortOrderCallback.class, callbacks -> adder -> {
		for (JsonKeySortOrderCallback callback : callbacks) {
			callback.register(adder);
		}
	});

	/**
	 * Called when the sort orders for keys are registered for data generation.
	 * @param adder The adder to add a key with a priority.
	 */
	void register(Adder adder);

	@FunctionalInterface
	interface Adder {
		/**
		 * Adds a key with a priority for sorting.
		 * @param key The key to compare.
		 * @param priority The priority for this key. Lower numbers appear before ones with higher numbers. The default priority is 2.
		 */
		void add(String key, int priority);
	}
}
