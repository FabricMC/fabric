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

import java.util.Optional;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/**
 * Defines the common attributes of {@linkplain FluidVariant fluid variants} of a given Fluid.
 * Register with {@link FluidVariantAttributes#register}.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public interface FluidVariantAttributeHandler {
	/**
	 * Return the name that should be used for the passed fluid variant.
	 */
	default Text getName(FluidVariant fluidVariant) {
		return fluidVariant.getFluid().getDefaultState().getBlockState().getBlock().getName();
	}

	/**
	 * Return the sound corresponding to this fluid being filled, or none if no sound is available.
	 *
	 * <p>If a non-empty sound event is returned, {@link Fluid#getBucketFillSound} will return that sound.
	 */
	default Optional<SoundEvent> getFillSound(FluidVariant variant) {
		return Optional.empty();
	}

	/**
	 * Return the sound corresponding to this fluid being emptied, or none if no sound is available.
	 *
	 * <p>If a non-empty sound event is returned, {@link BucketItem#playEmptyingSound} will play that sound.
	 */
	default Optional<SoundEvent> getEmptySound(FluidVariant variant) {
		return Optional.empty();
	}

	/**
	 * Return an integer in [0, 15]: the light level emitted by this fluid, or 0 if it doesn't naturally emit light.
	 */
	default int getLuminance(FluidVariant variant) {
		return variant.getFluid().getDefaultState().getBlockState().getLuminance();
	}

	/**
	 * Return a non-negative integer, representing the temperature of this fluid in Kelvin.
	 * The reference values are {@value FluidConstants#WATER_TEMPERATURE} for water, and {@value FluidConstants#LAVA_TEMPERATURE} for lava.
	 */
	default int getTemperature(FluidVariant variant) {
		return FluidConstants.WATER_TEMPERATURE;
	}

	/**
	 * Return a positive integer, representing the viscosity of this fluid.
	 * Fluids with lower viscosity generally flow faster than fluids with higher viscosity.
	 *
	 * <p>More precisely, viscosity should be {@value FluidConstants#VISCOSITY_RATIO} * {@link FlowableFluid#getFlowSpeed} for flowable fluids.
	 * The reference values are {@value FluidConstants#WATER_VISCOSITY} for water,
	 * {@value FluidConstants#LAVA_VISCOSITY_NETHER} for lava in ultrawarm dimensions (such as the nether),
	 * and {@value FluidConstants#LAVA_VISCOSITY} for lava in other dimensions.
	 *
	 * @param world World if available, otherwise null.
	 */
	default int getViscosity(FluidVariant variant, @Nullable World world) {
		return FluidConstants.WATER_VISCOSITY;
	}

	/**
	 * Return true if this fluid is lighter than air.
	 * Fluids that are lighter than air generally flow upwards.
	 */
	default boolean isLighterThanAir(FluidVariant variant) {
		return false;
	}
}
