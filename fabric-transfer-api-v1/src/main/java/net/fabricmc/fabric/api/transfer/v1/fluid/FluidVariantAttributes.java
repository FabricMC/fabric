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
import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.impl.transfer.TransferApiImpl;

/**
 * Common fluid variant attributes, accessible both client-side and server-side.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public class FluidVariantAttributes {
	private static final ApiProviderMap<Fluid, FluidVariantAttributeHandler> HANDLERS = ApiProviderMap.create();
	private static final FluidVariantAttributeHandler DEFAULT_HANDLER = new FluidVariantAttributeHandler() { };

	/**
	 * Register an attribute handler for the passed fluid.
	 */
	public static void register(Fluid fluid, FluidVariantAttributeHandler handler) {
		if (HANDLERS.putIfAbsent(fluid, handler) != null) {
			throw new IllegalArgumentException("Duplicate handler registration for fluid " + fluid);
		}
	}

	/**
	 * Return the attribute handler for the passed fluid, if available, and {@code null} otherwise.
	 */
	@Nullable
	public static FluidVariantAttributeHandler getHandler(Fluid fluid) {
		return HANDLERS.get(fluid);
	}

	/**
	 * Return the attribute handler for the passed fluid, if available, or the default instance otherwise.
	 */
	public static FluidVariantAttributeHandler getHandlerOrDefault(Fluid fluid) {
		FluidVariantAttributeHandler handler = HANDLERS.get(fluid);
		return handler == null ? DEFAULT_HANDLER : handler;
	}

	/**
	 * Return the name that should be used for the passed fluid variant.
	 */
	public static Text getName(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getName(variant);
	}

	/**
	 * Return the sound corresponding to a container of this fluid variant being filled.
	 */
	public static SoundEvent getFillSound(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getFillSound(variant);
	}

	/**
	 * Return the sound corresponding to a container of this fluid variant being emptied.
	 */
	public static SoundEvent getEmptySound(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getEmptySound(variant);
	}

	/**
	 * Return an integer in [0, 15]: the light level emitted by this fluid variant, or 0 if it doesn't naturally emit light.
	 */
	public static int getLuminance(FluidVariant variant) {
		int luminance = getHandlerOrDefault(variant.getFluid()).getLuminance(variant);

		if (luminance < 0 || luminance > 15) {
			TransferApiImpl.LOGGER.warn("Broken FluidVariantAttributeHandler. Invalid luminance %d for fluid variant %s".formatted(luminance, variant));
			return DEFAULT_HANDLER.getLuminance(variant);
		}

		return luminance;
	}

	/**
	 * Return a non-negative integer, representing the temperature of this fluid in Kelvin.
	 * The reference values are 300 for water, and 1300 for lava.
	 */
	public static int getTemperature(FluidVariant variant) {
		int temperature = getHandlerOrDefault(variant.getFluid()).getTemperature(variant);

		if (temperature < 0) {
			TransferApiImpl.LOGGER.warn("Broken FluidVariantAttributeHandler. Invalid temperature %d for fluid variant %s".formatted(temperature, variant));
			return DEFAULT_HANDLER.getTemperature(variant);
		}

		return temperature;
	}

	/**
	 * Return a positive integer, representing the viscosity of this fluid variant.
	 * Fluids with lower viscosity generally flow faster than fluids with higher viscosity.
	 *
	 * <p>More precisely, viscosity should roughly be 200 * {@code FlowableFluid.getFlowSpeed}.
	 * The reference values are 1000 for water, 2000 for lava in ultrawarm dimensions (such as the nether), and 6000 for lava in other dimensions.
	 *
	 * @param world World if available, otherwise null.
	 */
	public static int getViscosity(FluidVariant variant, @Nullable World world) {
		int viscosity = getHandlerOrDefault(variant.getFluid()).getViscosity(variant, world);

		if (viscosity <= 0) {
			TransferApiImpl.LOGGER.warn("Broken FluidVariantAttributeHandler. Invalid viscosity %d for fluid variant %s".formatted(viscosity, variant));
			return DEFAULT_HANDLER.getViscosity(variant, world);
		}

		return viscosity;
	}

	/**
	 * Return true if this fluid behaves like a gas.
	 * Gaseous fluids generally flow upwards.
	 */
	public static boolean isGaseous(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).isGaseous(variant);
	}

	static {
		register(Fluids.LAVA, new FluidVariantAttributeHandler() {
			@Override
			public SoundEvent getEmptySound(FluidVariant variant) {
				return SoundEvents.ITEM_BUCKET_EMPTY_LAVA;
			}

			@Override
			public int getTemperature(FluidVariant variant) {
				return FluidConstants.LAVA_TEMPERATURE;
			}
		});
	}
}