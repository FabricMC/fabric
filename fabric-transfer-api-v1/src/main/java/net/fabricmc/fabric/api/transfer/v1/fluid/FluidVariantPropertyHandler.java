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

package net.fabricmc.fabric.api.transfer.v1.fluid;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

/**
 * Defines the common properties of {@linkplain FluidVariant fluid variants} of a given Fluid.
 * Register with {@link FluidVariantProperties#register}.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public interface FluidVariantPropertyHandler {
	/**
	 * Return the name that should be used for the passed fluid variant.
	 */
	default Text getName(FluidVariant fluidVariant) {
		return fluidVariant.getFluid().getDefaultState().getBlockState().getBlock().getName();
	}

	/**
	 * Return the sound corresponding to this fluid being filled.
	 */
	default SoundEvent getFillSound(FluidVariant variant) {
		return variant.getFluid().getBucketFillSound().orElse(SoundEvents.ITEM_BUCKET_FILL);
	}

	/**
	 * Return the sound corresponding to this fluid being emptied.
	 */
	default SoundEvent getEmptySound(FluidVariant variant) {
		return SoundEvents.ITEM_BUCKET_EMPTY;
	}

	/**
	 * Return an integer in [0, 15]: the light level emitted by this fluid, or 0 if it doesn't naturally emit light.
	 */
	default int getLuminance(FluidVariant variant) {
		return variant.getFluid().getDefaultState().getBlockState().getLuminance();
	}

	/**
	 * Return any integer, representing the density of this fluid.
	 */
	default int getDensity(FluidVariant variant) {
		return 1000;
	}

	/**
	 * Return a non-negative integer, representing the temperature of this fluid in Kelvin.
	 */
	default int getTemperature(FluidVariant variant) {
		return 300;
	}

	/**
	 * Return a positive integer, representing the viscosity of this fluid.
	 * Fluids with lower viscosity generally flow faster than fluids with higher viscosity.
	 */
	default int getViscosity(FluidVariant variant) {
		return 1000;
	}

	/**
	 * Return true if this fluid behaves like a gas.
	 * Gaseous fluids generally flow upwards.
	 */
	default boolean isGaseous(FluidVariant variant) {
		return getDensity(variant) < 0;
	}
}
