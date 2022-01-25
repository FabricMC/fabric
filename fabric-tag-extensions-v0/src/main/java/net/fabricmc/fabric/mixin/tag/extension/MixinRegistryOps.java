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

package net.fabricmc.fabric.mixin.tag.extension;

import com.mojang.serialization.DynamicOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.impl.tag.extension.TagFactoryImpl;

/**
 * This mixin loads dynamic registry tags right after datapack entries loaded.
 * Needs a higher priority so it will be called before biome modifications.
 */
@Mixin(value = RegistryOps.class, priority = 900)
public class MixinRegistryOps {
	@Inject(method = "ofLoaded", at = @At("RETURN"))
	private static <T> void afterDynamicRegistryLoaded(DynamicOps<T> dynamicOps, ResourceManager resourceManager, DynamicRegistryManager registryManager, CallbackInfoReturnable<RegistryOps<T>> cir) {
		TagFactoryImpl.loadDynamicRegistryTags(registryManager, resourceManager);
	}
}
