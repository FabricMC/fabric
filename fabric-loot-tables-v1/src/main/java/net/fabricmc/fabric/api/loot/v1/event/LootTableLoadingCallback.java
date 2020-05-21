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
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.impl.loot.DelegatingLootTableBuilder;

/**
 * An event handler that is called when loot tables are loaded.
 * Use {@link #EVENT} to register instances.
 *
 * @deprecated Replaced with {@link net.fabricmc.fabric.api.loot.v2.event.LootTableLoadingCallback}
 */
@Deprecated
@FunctionalInterface
public interface LootTableLoadingCallback extends net.fabricmc.fabric.api.loot.v2.event.LootTableLoadingCallback {
	@FunctionalInterface
	interface LootTableSetter {
		void set(LootTable supplier);
	}

	Event<LootTableLoadingCallback> EVENT = new Event<LootTableLoadingCallback>() {
		@Override
		public void register(LootTableLoadingCallback listener) {
			net.fabricmc.fabric.api.loot.v2.event.LootTableLoadingCallback.EVENT.register(listener);
		}
	};

	@Override
	default void onLootTableLoading(ResourceManager resourceManager, LootManager lootManager, Identifier id, FabricLootTableBuilder table, net.fabricmc.fabric.api.loot.v2.event.LootTableLoadingCallback.LootTableSetter setter) {
		onLootTableLoading(resourceManager, lootManager, id, new DelegatingLootTableBuilder(table), setter::set);
	}

	void onLootTableLoading(ResourceManager resourceManager, LootManager manager, Identifier id, FabricLootSupplierBuilder supplier, LootTableSetter setter);
}
