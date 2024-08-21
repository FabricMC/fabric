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

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_10097;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingConstants;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;

@Mixin(class_10097.class)
abstract class class_10097Mixin {
	@Unique
	@Nullable
	private ModelLoadingEventDispatcher fabric_eventDispatcher;

	@Shadow
	abstract UnbakedModel method_62638(Identifier identifier);

	@Shadow
	abstract void method_62635(ModelIdentifier modelIdentifier, UnbakedModel unbakedModel);

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onReturnInit(CallbackInfo ci) {
		fabric_eventDispatcher = ModelLoadingEventDispatcher.CURRENT.get();
	}

	@Inject(method = "method_62632", at = @At("RETURN"))
	private void onAddStandardModels(BlockStatesLoader.class_10095 blockStateModels, CallbackInfo ci) {
		if (fabric_eventDispatcher == null) {
			return;
		}

		fabric_eventDispatcher.addExtraModels(id -> {
			ModelIdentifier modelId = ModelLoadingConstants.toResourceModelId(id);
			UnbakedModel unbakedModel = method_62638(id);
			method_62635(modelId, unbakedModel);
		});
	}

	@ModifyVariable(method = "method_62640", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
	@Nullable
	private UnbakedModel onLoadResourceModel(@Nullable UnbakedModel model, Identifier id) {
		if (fabric_eventDispatcher == null) {
			return model;
		}

		UnbakedModel resolvedModel = fabric_eventDispatcher.resolveModel(id);

		if (resolvedModel != null) {
			model = resolvedModel;
		}

		return fabric_eventDispatcher.modifyModelOnLoad(model, id, null);
	}

	@ModifyVariable(method = "method_62635", at = @At("HEAD"), argsOnly = true)
	private UnbakedModel onAddTopLevelModel(UnbakedModel model, ModelIdentifier modelId) {
		if (fabric_eventDispatcher == null) {
			return model;
		}

		if (ModelLoadingConstants.isResourceModelId(modelId)) {
			return model;
		}

		return fabric_eventDispatcher.modifyModelOnLoad(model, null, modelId);
	}
}
