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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.client.texture.RegistrableTexture;

/**
 * A simple fluid render handler that uses and loads sprites given by their
 * identifiers. Most fluids don't need more than this. In fact, if a fluid just
 * needs the vanilla water texture with a custom color, {@link #coloredWater}
 * can be used to easily create a fluid render handler for that.
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

	/**
	 * The array of textures used, in this case 3 textures can be used:
	 * <p>[0] -> StillTexture: The texture for still fluid.</p>
	 * <p>[1] -> FlowingTexture: The texture for flowing/falling fluid.</p>
	 * <p>[2] -> [OPTIONAL] OverlayTexture: The texture behind glass, leaves and other.</p>
	 * If the overlay texture is null, this array will have a count of 2.
	 */
	protected RegistrableTexture[] textures;

	/**
	 * The array of sprites used, in this case 3 textures will be used:
	 * <p>[0] -> StillSprite: The sprite for still fluid.</p>
	 * <p>[1] -> FlowingSprite: The sprite for flowing/falling fluid.</p>
	 * <p>[2] -> [OPTIONAL] OverlaySprite: The sprite behind glass, leaves and other.</p>
	 * If the overlay texture is null, this array will have a count of 2.
	 */
	protected final Sprite[] sprites;

	/**
	 * The color used to recolor the fluid textures.
	 * <p>(Must be a hexadecimal value)</p>
	 */
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
	public SimpleFluidRenderHandler(@NotNull RegistrableTexture stillTexture, @NotNull RegistrableTexture flowingTexture,
									@Nullable RegistrableTexture overlayTexture, int tint) {
		this.textures = new RegistrableTexture[overlayTexture == null ? 2 : 3];
		this.textures[0] = stillTexture;
		this.textures[1] = flowingTexture;
		if (overlayTexture != null) this.textures[2] = overlayTexture;
		this.sprites = new Sprite[textures.length];
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
	public SimpleFluidRenderHandler(@NotNull RegistrableTexture stillTexture, @NotNull RegistrableTexture flowingTexture,
									@Nullable RegistrableTexture overlayTexture) {
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
	public SimpleFluidRenderHandler(@NotNull RegistrableTexture stillTexture, @NotNull RegistrableTexture flowingTexture,
									int tint) {
		this(stillTexture, flowingTexture, null, tint);
	}

	/**
	 * Creates a fluid render handler without an overlay texture and no tint.
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 */
	public SimpleFluidRenderHandler(@NotNull RegistrableTexture stillTexture, @NotNull RegistrableTexture flowingTexture) {
		this(stillTexture, flowingTexture, null, -1);
	}

	/**
	 * Creates a fluid render handler with an overlay texture and a custom,
	 * fixed tint.
	 * <p></p>
	 * <p>NOTE: The fluid textures are assumed to be registered to the block sprite atlas.</p>
	 * <p>If they are not, you have to manually register the fluid textures.</p>
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 * @param overlayTexture The texture behind glass, leaves and other
	 * {@linkplain FluidRenderHandlerRegistry#setBlockTransparency registered
	 * transparent blocks}.
	 * @param tint The fluid color RGB. Alpha is ignored.
	 */
	public SimpleFluidRenderHandler(@Nullable Identifier stillTexture, @Nullable Identifier flowingTexture,
									@Nullable Identifier overlayTexture, int tint) {
		this(RegistrableTexture.nonRegistrable(stillTexture), RegistrableTexture.nonRegistrable(flowingTexture),
				RegistrableTexture.nonRegistrable(overlayTexture), tint);
	}

	/**
	 * Creates a fluid render handler with an overlay texture and no tint.
	 * <p></p>
	 * <p>NOTE: The fluid textures are assumed to be registered to the block sprite atlas.</p>
	 * <p>If they are not, you have to manually register the fluid textures.</p>
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 * @param overlayTexture The texture behind glass, leaves and other
	 * {@linkplain FluidRenderHandlerRegistry#setBlockTransparency registered
	 * transparent blocks}.
	 */
	public SimpleFluidRenderHandler(@Nullable Identifier stillTexture, @Nullable Identifier flowingTexture,
									@Nullable Identifier overlayTexture) {
		this(stillTexture, flowingTexture, overlayTexture, -1);
	}

	/**
	 * Creates a fluid render handler without an overlay texture and a custom,
	 * fixed tint.
	 * <p></p>
	 * <p>NOTE: The fluid textures are assumed to be registered to the block sprite atlas.</p>
	 * <p>If they are not, you have to manually register the fluid textures.</p>
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 * @param tint The fluid color RGB. Alpha is ignored.
	 */
	public SimpleFluidRenderHandler(@Nullable Identifier stillTexture, @Nullable Identifier flowingTexture,
									int tint) {
		this(stillTexture, flowingTexture, null, tint);
	}

	/**
	 * Creates a fluid render handler without an overlay texture and no tint.
	 * <p></p>
	 * <p>NOTE: The fluid textures are assumed to be registered to the block sprite atlas.</p>
	 * <p>If they are not, you have to manually register the fluid textures.</p>
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 */
	public SimpleFluidRenderHandler(@Nullable Identifier stillTexture, @Nullable Identifier flowingTexture) {
		this(stillTexture, flowingTexture, null, -1);
	}

	/**
	 * Creates a fluid render handler that uses the vanilla water texture with a fixed, custom color.
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
	 * Creates a fluid render handler that uses the vanilla lava texture with a fixed, custom color.
	 *
	 * @param tint The fluid color RGB. Alpha is ignored.
	 * @see	#LAVA_STILL
	 * @see	#LAVA_FLOWING
	 */
	public static SimpleFluidRenderHandler coloredLava(int tint) {
		return new SimpleFluidRenderHandler(LAVA_STILL, LAVA_FLOWING, tint);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @Nullable RegistrableTexture[] getFluidTextures() {
		return textures;
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
	public void reloadTextures(@NotNull SpriteAtlasTexture textureAtlas) {
		sprites[0] = textureAtlas.getSprite(textures[0].getIdentifier());
		sprites[1] = textureAtlas.getSprite(textures[1].getIdentifier());

		if (sprites.length >= 3) {
			sprites[2] = textureAtlas.getSprite(textures[2].getIdentifier());
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
