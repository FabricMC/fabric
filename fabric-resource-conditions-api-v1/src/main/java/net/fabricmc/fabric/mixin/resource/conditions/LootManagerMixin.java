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

package net.fabricmc.fabric.mixin.resource.conditions;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

/**
 * Starting with 1.20, LootManager directly implements ResourceReloader.
 */
@Mixin(LootManager.class)
public class LootManagerMixin {
	// Keep track of the DynamicRegistryManager instance by assgining it to the map that is passed to the async runnable.
	@Unique
	private static final Map<Object, DynamicRegistryManager.Immutable> dynamicRegistryManagerMap = Collections.synchronizedMap(new IdentityHashMap<>());

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;runAsync(Ljava/lang/Runnable;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void load(LootDataType type, ResourceManager resourceManager, Executor executor, Map<LootDataType<?>, Map<Identifier, ?>> results, CallbackInfoReturnable<CompletableFuture<?>> cir, Map map) {
		dynamicRegistryManagerMap.put(map, ResourceConditionsImpl.CURRENT_REGISTRIES.get());
	}

	// runAsync Runnable in load method
	@Inject(method = "method_51189", at = @At("HEAD"))
	private static void runAsync(ResourceManager resourceManager, LootDataType lootDataType, Map map, CallbackInfo ci) {
		assert ResourceConditionsImpl.CURRENT_REGISTRIES.get() == null;
		ResourceConditionsImpl.CURRENT_REGISTRIES.set(Objects.requireNonNull(dynamicRegistryManagerMap.remove(map)));
	}

	// forEach in load method
	@Inject(method = "method_51195", at = @At("HEAD"), cancellable = true)
	private static void applyResourceConditions(LootDataType lootDataType, Map map, Identifier id, JsonElement json, CallbackInfo ci) {
		if (json.isJsonObject()) {
			JsonObject obj = json.getAsJsonObject();

			if (obj.has(ResourceConditions.CONDITIONS_KEY)) {
				boolean matched = ResourceConditions.objectMatchesConditions(obj);

				if (!matched) {
					ci.cancel();
				}

				if (ResourceConditionsImpl.LOGGER.isDebugEnabled()) {
					String verdict = matched ? "Allowed" : "Rejected";
					ResourceConditionsImpl.LOGGER.debug("{} resource of type {} with id {}", verdict, lootDataType.getId(), id);
				}
			}
		}
	}

	// runAsync Runnable in load method
	@Inject(method = "method_51189", at = @At("RETURN"))
	private static void runAsyncEnd(ResourceManager resourceManager, LootDataType lootDataType, Map map, CallbackInfo ci) {
		ResourceConditionsImpl.CURRENT_REGISTRIES.remove();
	}
}
