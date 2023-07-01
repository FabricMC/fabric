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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.model.loading.ModelLoaderHooks;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingEventDispatcher;

@Mixin(targets = "net/minecraft/client/render/model/ModelLoader$BakerImpl")
public class ModelLoaderBakerImplMixin {
	@Shadow
	@Final
	private ModelLoader field_40571;

	@Redirect(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader$BakerImpl;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;"))
	private UnbakedModel invokeModifyBeforeBake(@Coerce Baker baker, Identifier id) {
		UnbakedModel model = baker.getOrLoadModel(id);
		ModelLoadingEventDispatcher dispatcher = ((ModelLoaderHooks) this.field_40571).fabric_getDispatcher();
		return dispatcher.modifyModelBeforeBake(id, model);
	}

	@Redirect(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/UnbakedModel;bake(Lnet/minecraft/client/render/model/Baker;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/BakedModel;"))
	private BakedModel invokeModifyAfterBake(UnbakedModel unbakedModel, Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier identifier) {
		BakedModel model = unbakedModel.bake(baker, textureGetter, settings, identifier);
		ModelLoadingEventDispatcher dispatcher = ((ModelLoaderHooks) this.field_40571).fabric_getDispatcher();
		return dispatcher.modifyModelAfterBake(identifier, unbakedModel, model, textureGetter, settings, baker);
	}

	@Redirect(method = "bake", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/JsonUnbakedModel;bake(Lnet/minecraft/client/render/model/Baker;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/model/BakedModel;"))
	private BakedModel invokeModifyAfterBake(JsonUnbakedModel unbakedModel, Baker baker, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier identifier, boolean hasDepth) {
		BakedModel model = unbakedModel.bake(baker, parent, textureGetter, settings, identifier, hasDepth);
		ModelLoadingEventDispatcher dispatcher = ((ModelLoaderHooks) this.field_40571).fabric_getDispatcher();
		return dispatcher.modifyModelAfterBake(identifier, unbakedModel, model, textureGetter, settings, baker);
	}
}
