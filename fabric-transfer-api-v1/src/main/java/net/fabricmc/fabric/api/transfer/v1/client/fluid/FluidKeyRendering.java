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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
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
	private static final Map<Fluid, FluidKeyRenderHandler> handlers = new IdentityHashMap<>();

	/**
	 * Register a render handler for the passed fluid.
	 */
	public static void register(Fluid fluid, FluidKeyRenderHandler handler) {
		Objects.requireNonNull(fluid, "Fluid may not be null.");
		Objects.requireNonNull(handler, "FluidKeyRenderHandler may not be null.");

		if (handlers.put(fluid, handler) != null) {
			throw new IllegalArgumentException("Duplicate handler registration for fluid " + fluid);
		}
	}

	/**
	 * Return the render handler for the passed fluid, if available.
	 */
	@Nullable
	public static FluidKeyRenderHandler getHandler(Fluid fluid) {
		return handlers.get(fluid);
	}

	/**
	 * Return the name of the passed fluid key.
	 */
	public static Text getName(FluidKey fluidKey) {
		FluidKeyRenderHandler handler = getHandler(fluidKey.getFluid());

		if (handler != null) {
			return handler.getName(fluidKey);
		} else {
			return fluidKey.getFluid().getDefaultState().getBlockState().getBlock().getName();
		}
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
		FluidKeyRenderHandler handler = getHandler(fluidKey.getFluid());

		if (handler != null) {
			handler.appendTooltip(fluidKey, tooltip, context);
		}

		// If advanced tooltips are enabled, render the fluid id
		if (context.isAdvanced()) {
			tooltip.add(new LiteralText(Registry.FLUID.getId(fluidKey.getFluid()).toString()).formatted(Formatting.DARK_GRAY));
		}

		// TODO: consider adding an event to append to tooltips?

		return tooltip;
	}

	/**
	 * Return the sprite that should be used to render the passed fluid key, or null if it's not available.
	 * The sprite should be rendered using the color returned by {@link #getClass()}.
	 */
	@Nullable
	public static Sprite getSprite(FluidKey fluidKey) {
		// If the fluid has a custom key renderer, use that
		FluidKeyRenderHandler handler = getHandler(fluidKey.getFluid());

		if (handler != null) {
			return handler.getSprite(fluidKey);
		}

		// Otherwise, fall back to the regular fluid renderer
		FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidKey.getFluid());

		if (fluidRenderHandler != null) {
			return fluidRenderHandler.getFluidSprites(null, null, fluidKey.getFluid().getDefaultState())[0];
		}

		return null;
	}

	/**
	 * Return the color that should be used to render {@linkplain #getSprite the sprite} of the passed fluid key.
	 */
	public static int getColor(FluidKey fluidKey) {
		// If the fluid has a custom key renderer, use that
		FluidKeyRenderHandler handler = getHandler(fluidKey.getFluid());

		if (handler != null) {
			return handler.getColor(fluidKey);
		}

		// Otherwise, fall back to the regular fluid renderer
		FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidKey.getFluid());

		if (fluidRenderHandler != null) {
			return fluidRenderHandler.getFluidColor(null, null, fluidKey.getFluid().getDefaultState());
		}

		return -1;
	}
}
