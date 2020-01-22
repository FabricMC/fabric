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

package net.fabricmc.fabric.api.loot.v1.event;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;

/**
 * An event handler that is called when loot tables are loaded.
 * Use {@link #EVENT} to register instances.
 */
@FunctionalInterface
public interface LootTableLoadingCallback {
	@FunctionalInterface
	interface LootTableSetter {
		void set(LootTable supplier);
	}

	Event<LootTableLoadingCallback> EVENT = EventFactory.createArrayBacked(
			LootTableLoadingCallback.class,
			(listeners) -> (resourceManager, manager, id, supplier, setter) -> {
				for (LootTableLoadingCallback callback : listeners) {
					callback.onLootTableLoading(resourceManager, manager, id, supplier, setter);
				}
			}
	);

	void onLootTableLoading(ResourceManager resourceManager, LootManager manager, Identifier id, FabricLootSupplierBuilder supplier, LootTableSetter setter);
}
