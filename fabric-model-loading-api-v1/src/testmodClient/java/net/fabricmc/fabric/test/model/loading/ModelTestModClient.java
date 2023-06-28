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

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

public class ModelTestModClient implements ClientModInitializer {
	public static final String ID = "fabric-model-loading-api-v1-testmod";

	public static final Identifier MODEL_ID = new Identifier(ID, "half_red_sand");

	static class DownQuadRemovingModel extends ForwardingBakedModel {
		DownQuadRemovingModel(BakedModel model) {
			wrapped = model;
		}

		@Override
		public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
			context.pushTransform(q -> q.cullFace() != Direction.DOWN);
			super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
			context.popTransform();
		}
	}

	@Override
	public void onInitializeClient() {
		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.addModels(MODEL_ID);
			// remove bottom face of gold blocks
			pluginContext.modifyModelAfterBake().register(ModelModifier.WRAP_PHASE, (model, context) -> {
				if (context.identifier().getPath().equals("block/gold_block")) {
					return new DownQuadRemovingModel(model);
				} else {
					return model;
				}
			});
			// make fences with west: true and everything else false appear to be a missing model visually
			ModelIdentifier fenceId = BlockModels.getModelId(Blocks.OAK_FENCE.getDefaultState().with(HorizontalConnectingBlock.WEST, true));
			pluginContext.modifyModelOnLoad().register(ModelModifier.OVERRIDE_PHASE, (model, context) -> {
				if (fenceId.equals(context.identifier())) {
					return context.loader().getOrLoadModel(ModelLoader.MISSING_ID);
				}

				return model;
			});
			// make brown glazed terracotta appear to be a missing model visually, but without affecting the item, by using pre-bake
			// using load here would make the item also appear missing
			pluginContext.modifyModelBeforeBake().register(ModelModifier.OVERRIDE_PHASE, (model, context) -> {
				if (context.identifier().getPath().equals("block/brown_glazed_terracotta")) {
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
