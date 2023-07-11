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

package net.fabricmc.fabric.test.renderer.simple.client;

import static net.fabricmc.fabric.test.renderer.simple.RendererTest.id;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.test.renderer.simple.FrameBlock;
import net.fabricmc.fabric.test.renderer.simple.RendererTest;

public final class RendererClientTest implements ClientModInitializer {
	private static final Set<Identifier> FRAME_MODELS = new HashSet<>();

	@Override
	public void onInitializeClient() {
		for (FrameBlock frameBlock : RendererTest.FRAMES) {
			// We don't specify a material for the frame mesh,
			// so it will use the default material, i.e. the one from BlockRenderLayerMap.
			BlockRenderLayerMap.INSTANCE.putBlock(frameBlock, RenderLayer.getCutoutMipped());

			String itemPath = Registries.ITEM.getId(frameBlock.asItem()).getPath();
			FRAME_MODELS.add(id("item/" + itemPath));
		}

		FRAME_MODELS.add(id("block/frame"));

		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.resolveModel().register(context -> {
				if (FRAME_MODELS.contains(context.id())) {
					return new FrameUnbakedModel();
				}

				return null;
			});

			pluginContext.registerBlockStateResolver(RendererTest.PILLAR, context -> {
				context.setModel(context.block().getDefaultState(), new PillarUnbakedModel());
			});
		});
	}
}
