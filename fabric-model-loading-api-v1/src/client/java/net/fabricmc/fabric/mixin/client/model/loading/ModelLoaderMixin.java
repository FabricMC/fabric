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

import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.model.loading.ModelLoaderHooks;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin implements ModelLoaderHooks {
	// this is the first one
	@Final
	@Shadow
	public static ModelIdentifier MISSING_ID;
	@Final
	@Shadow
	private Set<Identifier> modelsToLoad;
	@Final
	@Shadow
	private Map<Identifier, UnbakedModel> unbakedModels;
	@Shadow
	@Final
	private Map<Identifier, UnbakedModel> modelsToBake;

	private ModelLoadingEventDispatcher fabric_eventDispatcher;

	@Shadow
	private void addModel(ModelIdentifier id) {
	}

	@Shadow
	public abstract UnbakedModel getOrLoadModel(Identifier id);

	@Shadow
	private void loadModel(Identifier id) {
	}

	@Shadow
	private void putModel(Identifier id, UnbakedModel unbakedModel) {
	}

	@Inject(method = "addModel", at = @At("HEAD"))
	private void onAddModel(ModelIdentifier id, CallbackInfo info) {
		if (id == MISSING_ID) {
			ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
			fabric_eventDispatcher = new ModelLoadingEventDispatcher((ModelLoader) (Object) this, resourceManager);
			fabric_eventDispatcher.addExtraModels(this::addModel);
		}
	}

	@Unique
	private void addModel(Identifier id) {
		if (id instanceof ModelIdentifier) {
			addModel((ModelIdentifier) id);
		} else {
			// The vanilla addModel method is arbitrarily limited to ModelIdentifiers,
			// but it's useful to tell the game to just load and bake a direct model path as well.
			// Replicate the vanilla logic of addModel here.
			UnbakedModel unbakedModel = getOrLoadModel(id);
			this.unbakedModels.put(id, unbakedModel);
			this.modelsToBake.put(id, unbakedModel);
		}
	}

	@Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
	private void onLoadModel(Identifier id, CallbackInfo ci) {
		UnbakedModel customModel = fabric_eventDispatcher.resolveModel(id);

		if (customModel != null) {
			putModel(id, customModel);
			ci.cancel();
		}
	}

	@ModifyVariable(method = "putModel", at = @At("HEAD"), argsOnly = true)
	private UnbakedModel onPutModel(UnbakedModel model, Identifier identifier) {
		return fabric_eventDispatcher.modifyModelOnLoad(identifier, model);
	}

	@Override
	public ModelLoadingEventDispatcher fabric_getDispatcher() {
		return fabric_eventDispatcher;
	}
}
