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

package net.fabricmc.fabric.mixin.resource.loader;

import java.util.Optional;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Lifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.Resource;

import net.fabricmc.fabric.api.resource.ModResourcePack;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
	@Unique
	private static final RegistryEntryInfo MOD_PROVIDED_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.stable());

	// On the server side, loading mod-provided DRM entries should not show experiments screen.
	// While the lifecycle is set to experimental on the client side (a de-sync),
	// there is no good way to fix this without breaking protocol; the lifecycle seems to be unused on
	// the client side anyway.
	@ModifyExpressionValue(method = "loadFromResource(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"))
	private static Object markModProvidedAsStable(Object original, @Local Resource resource) {
		if (original instanceof RegistryEntryInfo info && info.knownPackInfo().isEmpty() && resource.getPack() instanceof ModResourcePack) {
			return MOD_PROVIDED_INFO;
		}

		return original;
	}
}
