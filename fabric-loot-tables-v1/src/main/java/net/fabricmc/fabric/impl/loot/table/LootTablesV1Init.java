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

package net.fabricmc.fabric.impl.loot.table;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;

public final class LootTablesV1Init implements ModInitializer {
	@Override
	public void onInitialize() {
		// Hook up the v1 callbacks to the v2 event
		net.fabricmc.fabric.api.loot.v2.LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, tableBuilder, setter) -> {
			LootTableLoadingCallback.EVENT.invoker().onLootTableLoading(
					resourceManager,
					lootManager,
					id,
					new DelegatingLootTableBuilder(tableBuilder),
					setter::set
			);
		});
	}
}
