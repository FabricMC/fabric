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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;

@Mixin(CapeFeatureRenderer.class)
public class CapeFeatureRendererMixin {
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"), method = "render", require = 1, allow = 1, cancellable = true)
	public void injectCapeRenderCheck(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		if (!LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.invoker().allowCapeRender(abstractClientPlayerEntity)) {
			ci.cancel();
		}
	}
}
