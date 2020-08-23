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

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

/**
 * This Mixin tries to hide the screen that warns users about experimental
 * features being used, but only for dimensions added through mods.
 */
@Mixin(DimensionOptions.class)
public class DimensionOptionMixin {
	/**
	 * This injects right after the method makes a local copy of all present
	 * dimensions, and modifies the list by removing our mod-provided dimension from
	 * it.
	 *
	 * <p>This means Vanilla will perform it's check whether it should display an
	 * experimental warning without considering our dimension.
	 */
	@ModifyVariable(method = "method_29567", at = @At(value = "INVOKE_ASSIGN", ordinal = 0, target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;", remap = false), allow = 1)
	private static List<Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions>> overrideExperimentalCheck(
			List<Map.Entry<RegistryKey<DimensionOptions>, DimensionOptions>> dimensions) {
		dimensions.removeIf(e -> FabricDimensionInternals.isStableModdedDimension(e.getKey()));
		return dimensions;
	}
}
