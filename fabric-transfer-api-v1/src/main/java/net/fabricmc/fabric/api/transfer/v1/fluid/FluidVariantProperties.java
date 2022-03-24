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

import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;

/**
 * Common fluid variant properties, accessible both client-side and server-side.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public class FluidVariantProperties {
	private static final ApiProviderMap<Fluid, FluidVariantPropertyHandler> HANDLERS = ApiProviderMap.create();
	private static final FluidVariantPropertyHandler DEFAULT_HANDLER = new FluidVariantPropertyHandler() { };

	/**
	 * Register a property handler for the passed fluid.
	 */
	public static void register(Fluid fluid, FluidVariantPropertyHandler handler) {
		if (HANDLERS.putIfAbsent(fluid, handler) != null) {
			throw new IllegalArgumentException("Duplicate handler registration for fluid " + fluid);
		}
	}

	/**
	 * Return the property handler for the passed fluid, if available, and {@code null} otherwise.
	 */
	@Nullable
	public static FluidVariantPropertyHandler getHandler(Fluid fluid) {
		return HANDLERS.get(fluid);
	}

	/**
	 * Return the property handler for the passed fluid, if available, or the default instance otherwise.
	 */
	public static FluidVariantPropertyHandler getHandlerOrDefault(Fluid fluid) {
		FluidVariantPropertyHandler handler = HANDLERS.get(fluid);
		return handler == null ? DEFAULT_HANDLER : handler;
	}

	public static Text getName(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getName(variant);
	}

	public static SoundEvent getFillSound(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getFillSound(variant);
	}

	public static SoundEvent getEmptySound(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getEmptySound(variant);
	}

	public static int getLuminance(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getLuminance(variant);
	}

	public static int getDensity(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getDensity(variant);
	}

	public static int getTemperature(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getTemperature(variant);
	}

	public static int getViscosity(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).getViscosity(variant);
	}

	public static boolean isGaseous(FluidVariant variant) {
		return getHandlerOrDefault(variant.getFluid()).isGaseous(variant);
	}

	// TODO: javadoc
	// TODO: should something be done for the empty fluid?

	static {
		register(Fluids.LAVA, new FluidVariantPropertyHandler() {
			@Override
			public SoundEvent getFillSound(FluidVariant variant) {
				return SoundEvents.ITEM_BUCKET_FILL_LAVA;
			}

			@Override
			public SoundEvent getEmptySound(FluidVariant variant) {
				return SoundEvents.ITEM_BUCKET_EMPTY_LAVA;
			}

			@Override
			public int getTemperature(FluidVariant variant) {
				return 1300;
			}

			@Override
			public int getViscosity(FluidVariant variant) {
				return 6000;
			}
		});
	}
}
