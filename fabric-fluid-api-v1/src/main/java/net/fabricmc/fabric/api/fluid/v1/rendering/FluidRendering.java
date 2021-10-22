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

package net.fabricmc.fabric.api.fluid.v1.rendering;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Provides basic fluid rendering.
 */
public class FluidRendering {
	/**
	 * Block atlas texture identifier.
	 */
	public static final Identifier BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");

	/**
	 * Render the fluid with the given texture id.
	 * <p>
	 * The texture paths, with the given id, are:
	 * <p>
	 * -> Still variant: block/[TEXTURE_ID]_still
	 * <p>
	 * -> Flowing variant: block/[TEXTURE_ID]_flow
	 * @param still The still variant of the fluid.
	 * @param flowing The flowing variant of the fluid.
	 * @param textureID The identifier of the texture to use.
	 */
	public static void render(final Fluid still, final Fluid flowing, final @NotNull Identifier textureID) {
		render(still, flowing, textureID, -1);
	}

	/**
	 * Render the fluid with the given texture id.
	 * <p>
	 * The textures must be placed into these paths:
	 * <p>
	 * > Still variant: <b>block/[TEXTURE_ID.PATH]_still</b>
	 * <p>
	 * > Flowing variant: <b>block/[TEXTURE_ID.PATH]_flow</b>
	 * @param still The still variant of the fluid.
	 * @param flowing The flowing variant of the fluid.
	 * @param textureID The identifier of the texture to use.
	 * @param color The color used to recolorize the fluid texture.
	 */
	public static void render(final Fluid still, final Fluid flowing, final @NotNull Identifier textureID, final int color) {
		//Generate the sprites identifiers from the given id
		final Identifier stillSpriteId = new Identifier(textureID.getNamespace(), "block/" + textureID.getPath() + "_still");
		final Identifier flowingSpriteId = new Identifier(textureID.getNamespace(), "block/" + textureID.getPath() + "_flow");

		//If the sprites are not already present, add them to the block atlas
		ClientSpriteRegistryCallback.event(BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(stillSpriteId);
			registry.register(flowingSpriteId);
		});

		//Get the fluid id from registry
		final Identifier fluidId = Registry.FLUID.getId(still);
		final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

		//Get the sprites resources
		final Sprite[] fluidSprites = { null, null };

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return listenerId;
			}

			@Override
			public void reload(ResourceManager resourceManager) {
				final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(BLOCK_ATLAS_TEXTURE);

				//Get the sprites from the block atlas when resources are reloaded
				fluidSprites[0] = atlas.apply(stillSpriteId);
				fluidSprites[1] = atlas.apply(flowingSpriteId);
			}
		});

		//The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
		final FluidRenderHandler renderHandler = new FluidRenderHandler()
		{
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return fluidSprites;
			}

			@Override
			public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
				return color;
			}
		};

		//Register the handlers
		FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
	}
}
