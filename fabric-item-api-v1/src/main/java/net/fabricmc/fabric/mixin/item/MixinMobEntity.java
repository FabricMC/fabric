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

package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.item.v1.ShieldRegistry;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity {
	/**
	 * Also disable modded shields.
	 */
	@Redirect(method = "disablePlayerShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item disableFabricShields(ItemStack itemStack) {
		Item item = itemStack.getItem();

		if (item != Items.SHIELD) {
			ShieldRegistry.Entry entry = ShieldRegistry.get(item);

			if (entry != null && entry.getAxeDisableDuration() != 0) {
				// Makes condition in target method return true
				return Items.SHIELD;
			}
		}

		return item;
	}

	/**
	 * Add cooldown for the modded shield instead of the vanilla one.
	 */
	@Redirect(method = "disablePlayerShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V"))
	private void setCooldownForShields(ItemCooldownManager cooldownManager, Item item, int duration, PlayerEntity player, ItemStack mobStack, ItemStack playerStack) {
		Item heldItem = playerStack.getItem();

		if (heldItem == Items.SHIELD) {
			cooldownManager.set(item, duration);
		}

		// At this point if the item is a shield it has already been checked if cooldown > 0 by disableFabricShields
		ShieldRegistry.Entry entry = ShieldRegistry.get(heldItem);

		if (entry != null) {
			if (entry.getAxeDisableDuration() > 0) {
				// Use custom cooldown duration
				cooldownManager.set(heldItem, entry.getAxeDisableDuration());
			} else {
				// Use vanilla cooldown duration
				cooldownManager.set(heldItem, duration);
			}
		}
	}

	/**
	 * Sets the preferred equipment slot for modded shields to offhand.
	 */
	@Inject(method = "getPreferredEquipmentSlot", at = @At("HEAD"), cancellable = true)
	private static void addPreferredShieldsSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> info) {
		if (ShieldRegistry.get(stack.getItem()) != null) {
			info.setReturnValue(EquipmentSlot.OFFHAND);
		}
	}
}
