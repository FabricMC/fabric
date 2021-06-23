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

package net.fabricmc.fabric.api.transfer.v1.client.fluid;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;

/**
 * Client-side display of fluid keys.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
@Environment(EnvType.CLIENT)
public class FluidKeyRendering {
	private static final ApiProviderMap<Fluid, FluidKeyRenderHandler> HANDLERS = ApiProviderMap.create();
	private static final FluidKeyRenderHandler DEFAULT_HANDLER = new FluidKeyRenderHandler() { };

	/**
	 * Register a render handler for the passed fluid.
	 */
	public static void register(Fluid fluid, FluidKeyRenderHandler handler) {
		if (HANDLERS.putIfAbsent(fluid, handler) != null) {
			throw new IllegalArgumentException("Duplicate handler registration for fluid " + fluid);
		}
	}

	/**
	 * Return the render handler for the passed fluid, if available, and {@code null} otherwise.
	 */
	@Nullable
	public static FluidKeyRenderHandler getHandler(Fluid fluid) {
		return HANDLERS.get(fluid);
	}

	/**
	 * Return the render handler for the passed fluid, if available, or the default instance otherwise.
	 */
	public static FluidKeyRenderHandler getHandlerOrDefault(Fluid fluid) {
		FluidKeyRenderHandler handler = HANDLERS.get(fluid);
		return handler == null ? DEFAULT_HANDLER : handler;
	}

	/**
	 * Return the name of the passed fluid key.
	 */
	public static Text getName(FluidKey fluidKey) {
		return getHandlerOrDefault(fluidKey.getFluid()).getName(fluidKey);
	}

	/**
	 * Return the tooltip for the passed fluid key, including the name and additional lines if available
	 * and the id of the fluid if advanced tooltips are enabled.
	 */
	public static List<Text> getTooltip(FluidKey fluidKey, TooltipContext context) {
		List<Text> tooltip = new ArrayList<>();

		// Name first
		tooltip.add(getName(fluidKey));

		// Additional tooltip information
		getHandlerOrDefault(fluidKey.getFluid()).appendTooltip(fluidKey, tooltip, context);

		// If advanced tooltips are enabled, render the fluid id
		if (context.isAdvanced()) {
			tooltip.add(new LiteralText(Registry.FLUID.getId(fluidKey.getFluid()).toString()).formatted(Formatting.DARK_GRAY));
		}

		// TODO: consider adding an event to append to tooltips?

		return tooltip;
	}

	/**
	 * Return the sprite that should be used to render the passed fluid key, or null if it's not available.
	 * The sprite should be rendered using the color returned by {@link #getColor}.
	 */
	@Nullable
	public static Sprite getSprite(FluidKey fluidKey) {
		return getHandlerOrDefault(fluidKey.getFluid()).getSprite(fluidKey);
	}

	/**
	 * Return the color that should be used to render {@linkplain #getSprite the sprite} of the passed fluid key.
	 */
	public static int getColor(FluidKey fluidKey) {
		return getHandlerOrDefault(fluidKey.getFluid()).getColor(fluidKey);
	}

	/**
	 * Return {@code true} if this fluid key should be rendered as filling tanks from the top.
	 */
	public static boolean fillFromTop(FluidKey fluidKey) {
		return getHandlerOrDefault(fluidKey.getFluid()).fillFromTop(fluidKey);
	}
}
