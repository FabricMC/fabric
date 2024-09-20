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

package net.fabricmc.fabric.mixin.content.registry;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;

import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryEventsContextImpl;

/**
 * Implements the invocation of {@link FabricFuelRegistryBuilder} callbacks.
 */
@Mixin(FuelRegistry.class)
public abstract class FuelRegistryMixin {
	/**
	 * Handles invoking both pre- and post-exclusion events.
	 *
	 * <p>Vanilla currently uses a single exclusion for non-flammable wood; if more builder calls for exclusions are added, this mixin method must be split accordingly.
	 */
	@WrapOperation(
			method = "createDefault(Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;Lnet/minecraft/resource/featuretoggle/FeatureSet;I)Lnet/minecraft/item/FuelRegistry;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/FuelRegistry$Builder;remove(Lnet/minecraft/registry/tag/TagKey;)Lnet/minecraft/item/FuelRegistry$Builder;"
			),
			allow = 1
	)
	private static FuelRegistry.Builder build(FuelRegistry.Builder builder, TagKey<Item> tag, Operation<FuelRegistry.Builder> operation, @Local(argsOnly = true) RegistryWrapper.WrapperLookup registries, @Local(argsOnly = true) FeatureSet features, @Local(argsOnly = true) int baseSmeltTime) {
		final var context = new FuelRegistryEventsContextImpl(registries, features, baseSmeltTime);

		FuelRegistryEvents.BUILD.invoker().build(builder, context);

		operation.call(builder, tag);
		FuelRegistryEvents.EXCLUSIONS.invoker().buildExclusions(builder, context);

		return builder;
	}
}
