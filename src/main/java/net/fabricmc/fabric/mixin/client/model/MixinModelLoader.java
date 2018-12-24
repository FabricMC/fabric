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

package net.fabricmc.fabric.mixin.client.model;

import net.fabricmc.fabric.impl.client.model.ModelLoaderHooks;
import net.fabricmc.fabric.impl.client.model.ModelLoadingRegistryImpl;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelLoader.class)
public class MixinModelLoader implements ModelLoaderHooks {
	// this is the first one
	@Shadow
	public static ModelIdentifier MISSING;
	@Shadow
	private ResourceManager resourceContainer;

	private ModelLoadingRegistryImpl.LoaderInstance fabric_mlrLoaderInstance;

	@Shadow
	private void addModel(ModelIdentifier id) {

	}

	@Shadow
	private void putModel(Identifier id, UnbakedModel unbakedModel) {

	}

	@Inject(at = @At("HEAD"), method = "loadModel", cancellable = true)
	private void loadModel(Identifier id, CallbackInfo ci) {
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
			ModelLoaderHooks hooks = (ModelLoaderHooks) (Object) this;

			fabric_mlrLoaderInstance = ModelLoadingRegistryImpl.begin((ModelLoader) (Object) this, resourceContainer);
			fabric_mlrLoaderInstance.onModelPopulation(hooks::fabric_addModel);
		}
	}

	@Inject(at = @At("RETURN"), method = "<init>")
	private void initFinishedHook(CallbackInfo info) {
		//noinspection ConstantConditions
		fabric_mlrLoaderInstance.finish();
	}

	@Override
	public void fabric_addModel(ModelIdentifier id) {
		addModel(id);
	}
}
