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

import java.util.Locale;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin extends FeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>> {
	@Shadow
	@Final
	private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

	private ArmorFeatureRendererMixin(FeatureRendererContext<LivingEntity, BipedEntityModel<LivingEntity>> context) {
		super(context);
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, BipedEntityModel<LivingEntity> model, CallbackInfo ci) {
		ItemStack stack = entity.getEquippedStack(armorSlot);
		ArmorRenderer renderer = ArmorRendererRegistryImpl.get(stack.getItem());

		if (renderer != null) {
			renderer.render(matrices, vertexConsumers, stack, entity, armorSlot, light, getContextModel());
			ci.cancel();
		}
	}

	@Inject(method = "getArmorTexture", at = @At(value = "HEAD"), cancellable = true)
	private void getArmorTexture(ArmorItem item, boolean secondLayer, String overlay, CallbackInfoReturnable<Identifier> cir) {
		final String name = item.getMaterial().getName();
		final int separator = name.indexOf(Identifier.NAMESPACE_SEPARATOR);

		if (separator != -1) {
			final String namespace = name.substring(0, separator);
			final String path = name.substring(separator + 1);
			final String texture = String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", namespace, path, secondLayer ? 2 : 1, overlay == null ? "" : "_" + overlay);

			cir.setReturnValue(ARMOR_TEXTURE_CACHE.computeIfAbsent(texture, Identifier::new));
		}
	}
}
