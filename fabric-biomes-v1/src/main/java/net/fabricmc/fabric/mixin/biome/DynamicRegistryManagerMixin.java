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

package net.fabricmc.fabric.mixin.biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.impl.biome.FabricBiomesInternal;

// NOTE: This can be replaced by #1029 once that gets merged
@Mixin(DynamicRegistryManager.class)
public class DynamicRegistryManagerMixin {
	@Inject(method = "create", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/RegistryOps$class_5506$class_5507;<init>()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void onCreateImpl(CallbackInfoReturnable<DynamicRegistryManager.Impl> cir, DynamicRegistryManager.Impl registryManager) {
		MutableRegistry<Biome> biomeRegistry = registryManager.get(Registry.BIOME_KEY);
		RegistryEntryAddedCallback.event(biomeRegistry).register((rawId, id, object) -> {
			FabricBiomesInternal.onBiomeRegistered(rawId, RegistryKey.of(Registry.BIOME_KEY, id), biomeRegistry.get(id), biomeRegistry, registryManager);
		});
	}
}
