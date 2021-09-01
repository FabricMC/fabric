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

package net.fabricmc.fabric.mixin.transfer;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.fluid.Fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantCache;

/**
 * Cache the FluidVariant with a null tag inside each Fluid directly.
 */
@Mixin(Fluid.class)
@SuppressWarnings("unused")
public class FluidMixin implements FluidVariantCache {
	@SuppressWarnings("ConstantConditions")
	private final FluidVariant fabric_cachedFluidVariant = new FluidVariantImpl((Fluid) (Object) this, null);

	@Override
	public FluidVariant fabric_getCachedFluidVariant() {
		return fabric_cachedFluidVariant;
	}
}
