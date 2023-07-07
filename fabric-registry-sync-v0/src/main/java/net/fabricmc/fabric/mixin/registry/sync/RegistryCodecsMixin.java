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

package net.fabricmc.fabric.mixin.registry.sync;

import com.mojang.serialization.Lifecycle;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;

// Implements defaulted dynamic registries for registry codecs (used for synced dynamic registries).
@Mixin(RegistryCodecs.class)
abstract class RegistryCodecsMixin {
	@Dynamic("method_40345: Synthetic lambda body for Codec.xmap in createRegistryCodec")
	@Redirect(method = "method_40345", at = @At(value = "NEW", target = "(Lnet/minecraft/registry/RegistryKey;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/registry/SimpleRegistry;"))
	private static <T> SimpleRegistry<T> redirectFlatDynamicRegistryCreation(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		return DynamicRegistriesImpl.createDynamicRegistry(key, lifecycle);
	}

	@Dynamic("method_45944: Synthetic lambda body for Codec.xmap in createKeyedRegistryCodec")
	@Redirect(method = "method_45944", at = @At(value = "NEW", target = "(Lnet/minecraft/registry/RegistryKey;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/registry/SimpleRegistry;"))
	private static <T> SimpleRegistry<T> redirectKeyedDynamicRegistryCreation(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		return DynamicRegistriesImpl.createDynamicRegistry(key, lifecycle);
	}
}
