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

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;

/**
 * Defines how FluidKeys should be displayed to clients. Register with {@link FluidKeyRendering#register}.
 */
@Environment(EnvType.CLIENT)
public interface FluidKeyRenderHandler {
	/**
	 * Return the name that should be used for the passed fluid key.
	 */
	Text getName(FluidKey fluidKey);

	/**
	 * Append additional tooltips to the passed list if additional information is contained in the fluid key.
	 *
	 * <p>The name of the fluid, and its identifier if the tooltip context is advanced, should not be appended.
	 * They are already added by {@link FluidKeyRendering#getTooltip}.
	 */
	void appendTooltip(FluidKey fluidKey, List<Text> tooltip, TooltipContext tooltipContext);

	/**
	 * Return the sprite that should be used to render the passed fluid key, for use in baked models, (block) entity renderers, or user interfaces.
	 *
	 * <p>Null may be returned if the fluid key should not be rendered.
	 */
	@Nullable
	Sprite getSprite(FluidKey fluidKey);

	/**
	 * Return the color to use when rendering {@linkplain #getSprite the sprite} of this fluid key.
	 */
	int getColor(FluidKey fluidKey);
}
