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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

public final class LootTablesV1Init implements ModInitializer {
	private static final ThreadLocal<Map<Identifier, BufferingLootTableBuilder>> BUFFERS = ThreadLocal.withInitial(HashMap::new);

	@Override
	public void onInitialize() {
		LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original, source) -> {
			BufferingLootTableBuilder builder = new BufferingLootTableBuilder();
			builder.init(original);
			BUFFERS.get().put(id, builder);

			LootTable[] result = new LootTable[1];
			LootTableLoadingCallback.EVENT.invoker().onLootTableLoading(
					resourceManager,
					lootManager,
					id,
					builder,
					table -> result[0] = table
			);

			return result[0];
		});

		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			Map<Identifier, BufferingLootTableBuilder> buffers = BUFFERS.get();

			if (buffers.containsKey(id)) {
				try {
					buffers.get(id).applyTo(tableBuilder);
				} finally {
					buffers.remove(id);

					if (buffers.isEmpty()) {
						BUFFERS.remove();
					}
				}
			}
		});
	}
}
