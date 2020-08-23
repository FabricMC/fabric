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

import java.util.OptionalInt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

/**
 * This convoluted Mixin's purpose is to mark only <em>specific</em> dimension types that are loaded via the
 * Registry Codec to be stable, but not <em>all</em>.
 *
 * <p>To do this in the most compatible way possible, we capture the identifier of the element that is being loaded
 * in a thread-local, and then modify the lifecycle of the data result based on that identifier.
 */
@Mixin(RegistryOps.class)
public class RegistryOpsMixin {
	private ThreadLocal<RegistryKey<?>> fabric_currentlyLoading;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onInit(CallbackInfo ci) {
		fabric_currentlyLoading = new ThreadLocal<>();
	}

	@Inject(method = "readSupplier", at = @At(value = "HEAD"))
	private <E> void captureLoadingId(RegistryKey<? extends Registry<E>> registryKey, MutableRegistry<E> mutableRegistry, Codec<E> codec, Identifier elementId, CallbackInfoReturnable<?> cri) {
		fabric_currentlyLoading.set(RegistryKey.of(registryKey, elementId));
	}

	@Inject(method = "readSupplier", at = @At(value = "RETURN"))
	private <E> void clearLoadingId(CallbackInfoReturnable<?> cri) {
		fabric_currentlyLoading.remove();
	}

	@SuppressWarnings({"unchecked"})
	@ModifyVariable(method = "readSupplier", at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/util/dynamic/RegistryOps$class_5506.method_31155(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/util/registry/RegistryKey;Lcom/mojang/serialization/Decoder;)Lcom/mojang/serialization/DataResult;"), allow = 1)
	private <E> DataResult<Pair<E, OptionalInt>> stabilizeDimensionType(DataResult<Pair<E, OptionalInt>> dataResult) {
		RegistryKey<?> key = fabric_currentlyLoading.get();

		if (key != null && key.method_31163(Registry.DIMENSION_TYPE_KEY) && FabricDimensionInternals.isStableModdedDimensionType((RegistryKey<DimensionType>) key)) {
			dataResult = dataResult.setLifecycle(Lifecycle.stable());
		}

		return dataResult;
	}
}
