/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.event.loot;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.loot.FabricLootSupplierBuilder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

/**
 * An event handler that is called when loot tables are loaded.
 * Use {@link #EVENT} to register instances.
 */
@FunctionalInterface
public interface LootTableLoadingCallback {
	final Event<LootTableLoadingCallback> EVENT = EventFactory.createArrayBacked(
			LootTableLoadingCallback.class,
			(listeners) -> (manager, id, supplier) -> {
				for (LootTableLoadingCallback callback : listeners) {
					callback.onLoading(manager, id, supplier);
				}
			}
	);

	void onLoading(ResourceManager manager, Identifier id, FabricLootSupplierBuilder supplier);
}
