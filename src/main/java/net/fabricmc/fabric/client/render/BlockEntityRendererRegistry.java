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
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for registering BlockEntityRenderers.
 */
public class BlockEntityRendererRegistry {
	public static final BlockEntityRendererRegistry INSTANCE = new BlockEntityRendererRegistry();
	private Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderers = null;
	private Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderersTmp = new HashMap<>();

	private BlockEntityRendererRegistry() {

	}

	public void initialize(Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> map) {
		if (renderers != null && renderers != map) {
			throw new RuntimeException("Tried to set renderers twice!");
		}

		if (renderers == map) {
			return;
		}

		renderers = map;
		for (BlockEntityRenderer renderer : renderersTmp.values()) {
			renderer.setRenderManager(BlockEntityRenderDispatcher.INSTANCE);
		}
		renderers.putAll(renderersTmp);
		renderersTmp = null;
	}

	public void register(Class<? extends BlockEntity> blockEntityClass, BlockEntityRenderer<? extends BlockEntity> blockEntityRenderer) {
		// TODO: warn on duplicate
		if (renderers != null) {
			renderers.put(blockEntityClass, blockEntityRenderer);
			blockEntityRenderer.setRenderManager(BlockEntityRenderDispatcher.INSTANCE);
		} else {
			renderersTmp.put(blockEntityClass, blockEntityRenderer);
		}
	}
}
