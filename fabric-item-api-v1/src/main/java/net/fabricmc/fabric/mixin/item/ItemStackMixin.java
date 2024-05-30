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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements FabricItemStack {
	@Shadow public abstract Item getItem();

	@WrapOperation(method = "damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/lang/Runnable;)V"))
	private void hookDamage(ItemStack instance, int amount, Random random, ServerPlayerEntity serverPlayerEntity, Runnable runnable, Operation<Void> original, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) EquipmentSlot slot) {
		CustomDamageHandler handler = ((ItemExtensions) getItem()).fabric_getCustomDamageHandler();

		if (handler != null) {
			// Track whether an item has been broken by custom handler
			MutableBoolean mut = new MutableBoolean(false);
			amount = handler.damage((ItemStack) (Object) this, amount, entity, slot, () -> {
				mut.setTrue();
				runnable.run();
			});

			// If item is broken, there's no reason to call the original.
			if (mut.booleanValue()) return;
		}

		original.call(instance, amount, random, serverPlayerEntity, runnable);
	}
}
