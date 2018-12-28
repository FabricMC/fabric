package net.fabricmc.fabric.model;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.events.client.ClientTickEvent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.function.Function;

public class ModelModClient implements ClientModInitializer {
	private static BakedModel bakedModel;

	@Override
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerAppender((manager, out) -> {
			System.out.println("--- ModelAppender called! ---");
			out.accept(new ModelIdentifier("fabric:model#custom"));
		});

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> ((modelId, context) -> {
			if (modelId.getVariant().equals("custom") && modelId.getNamespace().equals("fabric")) {
				System.out.println("--- ModelVariantProvider called! ---");
				return context.loadModel(new Identifier("fabric:custom"));
			} else {
				return null;
			}
		}));

		ModelLoadingRegistry.INSTANCE.registerResourceProvider(manager -> ((id, context) -> {
			if (id.toString().equals("fabric:custom")) {
				return context.loadModel(new Identifier("fabric:custom2"));
			} else if (id.toString().equals("fabric:custom2")) {
				System.out.println("--- ModelResourceProvider called! ---");
				return new UnbakedModel() {
					@Override
					public Collection<Identifier> getModelDependencies() {
						return Collections.emptyList();
					}

					@Override
					public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> var1, Set<String> var2) {
						return Collections.emptyList();
					}

					@Override
					public BakedModel bake(ModelLoader var1, Function<Identifier, Sprite> var2, ModelRotationContainer var3) {
						System.out.println("--- Model baked! ---");

						return bakedModel = new BakedModel() {
							@Override
							public List<BakedQuad> getQuads(BlockState var1, Direction var2, Random var3) {
								return Collections.emptyList();
							}

							@Override
							public boolean useAmbientOcclusion() {
								return false;
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
								return ModelTransformation.ORIGIN;
							}

							@Override
							public ModelItemPropertyOverrideList getItemPropertyOverrides() {
								return ModelItemPropertyOverrideList.ORIGIN;
							}
						};
					}
				};
			} else {
				return null;
			}
		}));

		ClientTickEvent.CLIENT.register((client) -> {
			if (client.getBakedModelManager().getModel(new ModelIdentifier("fabric:model#custom"))
				== bakedModel && bakedModel != null) {
				System.out.println("--- MODEL LOADED! ---");
			} else {
				System.out.println("--- MODEL NOT LOADED! ---");
			}
		});
	}
}
