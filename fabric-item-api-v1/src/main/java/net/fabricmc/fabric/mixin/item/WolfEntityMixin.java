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

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(WolfEntity.class)
class WolfEntityMixin {
	@Unique
	private ItemStack fabric_interactMob_itemStack = ItemStack.EMPTY;

	@Inject(method = "interactMob", at = @At("HEAD"))
	private void storeCopy(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		fabric_interactMob_itemStack = player.getStackInHand(hand).copy();
	}

	@Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;"))
	private @Nullable FoodComponent getStackAwareFoodComponent(Item instance, PlayerEntity player, Hand hand) {
		return fabric_interactMob_itemStack.getFoodComponent();
	}

	@Redirect(method = "isBreedingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;"))
	private @Nullable FoodComponent getStackAwareFoodComponent(Item instance, ItemStack stack) {
		return stack.getFoodComponent();
	}

	@Redirect(method = "isBreedingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z", ordinal = 0))
	private boolean isStackAwareFood(Item instance, ItemStack stack) {
		return stack.isFood();
	}
}
