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

package net.fabricmc.fabric.api.client.rendereregistry.v1;

import java.util.HashMap;
import java.util.function.Function;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

import net.fabricmc.fabric.mixin.client.renderer.registry.MixinBlockEntityRenderDispatcherInvoker;

/**
 * Helper class for registering BlockEntityRenderers.
 */
public class BlockEntityRendererRegistry {
	public static final BlockEntityRendererRegistry INSTANCE = new BlockEntityRendererRegistry();
	private static final HashMap<BlockEntityType<?>, Function<BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<?>>> renderers = new HashMap<>();
	private static boolean hasRegistered = false;

	private BlockEntityRendererRegistry() {
	}

	public <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, Function<BlockEntityRenderDispatcher, BlockEntityRenderer<E>> blockEntityRenderer) {
		if (!hasRegistered) {
			renderers.put(blockEntityType, blockEntityRenderer);
		} else {
			((MixinBlockEntityRenderDispatcherInvoker) BlockEntityRenderDispatcher.INSTANCE).invoke_register(blockEntityType, blockEntityRenderer.apply(BlockEntityRenderDispatcher.INSTANCE));
		}
	}

	public static void onInitialRegistry() {
		hasRegistered = true;
	}

	public static HashMap<BlockEntityType<?>, Function<BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<?>>> getRenderers() {
		return renderers;
	}
}
