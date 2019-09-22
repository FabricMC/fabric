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

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.loot.LootManager;
import net.minecraft.world.loot.LootSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(LootManager.class)
public class MixinLootManager {
	@Shadow private Map<Identifier, LootSupplier> suppliers;

	@Inject(method = "method_20712", at = @At("RETURN"))
	private void apply(Map<Identifier, JsonObject> objectMap, ResourceManager manager, Profiler profiler, CallbackInfo info) {
		Map<Identifier, LootSupplier> newSuppliers = new HashMap<>();

		suppliers.forEach((id, supplier) -> {
			FabricLootSupplierBuilder builder = FabricLootSupplierBuilder.of(supplier);

			//noinspection ConstantConditions
			LootTableLoadingCallback.EVENT.invoker().onLootTableLoading(
				manager, (LootManager) (Object) this, id, builder, (s) -> newSuppliers.put(id, s)
			);

			newSuppliers.computeIfAbsent(id, (i) -> builder.create());
		});

		suppliers = ImmutableMap.copyOf(newSuppliers);
	}
}
