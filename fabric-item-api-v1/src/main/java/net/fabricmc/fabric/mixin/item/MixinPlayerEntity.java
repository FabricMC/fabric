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
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.item.v1.ShieldRegistry;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
	protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	/**
	 * Allows modded shields to receive damage.
	 */
	@Redirect(method = "damageShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item damageFabricShields(ItemStack itemStack) {
		if (itemStack.getItem() == Items.SHIELD || ShieldRegistry.get(itemStack.getItem()) != null) {
			return Items.SHIELD;
		}

		return itemStack.getItem();
	}

	/**
	 * Add cooldown for the modded shield instead of the vanilla one.
	 */
	@Redirect(method = "disableShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V"))
	private void setCooldownForShields(ItemCooldownManager cooldownManager, Item item, int duration) {
		Item heldItem = this.activeItemStack.getItem();

		if (this.activeItemStack.getItem() == Items.SHIELD) {
			cooldownManager.set(Items.SHIELD, duration);
		} else {
			ShieldRegistry.Entry entry = ShieldRegistry.get(heldItem);

			if (entry != null && entry.getAxeDisableDuration() > 0) {
				cooldownManager.set(heldItem, entry.getAxeDisableDuration());
			}
		}
	}
}
