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

package net.fabricmc.fabric.test.model.loading;

import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class ModelTestModClient implements ClientModInitializer {
	public static final String ID = "fabric-model-loading-api-v1-testmod";

	public static final Identifier MODEL_ID = new Identifier(ID, "half_red_sand");

	static class DownQuadRemovingModel extends ForwardingBakedModel {
		public DownQuadRemovingModel(BakedModel model) {
			wrapped = model;
		}

		@Override
		public List<BakedQuad> getQuads(BlockState blockState, Direction face, Random rand) {
			return face == Direction.DOWN ? ImmutableList.of() : super.getQuads(blockState, face, rand);
		}
	}

	@Override
	public void onInitializeClient() {
		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.addModel(MODEL_ID);
			// remove bottom face of dirt blocks
			pluginContext.onBakedModelLoad().register((model, context) -> {
				if(context.location().getPath().equals("block/dirt")) {
					return new DownQuadRemovingModel(model);
				} else {
					return model;
				}
			});
			// make fences with west: true and everything else false appear to be a missing model visually
			ModelIdentifier fenceId = BlockModels.getModelId(Blocks.OAK_FENCE.getDefaultState().with(HorizontalConnectingBlock.WEST, true));
			pluginContext.onUnbakedModelLoad().register((model, context) -> {
				if(fenceId.equals(context.location())) {
					return context.loader().getOrLoadModel(ModelLoader.MISSING_ID);
				}
				return model;
			});
			// make brown glazed terracotta appear to be a missing model visually, but without affecting the item, by using pre-bake
			// using load here would make the item also appear missing
			pluginContext.onUnbakedModelPreBake().register((model, context) -> {
				if(context.location().getPath().equals("block/brown_glazed_terracotta")) {
					return context.loader().getOrLoadModel(ModelLoader.MISSING_ID);
				}
				return model;
			});
		});

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(SpecificModelReloadListener.INSTANCE);

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if (entityRenderer instanceof PlayerEntityRenderer) {
				registrationHelper.register(new BakedModelFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, SpecificModelReloadListener.INSTANCE::getSpecificModel));
			}
		});
	}
}
