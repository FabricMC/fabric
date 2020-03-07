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

package net.fabricmc.fabric.mixin.dimension.idremap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

// NOTE: This probably goes into dimension-fixes
@Mixin(DimensionType.class)
public abstract class MixinDimensionRawIndexFix {
	@Inject(at = @At("RETURN"), method = "byRawId", cancellable = true)
	private static void byRawId(final int id, final CallbackInfoReturnable<DimensionType> info) {
		if (info.getReturnValue() == null || info.getReturnValue().getRawId() != id) {
			for (DimensionType dimension : Registry.DIMENSION_TYPE) {
				if (dimension.getRawId() == id) {
					info.setReturnValue(dimension);
					return;
				}
			}
		}
	}
}
