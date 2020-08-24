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

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow public abstract Item getItem();

	@Unique
	private LivingEntity fabric_damagingEntity;

	@Unique
	private Consumer<LivingEntity> fabric_breakCallback;

	@Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"))
	private void saveDamager(int amount, LivingEntity entity, Consumer<LivingEntity> breakCallback, CallbackInfo ci) {
		this.fabric_damagingEntity = entity;
		this.fabric_breakCallback = breakCallback;
	}

	@ModifyArg(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z"), index = 0)
	private int hookDamage(int amount) {
		CustomDamageHandler handler = ((ItemExtensions) getItem()).fabric_getCustomDamageHandler();

		if (handler != null) {
			return handler.damage((ItemStack) (Object) this, amount, fabric_damagingEntity, fabric_breakCallback);
		}

		return amount;
	}

	@Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("RETURN"))
	private <T extends LivingEntity> void clearDamager(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
		this.fabric_damagingEntity = null;
		this.fabric_breakCallback = null;
	}
}
