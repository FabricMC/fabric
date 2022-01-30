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

import java.util.HashMap;
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
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableLoadingCallback;

@Mixin(LootManager.class)
abstract class LootManagerMixin {
	@Shadow
	private Map<Identifier, LootTable> tables;

	@Inject(method = "apply", at = @At("RETURN"))
	private void apply(Map<Identifier, JsonObject> jsonMap, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
		Map<Identifier, LootTable> newTables = new HashMap<>();

		tables.forEach((id, table) -> {
			FabricLootTableBuilder builder = FabricLootTableBuilder.copyOf(table);

			//noinspection ConstantConditions
			LootTableLoadingCallback.EVENT.invoker().onLootTableLoading(
					resourceManager, (LootManager) (Object) this, id, builder, replacement -> {
						newTables.put(id, replacement);
					}
			);

			newTables.computeIfAbsent(id, (i) -> builder.build());
		});

		tables = ImmutableMap.copyOf(newTables);
	}
}
