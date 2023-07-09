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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
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

	@Unique
	private ModelLoadingEventDispatcher fabric_eventDispatcher;
	@Unique
	private int fabric_guardGetOrLoadModel = 0;

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
			fabric_eventDispatcher = new ModelLoadingEventDispatcher((ModelLoader) (Object) this);
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

	@Inject(method = "getOrLoadModel", at = @At("HEAD"))
	private void fabric_preventNestedGetOrLoadModel(Identifier id, CallbackInfoReturnable<UnbakedModel> cir) {
		if (fabric_guardGetOrLoadModel > 0) {
			throw new IllegalStateException("ModelLoader#getOrLoadModel called from a ModelResolverTmp or ModelModifier.OnBake instance. This is not allowed to prevent subtle model loading errors. Use getOrLoadModel from the context instead.");
		}
	}

	@Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
	private void onLoadModel(Identifier id, CallbackInfo ci) {
		// Prevent calls to getOrLoadModel from loadModel as it will cause problems.
		// Mods should call getOrLoadModel on the ModelResolverTmp.Context instead.
		fabric_guardGetOrLoadModel++;

		try {
			if (fabric_eventDispatcher.resolveModel(id)) {
				ci.cancel();
			}
		} finally {
			fabric_guardGetOrLoadModel--;
		}
	}

	@ModifyVariable(method = "putModel", at = @At("HEAD"), argsOnly = true)
	private UnbakedModel onPutModel(UnbakedModel model, Identifier id) {
		fabric_guardGetOrLoadModel++;

		try {
			return fabric_eventDispatcher.modifyModelOnLoad(id, model);
		} finally {
			fabric_guardGetOrLoadModel--;
		}
	}

	@Override
	public ModelLoadingEventDispatcher fabric_getDispatcher() {
		return fabric_eventDispatcher;
	}

	/**
	 * Unlike getOrLoadModel, this method supports nested model loading.
	 *
	 * <p>Vanilla does not due to the iteration over modelsToLoad which causes models to be resolved multiple times,
	 * probably leading to crashes.
	 */
	@Override
	public UnbakedModel fabric_getOrLoadModel(Identifier id) {
		if (this.unbakedModels.containsKey(id)) {
			return this.unbakedModels.get(id);
		}

		if (!modelsToLoad.add(id)) {
			throw new IllegalStateException("Circular reference while loading " + id);
		}

		try {
			loadModel(id);
		} finally {
			modelsToLoad.remove(id);
		}

		return unbakedModels.get(id);
	}

	@Override
	public void fabric_putModel(Identifier id, UnbakedModel model) {
		putModel(id, model);
	}
}
