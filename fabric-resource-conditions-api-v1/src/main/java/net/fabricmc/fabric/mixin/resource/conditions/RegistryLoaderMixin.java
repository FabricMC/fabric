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

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Decoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.Resource;

import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
	@Unique
	private static final ThreadLocal<DynamicRegistryManager> REGISTRIES = new ThreadLocal<>();

	/**
	 * Capture the current registries, so they can be passed to the resource conditions.
	 */
	@WrapOperation(method = "loadFromResource(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/RegistryLoader;load(Lnet/minecraft/registry/RegistryLoader$RegistryLoadable;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;"))
	private static DynamicRegistryManager.Immutable captureRegistries(@Coerce Object registryLoadable, DynamicRegistryManager baseRegistryManager, List<RegistryLoader.Entry<?>> entries, Operation<DynamicRegistryManager.Immutable> original) {
		try {
			REGISTRIES.set(baseRegistryManager);
			return original.call(registryLoadable, baseRegistryManager, entries);
		} finally {
			REGISTRIES.remove();
		}
	}

	@Inject(
			method = "Lnet/minecraft/registry/RegistryLoader;parseAndAdd(Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Lnet/minecraft/registry/RegistryOps;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/resource/Resource;Lnet/minecraft/registry/entry/RegistryEntryInfo;)V",
			at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/gson/JsonParser;parseReader(Ljava/io/Reader;)Lcom/google/gson/JsonElement;", remap = false),
			cancellable = true
	)
	private static <E> void checkResourceCondition(
			MutableRegistry<E> registry, Decoder<E> decoder, RegistryOps<JsonElement> ops, RegistryKey<E> key, Resource resource, RegistryEntryInfo entryInfo,
			CallbackInfo ci, @Local Reader reader, @Local JsonElement json
	) throws IOException {
		// This method is called both on the server (when loading resources) and on the client (when syncing from the
		// server). We only want to apply resource conditions when loading via loadFromResource.
		DynamicRegistryManager registries = REGISTRIES.get();
		if (registries == null) return;

		if (json.isJsonObject() && !ResourceConditionsImpl.applyResourceConditions(json.getAsJsonObject(), key.getRegistry().toString(), key.getValue(), registries)) {
			reader.close();
			ci.cancel();
		}
	}
}
