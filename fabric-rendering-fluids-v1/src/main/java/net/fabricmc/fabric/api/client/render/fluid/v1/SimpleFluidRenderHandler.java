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

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * A simple fluid render handler that uses and loads sprites given by their
 * identifiers. Most fluids don't need more than this. The fluid is not tinted.
 *
 * <p>Note that it's assumed that the fluid textures are assumed to be
 * registered to the sprite atlas. If they are not, you have to manually
 * register the fluid textures. The "fabric-textures" API may come in handy for
 * that.
 */
public class SimpleFluidRenderHandler implements FluidRenderHandler {
	protected final Identifier stillTexture;
	protected final Identifier flowingTexture;
	protected final Identifier overlayTexture;

	protected final Sprite[] sprites;

	/**
	 * Creates a fluid render handler with an overlay texture.
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 * @param overlayTexture The texture behind glass, leaves and other
	 * {@linkplain FluidRenderHandlerRegistry#setBlockTransparency registered
	 * transparent blocks}.
	 */
	public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture, @Nullable Identifier overlayTexture) {
		this.stillTexture = stillTexture;
		this.flowingTexture = flowingTexture;
		this.overlayTexture = overlayTexture;
		this.sprites = new Sprite[overlayTexture == null ? 2 : 3];
	}

	/**
	 * Creates a fluid render handler without an overlay texture.
	 *
	 * @param stillTexture The texture for still fluid.
	 * @param flowingTexture The texture for flowing/falling fluid.
	 */
	public SimpleFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture) {
		this(stillTexture, flowingTexture, null);
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
		sprites[0] = textureAtlas.getSprite(stillTexture);
		sprites[1] = textureAtlas.getSprite(flowingTexture);

		if (overlayTexture != null) {
			sprites[2] = textureAtlas.getSprite(overlayTexture);
		}
	}
}
