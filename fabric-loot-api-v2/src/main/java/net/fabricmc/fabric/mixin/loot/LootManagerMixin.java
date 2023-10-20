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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
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
	private Map<LootDataKey<?>, ?> keyToValue;

	@Inject(method = "reload", at = @At("RETURN"), cancellable = true)
	private void reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		//noinspection DataFlowIssue
		LootManager lootManager = (LootManager) (Object) this;
		cir.setReturnValue(cir.getReturnValue().thenRun(() -> applyLootTableEvents(manager, lootManager)));
	}

	@Unique
	private void applyLootTableEvents(ResourceManager resourceManager, LootManager lootManager) {
		// The builder for the new LootManager.tables map with modified loot tables.
		// We're using an immutable map to match vanilla.
		ImmutableMap.Builder<LootDataKey<?>, Object> newTables = ImmutableMap.builder();

		this.keyToValue.forEach((dataKey, entry) -> {
			if (dataKey == LootManager.EMPTY_LOOT_TABLE) {
				// This is a special table and cannot be modified.
				// Vanilla also warns about that.
				newTables.put(dataKey, entry);
				return;
			}

			if (!(entry instanceof LootTable table)) {
				// We only want to modify loot tables
				newTables.put(dataKey, entry);
				return;
			}

			LootTableSource source = LootUtil.determineSource(dataKey.id(), resourceManager);
			// Invoke the REPLACE event for the current loot table.
			LootTable replacement = LootTableEvents.REPLACE.invoker().replaceLootTable(resourceManager, lootManager, dataKey.id(), table, source);

			if (replacement != null) {
				// Set the loot table to MODIFY to be the replacement loot table.
				// The MODIFY event will also see it as a replaced loot table via the source.
				table = replacement;
				source = LootTableSource.REPLACED;
			}

			// Turn the current table into a modifiable builder and invoke the MODIFY event.
			LootTable.Builder builder = FabricLootTableBuilder.copyOf(table);
			LootTableEvents.MODIFY.invoker().modifyLootTable(resourceManager, lootManager, dataKey.id(), builder, source);

			// Turn the builder back into a loot table and store it in the new table.
			newTables.put(dataKey, builder.build());
		});

		this.keyToValue = newTables.build();
		LootTableEvents.ALL_LOADED.invoker().onLootTablesLoaded(resourceManager, lootManager);
	}
}
