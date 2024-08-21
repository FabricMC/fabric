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

import java.util.function.Function;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.MissingModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;

import net.fabricmc.fabric.impl.client.model.loading.BakerImplHooks;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoaderHooks;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingConstants;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;

@Mixin(ModelBaker.class)
abstract class ModelLoaderMixin implements ModelLoaderHooks {
	@Unique
	@Nullable
	private ModelLoadingEventDispatcher fabric_eventDispatcher;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onReturnInit(CallbackInfo ci) {
		fabric_eventDispatcher = ModelLoadingEventDispatcher.CURRENT.get();
	}

	@WrapOperation(method = "method_61072", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelBaker$BakerImpl;bake(Lnet/minecraft/client/render/model/UnbakedModel;Lnet/minecraft/client/render/model/ModelBakeSettings;)Lnet/minecraft/client/render/model/BakedModel;"))
	private BakedModel wrapSingleOuterBake(@Coerce Baker baker, UnbakedModel unbakedModel, ModelBakeSettings settings, Operation<BakedModel> operation, ModelBaker.SpriteGetter spriteGetter, ModelIdentifier id) {
		if (fabric_eventDispatcher == null) {
			return operation.call(baker, unbakedModel, settings);
		}

		if (ModelLoadingConstants.isResourceModelId(id) || id.equals(MissingModel.MODEL_ID)) {
			// Call the baker instead of the operation to ensure the baked model is cached and doesn't end up going
			// through events twice.
			// This ignores the UnbakedModel in field_53662 (top-level model map) but it should be the same as the one in field_53663 (resource model map).
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
}
