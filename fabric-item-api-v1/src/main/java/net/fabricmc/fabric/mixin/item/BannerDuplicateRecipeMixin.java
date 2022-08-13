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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BannerDuplicateRecipe;
import net.minecraft.util.collection.DefaultedList;

@Mixin(BannerDuplicateRecipe.class)
public class BannerDuplicateRecipeMixin {
	@Unique
	private ItemStack capturedItemStack;

	@Inject(method = "getRemainder(Lnet/minecraft/inventory/CraftingInventory;)Lnet/minecraft/util/collection/DefaultedList;", at = @At(value = "JUMP", target = "Lnet/minecraft/inventory/CraftingInventory;getStack(I)Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void captureCurrentStack(CraftingInventory craftingInventory, CallbackInfoReturnable<DefaultedList<ItemStack>> cir, DefaultedList<ItemStack> defaultedList, int i) {
		capturedItemStack = craftingInventory.getStack(i);
	}

	@Redirect(method = "getRemainder(Lnet/minecraft/inventory/CraftingInventory;)Lnet/minecraft/util/collection/DefaultedList;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;hasRecipeRemainder()Z"))
	private boolean hasStackRecipeRemainder(Item instance) {
		return capturedItemStack.getItem().hasRecipeRemainder(capturedItemStack);
	}

	@Redirect(method = "getRemainder(Lnet/minecraft/inventory/CraftingInventory;)Lnet/minecraft/util/collection/DefaultedList;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
	private Object getStackRecipeRemainder(DefaultedList<ItemStack> inventory, int index, Object element) {
		return inventory.set(index, capturedItemStack.getItem().getRecipeRemainder(capturedItemStack));
	}
}
