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

package net.fabricmc.fabric.mixin.structure;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.api.structure.v1.FabricStructurePool;
import net.fabricmc.fabric.api.structure.v1.StructurePoolAddCallback;

@Mixin(DynamicRegistryManager.class)
public abstract class MixinDynamicRegistryManager {
	@Inject(method = "load(Lnet/minecraft/util/dynamic/RegistryOps;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Lnet/minecraft/util/registry/DynamicRegistryManager$Info;)V", at = @At(value = "TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
	private static <E> void load(RegistryOps<?> ops, DynamicRegistryManager.Impl manager, DynamicRegistryManager.Info<E> info, CallbackInfo ci, RegistryKey<? extends Registry<E>> registryKey, SimpleRegistry<E> simpleRegistry) {
		if (registryKey.getValue().toString().contains("template_pool")) {
			for (Map.Entry<RegistryKey<Object>, Object> e : ((SimpleRegistryAccessor) simpleRegistry).getKeyToEntry().entrySet()) {
				if (e.getValue() instanceof StructurePool) {
					StructurePoolAddCallback.EVENT.invoker().add(new FabricStructurePool((StructurePool) e.getValue()));
				}
			}
		}
	}
}
