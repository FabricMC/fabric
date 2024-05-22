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

import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.loot.LootDataType;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

// Should apply before Loot API.
@Mixin(value = ReloadableRegistries.class, priority = 900)
public class ReloadableRegistriesMixin {
	// The cross-thread nature of the stuff makes this necessary. It is technically possible to query the wrapper from
	// the ops, but it requires more mixins.
	// Key refers to value, but value does not refer to key, so WeakHashMap is fine.
	@Unique
	private static final WeakHashMap<RegistryOps<?>, RegistryWrapper.WrapperLookup> REGISTRY_LOOKUPS = new WeakHashMap<>();

	@WrapOperation(method = "reload", at = @At(value = "NEW", target = "net/minecraft/registry/ReloadableRegistries$ReloadableWrapperLookup"))
	private static ReloadableRegistries.ReloadableWrapperLookup storeWrapperLookup(DynamicRegistryManager registryManager, Operation<ReloadableRegistries.ReloadableWrapperLookup> original, @Share("wrapper") LocalRef<RegistryWrapper.WrapperLookup> share) {
		ReloadableRegistries.ReloadableWrapperLookup lookup = original.call(registryManager);
		share.set(lookup);
		return lookup;
	}

	@Inject(method = "reload", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/registry/ReloadableRegistries$ReloadableWrapperLookup;getOps(Lcom/mojang/serialization/DynamicOps;)Lnet/minecraft/registry/RegistryOps;", shift = At.Shift.AFTER))
	private static void storeWrapperLookup(CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, ResourceManager resourceManager, Executor prepareExecutor, CallbackInfoReturnable<CompletableFuture<CombinedDynamicRegistries<ServerDynamicRegistryType>>> cir, @Local RegistryOps ops, @Share("wrapper") LocalRef<RegistryWrapper.WrapperLookup> share) {
		REGISTRY_LOOKUPS.put(ops, share.get());
	}

	@Inject(method = "method_58278", at = @At("HEAD"), cancellable = true)
	private static void applyConditions(LootDataType lootDataType, RegistryOps ops, MutableRegistry mutableRegistry, Identifier id, JsonElement json, CallbackInfo ci) {
		if (json.isJsonObject() && !ResourceConditionsImpl.applyResourceConditions(json.getAsJsonObject(), lootDataType.registryKey().getValue().getPath(), id, REGISTRY_LOOKUPS.get(ops))) {
			ci.cancel();
		}
	}
}
