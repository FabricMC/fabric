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

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;

/**
 * Interface for handling the rendering of a FluidState.
 */
public interface FluidRenderHandler {
	/**
	 * Get the sprites for a fluid being rendered at a given position. For
	 * optimal performance, the sprites should be loaded as part of a resource
	 * reload and *not* looked up every time the method is called! You likely
	 * want to override {@link #reloadTextures} to reload your fluid sprites.
	 *
	 * <p>The "fabric-textures" module contains sprite rendering facilities,
	 * which may come in handy here.
	 *
	 * @param view The world view pertaining to the fluid. May be null!
	 * @param pos The position of the fluid in the world. May be null!
	 * @param state The current state of the fluid.
	 * @return An array of size two or more: the first entry contains the
	 * "still" sprite, while the second entry contains the "flowing" sprite. If
	 * it contains a third sprite, that sprite is used as overlay behind glass
	 * and leaves.
	 */
	Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state);

	/**
	 * Get the tint color for a fluid being rendered at a given position.
	 *
	 * <p>Note: As of right now, our hook cannot handle setting a custom alpha
	 * tint here - as such, it must be contained in the texture itself!
	 *
	 * @param view The world view pertaining to the fluid. May be null!
	 * @param pos The position of the fluid in the world. May be null!
	 * @param state The current state of the fluid.
	 * @return The tint color of the fluid.
	 */
	default int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return -1;
	}

	/**
	 * Tessellate your fluid. This method will be invoked before the default
	 * fluid renderer. By default, it will call the default fluid renderer. Call
	 * {@code FluidRenderHandler.super.renderFluid} if you want to render over
	 * the default fluid renderer.
	 *
	 * <p>Note that this method must *only* return {@code true} if at least one
	 * face is tessellated. If no faces are tessellated this method must return
	 * {@code false}.
	 *
	 * @param pos The position in the world, of the fluid to render.
	 * @param world The world the fluid is in
	 * @param vertexConsumer The vertex consumer to tessellate the fluid in.
	 * @param blockState The block state being rendered.
	 * @param fluidState The fluid state being rendered.
	 */
	default void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
		((FluidRenderHandlerRegistryImpl) FluidRenderHandlerRegistry.INSTANCE).renderFluid(pos, world, vertexConsumer, blockState, fluidState);
	}

	/**
	 * Look up your Fluid's sprites from the texture atlas. Called when the
	 * fluid renderer reloads its textures. This is a convenient way of
	 * reloading and does not require an advanced resource manager reload
	 * listener.
	 *
	 * <p>The "fabric-textures" module contains sprite rendering facilities,
	 * which may come in handy here.
	 *
	 * @param textureAtlas The blocks texture atlas, provided for convenience.
	 */
	default void reloadTextures(SpriteAtlasTexture textureAtlas) {
	}
}
