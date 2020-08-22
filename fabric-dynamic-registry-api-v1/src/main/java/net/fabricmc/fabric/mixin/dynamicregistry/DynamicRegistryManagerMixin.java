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

package net.fabricmc.fabric.mixin.dynamicregistry;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.dynamicregistry.v1.CustomDynamicRegistry;
import net.fabricmc.fabric.api.dynamicregistry.v1.DynamicRegistryProvider;
import net.fabricmc.loader.api.FabricLoader;

@Mixin(DynamicRegistryManager.class)
public class DynamicRegistryManagerMixin {
	@Inject(method = "method_30531", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/DynamicRegistryManager;register(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/util/registry/RegistryKey;Lcom/mojang/serialization/Codec;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void registerCustomDynamicRegistries(CallbackInfoReturnable<ImmutableMap<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>>> ci, ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> builder) {
		List<CustomDynamicRegistry<?>> customDynamicRegistries = new ArrayList<>();

		List<DynamicRegistryProvider> providers = FabricLoader.getInstance().getEntrypoints("dynamic-registry-provider", DynamicRegistryProvider.class);

		for (DynamicRegistryProvider provider : providers) {
			provider.getDynamicRegistries(customDynamicRegistries);
		}

		for (CustomDynamicRegistry<?> customDynamicRegistry : customDynamicRegistries) {
			addRegistry(customDynamicRegistry);
			builder.put(customDynamicRegistry.getRegistryRef(), customDynamicRegistry.getInfo());
		}
	}

	@Unique
	private static <T> void addRegistry(CustomDynamicRegistry<T> customDynamicRegistry) {
		BuiltinRegistries.addRegistry(customDynamicRegistry.getRegistryRef(), customDynamicRegistry.getRegistry(), customDynamicRegistry.getDefaultValueSupplier(), customDynamicRegistry.getLifecycle());
	}
}
