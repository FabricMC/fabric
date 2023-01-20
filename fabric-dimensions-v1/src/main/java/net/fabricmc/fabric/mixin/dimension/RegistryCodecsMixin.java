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

package net.fabricmc.fabric.mixin.dimension;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Mixin;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import net.fabricmc.fabric.impl.dimension.FailSoftMapCodec;

@Mixin(RegistryCodecs.class)
public class RegistryCodecsMixin {
	/**
	 * Fix the issue that cannot load world after uninstalling a dimension mod/datapack.
	 * After uninstalling a dimension mod/datapack, the dimension config in `level.dat` file cannot be deserialized.
	 * The solution is to make it fail-soft.
	 * This contains vanilla code copy and should be checked when upgrading to newer MC version.
	 * It doesn't redirect `Codec.unboundedMap` because `FailSoftMapCodec` does not inherit `UnboundedMapCodec`.
	 * Currently, `createKeyedRegistryCodec` is only used in dimension codec.
	 */
	@SuppressWarnings("UnstableApiUsage")
	@Inject(
			method = "createKeyedRegistryCodec",
			at = @At("HEAD"),
			cancellable = true
	)
	private static <E> void injectCreateKeyedRegistryCodec(
			RegistryKey<? extends Registry<E>> registryRef, Lifecycle lifecycle,
			Codec<E> elementCodec, CallbackInfoReturnable<Codec<Registry<E>>> cir
	) {
		FailSoftMapCodec<RegistryKey<E>, E> codec =
				new FailSoftMapCodec<>(RegistryKey.createCodec(registryRef), elementCodec);
		Codec<Registry<E>> result = codec.xmap(entries -> {
			SimpleRegistry<E> mutableRegistry = new SimpleRegistry<>(registryRef, lifecycle);
			entries.forEach((key, value) -> mutableRegistry.add(key, value, lifecycle));
			return mutableRegistry.freeze();
		}, registry -> ImmutableMap.copyOf(registry.getEntrySet()));
		cir.setReturnValue(result);
	}
}
