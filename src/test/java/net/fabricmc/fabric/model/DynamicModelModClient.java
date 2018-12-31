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

package net.fabricmc.fabric.model;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.DynamicBakedModel;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.RenderCacheView;
import net.fabricmc.fabric.events.client.ClientTickEvent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.sortme.SomethingDirectionSomethingQuadBakery;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.function.Function;

public class DynamicModelModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> ((modelId, context) -> {
			if (modelId.getPath().equals("stone")) {
				System.out.println("--- ModelVariantProvider called! ---");
				return context.loadModel(new Identifier("fabric:stone_patched"));
			} else {
				return null;
			}
		}));

		ModelLoadingRegistry.INSTANCE.registerResourceProvider(manager -> ((id, context) -> {
			if (id.toString().equals("fabric:stone_patched")) {
				System.out.println("--- ModelResourceProvider called! ---");
				return new UnbakedModel() {
					UnbakedModel unbakedParent;

					@Override
					public Collection<Identifier> getModelDependencies() {
						return Collections.singletonList(new Identifier("minecraft:block/stone"));
					}

					@Override
					public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> var1, Set<String> var2) {
						unbakedParent = var1.apply(new Identifier("minecraft:block/stone"));
						return unbakedParent.getTextureDependencies(var1, var2);
					}

					@Override
					public BakedModel bake(ModelLoader var1, Function<Identifier, Sprite> var2, ModelRotationContainer var3) {
						BakedModel parent = unbakedParent.bake(
							var1, var2, var3);

						return new DynamicBakedModel<Object>() {
							@Override
							public boolean useAmbientOcclusion() {
								return true;
							}

							@Override
							public boolean hasDepthInGui() {
								return false;
							}

							@Override
							public boolean isBuiltin() {
								return false;
							}

							@Override
							public Sprite getSprite() {
								return MinecraftClient.getInstance().getSpriteAtlas().getSprite("missingno");
							}

							@Override
							public ModelTransformation getTransformations() {
								return parent.getTransformations();
							}

							@Override
							public ModelItemPropertyOverrideList getItemPropertyOverrides() {
								return ModelItemPropertyOverrideList.ORIGIN;
							}

							@Override
							public Object getRenderData(BlockState state, RenderCacheView view, BlockPos pos) {
								return new Object();
							}

							@Override
							public List<BakedQuad> getQuads(Object data, BlockRenderLayer layer, BlockState state, Direction face, Random random) {
								if (data == null) {
									return Collections.emptyList();
								} else {
									return parent.getQuads(state, face, random);
								}
							}
						};
					}
				};
			} else {
				return null;
			}
		}));
	}
}
