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

package net.fabricmc.fabric.mixin.client.model.loading;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.impl.client.model.loading.BakerImplHooks;
import net.fabricmc.fabric.impl.client.model.loading.BlockStatesLoaderHooks;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoaderHooks;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingConstants;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;

@Mixin(ModelLoader.class)
abstract class ModelLoaderMixin implements ModelLoaderHooks {
	@Final
	@Shadow
	private Set<Identifier> modelsToLoad;
	@Final
	@Shadow
	private Map<Identifier, UnbakedModel> unbakedModels;
	@Shadow
	@Final
	private Map<ModelIdentifier, UnbakedModel> modelsToBake;
	@Shadow
	@Final
	private UnbakedModel missingModel;

	@Unique
	private ModelLoadingEventDispatcher fabric_eventDispatcher;
	@Unique
	private final ObjectLinkedOpenHashSet<Identifier> modelLoadingStack = new ObjectLinkedOpenHashSet<>();

	@Shadow
	abstract UnbakedModel getOrLoadModel(Identifier id);

	@Shadow
	abstract void add(ModelIdentifier id, UnbakedModel unbakedModel);

	@Shadow
	abstract JsonUnbakedModel loadModelFromJson(Identifier id);

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BlockStatesLoader;load()V"))
	private void afterMissingModelInit(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates, CallbackInfo info, @Local BlockStatesLoader blockStatesLoader) {
		// Sanity check
		if (missingModel == null || !modelsToBake.containsKey(ModelLoader.MISSING_MODEL_ID)) {
			throw new AssertionError("Missing model not initialized. This is likely a Fabric API porting bug.");
		}

		// Add the missing model to the cache since vanilla doesn't. Mods may load/bake the missing model directly.
		unbakedModels.put(ModelLoader.MISSING_ID, missingModel);
		profiler.swap("fabric_plugins_init");

		fabric_eventDispatcher = new ModelLoadingEventDispatcher((ModelLoader) (Object) this, ModelLoadingPluginManager.CURRENT_PLUGINS.get());
		fabric_eventDispatcher.addExtraModels(this::addExtraModel);
		((BlockStatesLoaderHooks) blockStatesLoader).fabric_setLoadingOverride(fabric_eventDispatcher::loadBlockStateModels);
	}

	@Unique
	private void addExtraModel(Identifier id) {
		ModelIdentifier modelId = ModelLoadingConstants.toResourceModelId(id);
		UnbakedModel unbakedModel = getOrLoadModel(id);
		add(modelId, unbakedModel);
	}

	@Inject(method = "getOrLoadModel", at = @At("HEAD"), cancellable = true)
	private void allowRecursiveLoading(Identifier id, CallbackInfoReturnable<UnbakedModel> cir) {
		// If the stack is empty, this is the top-level call, so proceed as normal.
		if (!modelLoadingStack.isEmpty()) {
			if (unbakedModels.containsKey(id)) {
				cir.setReturnValue(unbakedModels.get(id));
			} else if (modelLoadingStack.contains(id)) {
				throw new IllegalStateException("Circular reference while loading model '" + id + "' (" + modelLoadingStack.stream().map(i -> i + "->").collect(Collectors.joining()) + id + ")");
			} else {
				UnbakedModel model = loadModel(id);
				unbakedModels.put(id, model);
				// These will be loaded at the top-level call.
				modelsToLoad.addAll(model.getModelDependencies());
				cir.setReturnValue(model);
			}
		}
	}

	// This is the call that needs to be redirected to support ModelResolvers, but it returns a JsonUnbakedModel.
	// Redirect it to always return null and handle the logic in a ModifyVariable right after the call.
	@Redirect(method = "getOrLoadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadModelFromJson(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"))
	private JsonUnbakedModel cancelLoadModelFromJson(ModelLoader self, Identifier id) {
		return null;
	}

	@ModifyVariable(method = "getOrLoadModel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/model/ModelLoader;loadModelFromJson(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"))
	private UnbakedModel doLoadModel(UnbakedModel model, @Local(ordinal = 1) Identifier id) {
		return loadModel(id);
	}

	@Unique
	private UnbakedModel loadModel(Identifier id) {
		modelLoadingStack.add(id);

		try {
			UnbakedModel model = fabric_eventDispatcher.resolveModel(id);

			if (model == null) {
				model = loadModelFromJson(id);
			}

			return fabric_eventDispatcher.modifyModelOnLoad(model, id, null);
		} finally {
			modelLoadingStack.removeLast();
		}
	}

	@ModifyVariable(method = "add", at = @At("HEAD"), argsOnly = true)
	private UnbakedModel onAdd(UnbakedModel model, ModelIdentifier id) {
		if (ModelLoadingConstants.isResourceModelId(id)) {
			return model;
		}

		return fabric_eventDispatcher.modifyModelOnLoad(model, null, id);
	}

	@WrapOperation(method = "method_61072(Lnet/minecraft/client/render/model/ModelLoader$SpriteGetter;Lnet/minecraft/client/util/ModelIdentifier;Lnet/minecraft/client/render/model/UnbakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader$BakerImpl;bake(Lnet/minecraft/client/render/model/UnbakedModel;Lnet/minecraft/client/render/model/ModelBakeSettings;)Lnet/minecraft/client/render/model/BakedModel;"))
	private BakedModel wrapSingleOuterBake(@Coerce Baker baker, UnbakedModel unbakedModel, ModelBakeSettings settings, Operation<BakedModel> operation, ModelLoader.SpriteGetter spriteGetter, ModelIdentifier id) {
		if (ModelLoadingConstants.isResourceModelId(id) || id.equals(ModelLoader.MISSING_MODEL_ID)) {
			// Call the baker instead of the operation to ensure the baked model is cached and doesn't end up going
			// through events twice.
			// This ignores the UnbakedModel in modelsToBake but it should be the same as the one in unbakedModels.
			return baker.bake(id.id(), settings);
		}

		Function<SpriteIdentifier, Sprite> textureGetter = ((BakerImplHooks) baker).fabric_getTextureGetter();
		unbakedModel = fabric_eventDispatcher.modifyModelBeforeBake(unbakedModel, null, id, textureGetter, settings, baker);
		BakedModel model = operation.call(baker, unbakedModel, settings);
		return fabric_eventDispatcher.modifyModelAfterBake(model, null, id, unbakedModel, textureGetter, settings, baker);
	}

	@Override
	public ModelLoadingEventDispatcher fabric_getDispatcher() {
		return fabric_eventDispatcher;
	}

	@Override
	public UnbakedModel fabric_getMissingModel() {
		return missingModel;
	}

	@Override
	public UnbakedModel fabric_getOrLoadModel(Identifier id) {
		return getOrLoadModel(id);
	}

	@Override
	public void fabric_add(ModelIdentifier id, UnbakedModel model) {
		add(id, model);
	}
}
