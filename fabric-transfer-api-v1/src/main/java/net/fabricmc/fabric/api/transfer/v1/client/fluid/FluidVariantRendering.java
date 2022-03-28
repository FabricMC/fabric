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
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;

/**
 * Client-side display of fluid variants.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Environment(EnvType.CLIENT)
public class FluidVariantRendering {
	private static final ApiProviderMap<Fluid, FluidVariantRenderHandler> HANDLERS = ApiProviderMap.create();
	private static final FluidVariantRenderHandler DEFAULT_HANDLER = new FluidVariantRenderHandler() { };

	/**
	 * Register a render handler for the passed fluid.
	 */
	public static void register(Fluid fluid, FluidVariantRenderHandler handler) {
		if (HANDLERS.putIfAbsent(fluid, handler) != null) {
			throw new IllegalArgumentException("Duplicate handler registration for fluid " + fluid);
		}
	}

	/**
	 * Return the render handler for the passed fluid, if available, and {@code null} otherwise.
	 */
	@Nullable
	public static FluidVariantRenderHandler getHandler(Fluid fluid) {
		return HANDLERS.get(fluid);
	}

	/**
	 * Return the render handler for the passed fluid, if available, or the default instance otherwise.
	 */
	public static FluidVariantRenderHandler getHandlerOrDefault(Fluid fluid) {
		FluidVariantRenderHandler handler = HANDLERS.get(fluid);
		return handler == null ? DEFAULT_HANDLER : handler;
	}

	/**
	 * Return the name of the passed fluid variant.
	 * @deprecated use {@link FluidVariantAttributes#getName} instead.
	 */
	@Deprecated(forRemoval = true)
	public static Text getName(FluidVariant fluidVariant) {
		return getHandlerOrDefault(fluidVariant.getFluid()).getName(fluidVariant);
	}

	/**
	 * Return a mutable list: the tooltip for the passed fluid variant, including the name and additional lines if available
	 * and the id of the fluid if advanced tooltips are enabled.
	 *
	 * <p>Compared to {@linkplain #getTooltip(FluidVariant, TooltipContext) the other overload}, the current tooltip context is automatically used.
	 */
	public static List<Text> getTooltip(FluidVariant fluidVariant) {
		return getTooltip(fluidVariant, MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
	}

	/**
	 * Return a mutable list: the tooltip for the passed fluid variant, including the name and additional lines if available
	 * and the id of the fluid if advanced tooltips are enabled.
	 */
	public static List<Text> getTooltip(FluidVariant fluidVariant, TooltipContext context) {
		List<Text> tooltip = new ArrayList<>();

		// Name first
		tooltip.add(getName(fluidVariant));

		// Additional tooltip information
		getHandlerOrDefault(fluidVariant.getFluid()).appendTooltip(fluidVariant, tooltip, context);

		// If advanced tooltips are enabled, render the fluid id
		if (context.isAdvanced()) {
			tooltip.add(new LiteralText(Registry.FLUID.getId(fluidVariant.getFluid()).toString()).formatted(Formatting.DARK_GRAY));
		}

		// TODO: consider adding an event to append to tooltips?

		return tooltip;
	}

	/**
	 * Return the still and the flowing sprite that should be used to render the passed fluid variant, or null if they are not available.
	 * The sprites should be rendered using the color returned by {@link #getColor}.
	 *
	 * @see FluidVariantRenderHandler#getSprites
	 */
	@Nullable
	public static Sprite[] getSprites(FluidVariant fluidVariant) {
		return getHandlerOrDefault(fluidVariant.getFluid()).getSprites(fluidVariant);
	}

	/**
	 * Return the still sprite that should be used to render the passed fluid variant, or null if it's not available.
	 * The sprite should be rendered using the color returned by {@link #getColor}.
	 */
	@Nullable
	public static Sprite getSprite(FluidVariant fluidVariant) {
		Sprite[] sprites = getSprites(fluidVariant);
		return sprites != null ? Objects.requireNonNull(sprites[0]) : null;
	}

	/**
	 * Return the position-independent color that should be used to render {@linkplain #getSprite the sprite} of the passed fluid variant.
	 */
	public static int getColor(FluidVariant fluidVariant) {
		return getColor(fluidVariant, null, null);
	}

	/**
	 * Return the color that should be used when rendering {@linkplain #getSprite the sprite} of the passed fluid variant.
	 *
	 * <p>If the world and the position parameters are null, a position-independent color is returned.
	 * If the world and position parameters are not null, the color may depend on the position.
	 * For example, if world and position are passed, water will use them to return a biome-dependent color.
	 */
	public static int getColor(FluidVariant fluidVariant, @Nullable BlockRenderView view, @Nullable BlockPos pos) {
		return getHandlerOrDefault(fluidVariant.getFluid()).getColor(fluidVariant, view, pos);
	}

	/**
	 * Return {@code true} if this fluid variant should be rendered as filling tanks from the top.
	 * @deprecated use {@link FluidVariantAttributes#isLighterThanAir} instead.
	 */
	@Deprecated(forRemoval = true)
	public static boolean fillsFromTop(FluidVariant fluidVariant) {
		return getHandlerOrDefault(fluidVariant.getFluid()).fillsFromTop(fluidVariant);
	}
}
