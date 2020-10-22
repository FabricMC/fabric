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

package net.fabricmc.fabric.mixin.extensibility.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.extensibility.item.v1.bow.BowInterface;
import net.fabricmc.fabric.api.extensibility.item.v1.crossbow.CrossbowInterface;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
	@Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item renderFirstPersonItem(ItemStack heldItem) {
		if (heldItem.getItem() instanceof CrossbowInterface) {
			return Items.CROSSBOW; // Return true to invoke crossbow rendering path
		}

		return heldItem.getItem();
	}

	@Redirect(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item renderItem(ItemStack heldItem) {
		if (heldItem.getItem() instanceof CrossbowInterface) {
			return Items.CROSSBOW; // Return crossbow for rendering
		}

		if (heldItem.getItem() instanceof BowInterface) {
			return Items.BOW; // Return bow for rendering
		}

		return heldItem.getItem();
	}
}
