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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.model.loading.BakerImplHooks;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoaderHooks;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;

@Mixin(targets = "net/minecraft/client/render/model/ModelLoader$BakerImpl")
abstract class ModelLoaderBakerImplMixin implements BakerImplHooks {
	@Shadow
	@Final
	private ModelLoader field_40571;
	@Shadow
	@Final
	private Function<SpriteIdentifier, Sprite> textureGetter;

	@WrapOperation(method = "bake(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/ModelBakeSettings;)Lnet/minecraft/client/render/model/BakedModel;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader$BakerImpl;bake(Lnet/minecraft/client/render/model/UnbakedModel;Lnet/minecraft/client/render/model/ModelBakeSettings;)Lnet/minecraft/client/render/model/BakedModel;"))
	private BakedModel wrapInnerBake(@Coerce Baker self, UnbakedModel unbakedModel, ModelBakeSettings settings, Operation<BakedModel> operation, Identifier id) {
		ModelLoadingEventDispatcher dispatcher = ((ModelLoaderHooks) this.field_40571).fabric_getDispatcher();
		unbakedModel = dispatcher.modifyModelBeforeBake(unbakedModel, id, null, textureGetter, settings, self);
		BakedModel model = operation.call(self, unbakedModel, settings);
		return dispatcher.modifyModelAfterBake(model, id, null, unbakedModel, textureGetter, settings, self);
	}

	@Override
	public Function<SpriteIdentifier, Sprite> fabric_getTextureGetter() {
		return textureGetter;
	}
}
