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

package net.fabricmc.fabric.mixin.client.model;

import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.model.ModelLoaderHooks;
import net.fabricmc.fabric.impl.client.model.ModelLoadingRegistryImpl;

@Mixin(ModelLoader.class)
public abstract class MixinModelLoader implements ModelLoaderHooks {
	// this is the first one
	@Shadow
	public static ModelIdentifier MISSING;
	@Shadow
	private ResourceManager resourceManager;
	@Shadow
	private Set<Identifier> modelsToLoad;
	@Shadow
	private Map<Identifier, UnbakedModel> unbakedModels;
	@Shadow
	@Final
	private Map<Identifier, UnbakedModel> modelsToBake;

	private ModelLoadingRegistryImpl.LoaderInstance fabric_mlrLoaderInstance;

	@Shadow
	private void addModel(ModelIdentifier id) { }

	@Shadow
	private void putModel(Identifier id, UnbakedModel unbakedModel) { }

	@Shadow
	private void loadModel(Identifier id) { }

	@Shadow public abstract UnbakedModel getOrLoadModel(Identifier id);

	@Inject(at = @At("HEAD"), method = "loadModel", cancellable = true)
	private void loadModelHook(Identifier id, CallbackInfo ci) {
		UnbakedModel customModel = fabric_mlrLoaderInstance.loadModelFromVariant(id);

		if (customModel != null) {
			putModel(id, customModel);
			ci.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "addModel")
	private void addModelHook(ModelIdentifier id, CallbackInfo info) {
		if (id == MISSING) {
			//noinspection RedundantCast
			ModelLoaderHooks hooks = this;

			fabric_mlrLoaderInstance = ModelLoadingRegistryImpl.begin((ModelLoader) (Object) this, resourceManager);
			fabric_mlrLoaderInstance.onModelPopulation(hooks::fabric_addModel);
		}
	}

	@Inject(at = @At("RETURN"), method = "<init>")
	private void initFinishedHook(CallbackInfo info) {
		//noinspection ConstantConditions
		fabric_mlrLoaderInstance.finish();
	}

	@Override
	public void fabric_addModel(Identifier id) {
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

	@Override
	public UnbakedModel fabric_loadModel(Identifier id) {
		if (!modelsToLoad.add(id)) {
			throw new IllegalStateException("Circular reference while loading " + id);
		}

		loadModel(id);
		modelsToLoad.remove(id);
		return unbakedModels.get(id);
	}
}
