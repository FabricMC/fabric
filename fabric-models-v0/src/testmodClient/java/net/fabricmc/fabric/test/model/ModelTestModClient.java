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

package net.fabricmc.fabric.test.model;

import net.fabricmc.fabric.api.client.model.ModelLoadObserver;

import net.minecraft.block.Blocks;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.function.Function;

public class ModelTestModClient implements ClientModInitializer {
	public static final String ID = "fabric-models-v0-testmod";

	public static final Identifier MODEL_ID = new Identifier(ID, "half_red_sand");

	@Override
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(MODEL_ID);
		});

		// Remove the bottom face on dirt blocks
		ModelLoadingRegistry.INSTANCE.registerLoadObserver(manager -> new ModelLoadObserver() {
			@Override
			public BakedModel onBakedModelLoad(Identifier location, UnbakedModel unbakedModel, BakedModel originalModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker, ModelLoader loader) {
				if(location.getPath().equals("block/dirt")) {
					// modders, treating quad list as mutable can break performance mods like FerriteCore, this is being
					// done here purely for test purposes
					List<BakedQuad> quads = originalModel.getQuads(Blocks.DIRT.getDefaultState(), Direction.DOWN, Random.create());
					quads.clear();
				}
				return originalModel;
			}
		});

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(SpecificModelReloadListener.INSTANCE);

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if (entityRenderer instanceof PlayerEntityRenderer) {
				registrationHelper.register(new BakedModelFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, SpecificModelReloadListener.INSTANCE::getSpecificModel));
			}
		});
	}
}
