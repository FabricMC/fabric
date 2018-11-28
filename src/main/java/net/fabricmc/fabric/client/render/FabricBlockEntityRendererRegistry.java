/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.client.render;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

import java.util.Map;

public class FabricBlockEntityRendererRegistry {
	public static final FabricBlockEntityRendererRegistry INSTANCE = new FabricBlockEntityRendererRegistry();
	private Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> blockEntityRenderers;

	private FabricBlockEntityRendererRegistry() {

	}

	public void setBlockEntityRendererMap(Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> map) {
		if (blockEntityRenderers != null && blockEntityRenderers != map) {
			throw new RuntimeException("Tried to set blockEntityRenderers twice!");
		}

		blockEntityRenderers = map;
	}

	public void register(Class<? extends BlockEntity> blockEntityClass, BlockEntityRenderer<? extends BlockEntity> blockEntityRenderer) {
		// TODO: warn on duplicate
		blockEntityRenderers.put(blockEntityClass, blockEntityRenderer);
		blockEntityRenderer.setRenderManager(BlockEntityRenderManager.instance);
	}
}
