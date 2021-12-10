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

package net.fabricmc.fabric.api.client.render.fluid.v1;

import java.util.Arrays;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * A simple fluid render handler that uses and loads sprites given by their
 * identifiers. Most fluids don't need more than this. In fact, if a fluid just
 * needs the vanilla water texture with a custom color, {@link #coloredWater}
 * can be used to easily create a fluid render handler for that.
 *
 * <p>Note that it's assumed that the fluid textures are assumed to be
 * registered to the blocks sprite atlas. If they are not, you have to manually
 * register the fluid textures. The "fabric-textures" API may come in handy for
 * that.
 */
public class SimpleFluidRenderHandler implements FluidRenderHandler {
	/**
	 * The vanilla still water texture identifier.
	 */
	public static final Identifier WATER_STILL = new Identifier("block/water_still");

	/**
	 * The vanilla flowing water texture identifier.
	 */
	public static final Identifier WATER_FLOWING = new Identifier("block/water_flow");

	/**
	 * The vanilla water overlay texture identifier.
	 */
	public static final Identifier WATER_OVERLAY = new Identifier("block/water_overlay");

	/**
	 * The vanilla still lava texture identifier.
	 */
	public static final Identifier LAVA_STILL = new Identifier("block/lava_still");

	/**
	 * The vanilla flowing lava texture identifier.
	 */
	public static final Identifier LAVA_FLOWING = new Identifier("block/lava_flow");

	private final Identifier[] textures = new Identifier[3];

	protected final Sprite[] sprites;

	protected final int tint;

	/**
	 * Creates a fluid render handler with an overlay texture and a custom,
	 * fixed tint.
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 * @param overlayTexture The texture behind glass, leaves and other
	 * {@linkplain FluidRenderHandlerRegistry#setBlockTransparency registered
	 * transparent blocks}.
	 * @param tint The fluid color RGB. Alpha is ignored.
	 */
	public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture, @Nullable Identifier overlayTexture, int tint) {
		this.setStillTexture(stillTexture);
		this.setFlowingTexture(flowingTexture);
		this.setOverlayTexture(overlayTexture);
		this.sprites = new Sprite[overlayTexture == null ? 2 : 3];
		this.tint = tint;
	}

	/**
	 * Creates a fluid render handler with an overlay texture and no tint.
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 * @param overlayTexture The texture behind glass, leaves and other
	 * {@linkplain FluidRenderHandlerRegistry#setBlockTransparency registered
	 * transparent blocks}.
	 */
	public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture, Identifier overlayTexture) {
		this(stillTexture, flowingTexture, overlayTexture, -1);
	}

	/**
	 * Creates a fluid render handler without an overlay texture and a custom,
	 * fixed tint.
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 * @param tint The fluid color RGB. Alpha is ignored.
	 */
	public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture, int tint) {
		this(stillTexture, flowingTexture, null, tint);
	}

	/**
	 * Creates a fluid render handler without an overlay texture and no tint.
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 */
	public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture) {
		this(stillTexture, flowingTexture, null, -1);
	}

	/**
	 * Creates a fluid render handler that uses the vanilla water texture with a
	 * fixed, custom color.
	 *
	 * @param tint The fluid color RGB. Alpha is ignored.
	 * @see	#WATER_STILL
	 * @see	#WATER_FLOWING
	 * @see #WATER_OVERLAY
	 */
	public static SimpleFluidRenderHandler coloredWater(int tint) {
		return new SimpleFluidRenderHandler(WATER_STILL, WATER_FLOWING, WATER_OVERLAY, tint);
	}

	/**
	 * @return The current texture for the fluid in still state.
	 */
	public Identifier getStillTexture() {
		return textures[0];
	}

	/**
	 * @return The current texture for the fluid in flowing state.
	 */
	public Identifier getFlowingTexture() {
		return textures[1];
	}

	/**
	 * @return The current texture for the fluid overlay.
	 */
	public Identifier getOverlayTexture() {
		return textures[2];
	}

	/**
	 * Set the current texture for the fluid in still state.
	 * @param id Identifier of the texture.
	 */
	public void setStillTexture(Identifier id) {
		textures[0] = id;
	}

	/**
	 * Set the current texture for the fluid in flowing state.
	 * @param id Identifier of the texture.
	 */
	public void setFlowingTexture(Identifier id) {
		textures[1] = id;
	}

	/**
	 * Set the current texture for the fluid overlay.
	 * @param id Identifier of the texture.
	 */
	public void setOverlayTexture(Identifier id) {
		textures[2] = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identifier[] getTexturesIds() {
		return Arrays.stream(textures).filter(Objects::nonNull).toArray(Identifier[]::new);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return sprites;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reloadTextures(SpriteAtlasTexture textureAtlas) {
		sprites[0] = textureAtlas.getSprite(getStillTexture());
		sprites[1] = textureAtlas.getSprite(getFlowingTexture());

		if (getOverlayTexture() != null) {
			sprites[2] = textureAtlas.getSprite(getOverlayTexture());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return tint;
	}
}
