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

package net.fabricmc.fabric.impl.blockrenderlayer;

import java.util.function.BiConsumer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;

public class BlockRenderLayerMapImpl implements BlockRenderLayerMap {
	private BlockRenderLayerMapImpl() {}
	
	@Override
	public void putBlock(Block block, RenderLayer renderLayer) {
		if (block == null) {
			LOG.warn("Ignoring request to map null block to BlockRenderLayer");
		} else if (renderLayer == null) {
			LOG.warn("Ignoring request to map block " + block.toString() + " to null BlockRenderLayer");
		} else {
			blockHandler.accept(block, renderLayer);
		}
	}

	@Override
	public void putItem(Item item, RenderLayer renderLayer) {
		if (item == null) {
			LOG.warn("Ignoring request to map null item to BlockRenderLayer");
		} else if (renderLayer == null) {
			LOG.warn("Ignoring request to map item " + item.toString() + " to null BlockRenderLayer");
		} else {
			itemHandler.accept(item, renderLayer);
		}
	}

	@Override
	public void putFluid(Fluid fluid, RenderLayer renderLayer) {
		if (fluid == null) {
			LOG.warn("Ignoring request to map null fluid to BlockRenderLayer");
		} else if (renderLayer == null) {
			LOG.warn("Ignoring request to map fluid " + fluid.toString() + " to null BlockRenderLayer");	
		} else {
			fluidHandler.accept(fluid, renderLayer);
		}
	}
	
	public static final BlockRenderLayerMap INSTANCE = new BlockRenderLayerMapImpl();
	
	private static final Logger LOG = LogManager.getLogger();
	
	// These should never be used before our Mixin populates them because a non-null BRL instance is 
	// a required parameter of our methods. They are given dummy consumers that log
	// warnings in case something goes wrong.
	
	private static BiConsumer<Block, RenderLayer> blockHandler = (b, l) -> {
		LOG.warn("Unable to map Block {} to BlockRenderLayer. Mapping handler not ready.", b);
	};

	private static BiConsumer<Item, RenderLayer> itemHandler = (b, l) -> {
		LOG.warn("Unable to map Item {} to BlockRenderLayer. Mapping handler not ready.", b);
	};

	private static BiConsumer<Fluid, RenderLayer> fluidHandler = (f, b) -> {
		LOG.warn("Unable to map Fluid {} to BlockRenderLayer. Mapping handler not ready.", f);
	};

	public static void initialize(BiConsumer<Block, RenderLayer> blockHandlerIn, BiConsumer<Item, RenderLayer> itemHandlerIn, BiConsumer<Fluid, RenderLayer> fluidHandlerIn) {
		blockHandler = blockHandlerIn;
		itemHandler = itemHandlerIn;
		fluidHandler = fluidHandlerIn;
	}
}
