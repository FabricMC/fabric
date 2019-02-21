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

package net.fabricmc.fabric.mixin.client.render;

import net.fabricmc.fabric.api.item.ArmorItemNamespaced;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ArmorFeatureRenderer.class)
public class MixinArmorFeatureRenderer {
	private ThreadLocal<ItemStack> fabric_stackCopy;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), method = "renderArmor", locals = LocalCapture.CAPTURE_FAILHARD)
	private void renderArmorHead(LivingEntity entity, float f1, float f2, float f3, float f4, float f5, float f6, float f7, EquipmentSlot slot, CallbackInfo info, ItemStack stack) {
		if (fabric_stackCopy == null) {
			fabric_stackCopy = new ThreadLocal<>();
		}
		fabric_stackCopy.set(stack);
	}

	@Inject(at = @At("RETURN"), method = "renderArmor")
	private void renderArmorReturn(LivingEntity entity, float f1, float f2, float f3, float f4, float f5, float f6, float f7, EquipmentSlot slot, CallbackInfo info) {
		fabric_stackCopy.remove();
	}

	@Inject(at = @At("HEAD"), method = "method_4174", cancellable = true)
	private void method_4174(ArmorItem armorItem, boolean layerTwo, String suffix, CallbackInfoReturnable<Identifier> info) {
		if (armorItem instanceof ArmorItemNamespaced) {
			ItemStack stack = fabric_stackCopy != null ? fabric_stackCopy.get() : null;
			if (stack == null) {
				stack = new ItemStack(armorItem);
			}

			info.setReturnValue(((ArmorItemNamespaced) armorItem).getNamespacedArmorTexture(
				stack, layerTwo ? 2 : 1, suffix != null ? suffix : ""
			));
		}
	}

		// TODO
}
