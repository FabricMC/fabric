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

package net.fabricmc.fabric.impl.client.rendering.fluid;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.BiomeKeys;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.client.texture.RegistrableTexture;

public class FluidRenderHandlerRegistryImpl implements FluidRenderHandlerRegistry {
	@SuppressWarnings("ConstantConditions")
	private static final int DEFAULT_WATER_COLOR = BuiltinRegistries.BIOME.get(BiomeKeys.OCEAN).getWaterColor();
	private final Map<Fluid, FluidRenderHandler> handlers = new IdentityHashMap<>();
	private final Map<Fluid, FluidRenderHandler> modHandlers = new IdentityHashMap<>();
	private final Map<Block, Boolean> overlayBlocks = new IdentityHashMap<>();

	private FluidRenderer fluidRenderer;

	public FluidRenderHandlerRegistryImpl() {
	}

	@Override
	public FluidRenderHandler get(Fluid fluid) {
		return handlers.get(fluid);
	}

	public FluidRenderHandler getOverride(Fluid fluid) {
		return modHandlers.get(fluid);
	}

	@Override
	public void register(Fluid fluid, FluidRenderHandler renderer) {
		handlers.put(fluid, renderer);
		modHandlers.put(fluid, renderer);

		RegistrableTexture[] textures = renderer.getFluidTextures();

		if (textures != null) {
			for (RegistrableTexture texture : textures) {
				if (texture.isToBeRegistered()) {
					ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
						//The textures are added in a Set, so there will be no duplicates.
						registry.register(texture.getIdentifier());
					});
				}
			}
		}
	}

	@Override
	public void setBlockTransparency(Block block, boolean transparent) {
		overlayBlocks.put(block, transparent);
	}

	@Override
	public boolean isBlockTransparent(Block block) {
		return overlayBlocks.computeIfAbsent(block, k -> k instanceof TransparentBlock || k instanceof LeavesBlock);
	}

	public void onFluidRendererReload(FluidRenderer renderer, Sprite[] waterSprites, Sprite[] lavaSprites, Sprite waterOverlay) {
		fluidRenderer = renderer;

		Sprite[] waterSpritesFull = {waterSprites[0], waterSprites[1], waterOverlay};
		FluidRenderHandler waterHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return waterSpritesFull;
			}

			@Override
			public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
				if (view != null && pos != null) {
					return BiomeColors.getWaterColor(view, pos);
				} else {
					return DEFAULT_WATER_COLOR;
				}
			}
		};

		//noinspection Convert2Lambda
		FluidRenderHandler lavaHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return lavaSprites;
			}
		};

		register(Fluids.WATER, waterHandler);
		register(Fluids.FLOWING_WATER, waterHandler);
		register(Fluids.LAVA, lavaHandler);
		register(Fluids.FLOWING_LAVA, lavaHandler);
		handlers.putAll(modHandlers);

		SpriteAtlasTexture texture = MinecraftClient.getInstance()
				.getBakedModelManager()
				.getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

		for (FluidRenderHandler handler : handlers.values()) {
			handler.reloadTextures(texture);
		}
	}

	public boolean renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, FluidState state) {
		return fluidRenderer.render(world, pos, vertexConsumer, state);
	}
}
