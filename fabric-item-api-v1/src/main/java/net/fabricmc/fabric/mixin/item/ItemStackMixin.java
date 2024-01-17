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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements FabricItemStack {
	@Shadow public abstract Item getItem();

	@WrapOperation(method = "damage(ILnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/lang/Runnable;)V"))
	private void hookDamage(ItemStack instance, int amount, Random random, ServerPlayerEntity serverPlayerEntity, Runnable runnable, Operation<Void> original) {
		CustomDamageHandler handler = ((ItemExtensions) getItem()).fabric_getCustomDamageHandler();

		if (handler != null) {
			// TODO 1.20.5, what to do about break callback
			assert false;
			amount = handler.damage((ItemStack) (Object) this, amount, serverPlayerEntity, null);
		}

		original.call(instance, amount, random, serverPlayerEntity, runnable);
	}

	@Redirect(
			method = "getAttributeModifiers",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/Item;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"
			)
	)
	public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> hookGetAttributeModifiers(Item item, EquipmentSlot slot) {
		ItemStack stack = (ItemStack) (Object) this;
		//we need to ensure it is modifiable for the callback, use linked map to preserve ordering
		Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifiers = LinkedHashMultimap.create(item.getAttributeModifiers(stack, slot));
		ModifyItemAttributeModifiersCallback.EVENT.invoker().modifyAttributeModifiers(stack, slot, attributeModifiers);
		return attributeModifiers;
	}

	@Redirect(
			method = "isSuitableFor",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/Item;isSuitableFor(Lnet/minecraft/block/BlockState;)Z"
			)
	)
	public boolean hookIsSuitableFor(Item item, BlockState state) {
		return item.isSuitableFor((ItemStack) (Object) this, state);
	}

	@Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
	public void isStackAwareFood(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.getFoodComponent() != null);
	}
}
