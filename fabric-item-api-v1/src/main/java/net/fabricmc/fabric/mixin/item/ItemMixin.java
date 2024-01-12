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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(Item.class)
abstract class ItemMixin implements ItemExtensions, FabricItem {
	@Unique
	@Nullable
	private EquipmentSlotProvider equipmentSlotProvider;

	@Unique
	@Nullable
	private CustomDamageHandler customDamageHandler;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(Item.Settings settings, CallbackInfo info) {
		FabricItemInternals.onBuild(settings, (Item) (Object) this);
	}

	@Override
	@Nullable
	public EquipmentSlotProvider fabric_getEquipmentSlotProvider() {
		return equipmentSlotProvider;
	}

	@Override
	public void fabric_setEquipmentSlotProvider(@Nullable EquipmentSlotProvider equipmentSlotProvider) {
		this.equipmentSlotProvider = equipmentSlotProvider;
	}

	@Override
	@Nullable
	public CustomDamageHandler fabric_getCustomDamageHandler() {
		return customDamageHandler;
	}

	@Override
	public void fabric_setCustomDamageHandler(@Nullable CustomDamageHandler handler) {
		this.customDamageHandler = handler;
	}

	@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;"))
	private @Nullable FoodComponent getStackAwareFoodComponent(Item instance, Operation<Boolean> original, World world, PlayerEntity user, Hand hand) {
		return user.getStackInHand(hand).getFoodComponent();
	}

	@WrapOperation(method = "getMaxUseTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getFoodComponent()Lnet/minecraft/item/FoodComponent;"))
	private @Nullable FoodComponent getStackAwareFoodComponent(Item instance, Operation<Boolean> original, ItemStack stack) {
		return stack.getFoodComponent();
	}

	@WrapOperation(method = {"getMaxUseTime", "getUseAction"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z"))
	private boolean isStackAwareFood(Item instance, Operation<Boolean> original, ItemStack stack) {
		return stack.isFood();
	}

	@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z"))
	private boolean isStackAwareFood(Item instance, Operation<Boolean> original, World world, PlayerEntity user, Hand hand) {
		return user.getStackInHand(hand).isFood();
	}

	@WrapOperation(method = "finishUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isFood()Z"))
	private boolean isStackAwareFood(Item instance, Operation<Boolean> original, ItemStack stack, World world, LivingEntity user) {
		return stack.isFood();
	}
}
