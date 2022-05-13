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

package net.fabricmc.fabric.mixin.loot;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.fabric.impl.loot.LootUtil;

/**
 * Implements the events from {@link LootTableEvents}.
 */
@Mixin(LootManager.class)
abstract class LootManagerMixin {
	@Shadow
	private Map<Identifier, LootTable> tables;

	@Inject(method = "apply", at = @At("RETURN"))
	private void apply(Map<Identifier, JsonObject> jsonMap, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
		// The builder for the new LootManager.tables map with modified loot tables.
		// We're using an immutable map to match vanilla.
		ImmutableMap.Builder<Identifier, LootTable> newTables = ImmutableMap.builder();

		tables.forEach((id, table) -> {
			if (id.equals(LootTables.EMPTY)) {
				// This is a special table and cannot be modified.
				// Vanilla also warns about that.
				return;
			}

			// noinspection ConstantConditions
			LootManager lootManager = (LootManager) (Object) this;
			LootTableSource source = LootUtil.determineSource(id, resourceManager);

			// Invoke the REPLACE event for the current loot table.
			LootTable replacement = LootTableEvents.REPLACE.invoker().replaceLootTable(resourceManager, lootManager, id, table, source);

			if (replacement != null) {
				// Set the loot table to MODIFY to be the replacement loot table.
				// The MODIFY event will also see it as a replaced loot table via the source.
				table = replacement;
				source = LootTableSource.REPLACED;
			}

			// Turn the current table into a modifiable builder and invoke the MODIFY event.
			LootTable.Builder builder = FabricLootTableBuilder.copyOf(table);
			LootTableEvents.MODIFY.invoker().modifyLootTable(resourceManager, lootManager, id, builder, source);

			// Turn the builder back into a loot table and store it in the new table.
			newTables.put(id, builder.build());
		});

		// Finally, store the new loot table map in the field.
		tables = newTables.build();
	}
}
