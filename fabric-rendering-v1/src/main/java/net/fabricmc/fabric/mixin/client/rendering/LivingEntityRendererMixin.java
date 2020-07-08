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

package net.fabricmc.fabric.mixin.client.rendering;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

import net.fabricmc.fabric.api.client.rendering.v1.RegisterFeatureRendererCallback;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
	@Shadow
	protected abstract boolean addFeature(FeatureRenderer<T, M> feature);

	@SuppressWarnings({"unchecked", "ConstantConditions"})
	@Inject(method = "<init>", at = @At("TAIL"))
	private void registerFeatures(EntityRenderDispatcher dispatcher, M model, float shadowRadius, CallbackInfo ci) {
		RegisterFeatureRendererCallback.EVENT.invoker().registerFeatureRenderers((LivingEntityRenderer<T, M>) (Object) this, featureRenderer -> {
			Objects.requireNonNull(featureRenderer, "Feature Renderer cannot be null");
			this.addFeature((FeatureRenderer<T, M>) featureRenderer);
		});
	}
}

