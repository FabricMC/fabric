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

package net.fabricmc.fabric.mixin.recipe.cooking;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.DynamicRegistryManager;

/**
 * A Mixin class to ensure that furnaces can use the functionality provided from recipes involving multiple output items
 * (not item types, just multiple of the same item).
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
	/**
	 * A wrapper altering the result of a smelting recipe when outputting into a partially filled output slot. 1.20.4
	 * functionality increments the output by 1. This wrapper instead increases the count by the amount defined in the
	 * recipe.
	 *
	 * <p>Assumes that the increment will be valid and does not stop recipe if it would go over. E.g. an increment of
	 * 65 would always be performed, if not stopped by other logic prior to this.
	 *
	 * @param outputStack the instance of the itemstack to increment, in the furnace's output slot.
	 * @param increment the original incremented amount (1.20.4 this is always 1).
	 * @param incrementItemStack the method responsible for increasing the item stack count.
	 * @param registryManager the registry manager to access for the recipe
	 * @param recipe the (!= null) recipe.
	 */
	@WrapOperation(
			method = "craftRecipe",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;increment(I)V"
			)
	)
	private static void incrementByRecipe(ItemStack outputStack, int increment, Operation<Void> incrementItemStack, DynamicRegistryManager registryManager, RecipeEntry<?> recipe) {
		incrementItemStack.call(
				outputStack,
				recipe.value()
						.getResult(registryManager)
						.getCount());
	}

	/**
	 * Modifies the return value of {@code ItemStack.getCount()} in the specified function to be compatible with
	 * multiple item output. In Vanilla (1.20.4), this is used in the original function to check whether the furnace
	 * output stack has enough space to accommodate the new items. It assumes this is always equal to 1 and so checks
	 * whether the current number of items is less than the maximum number.
	 *
	 * <p>This mixin modifies the original "count" result so that it checks against the count modified for the number added
	 * by the recipe - 1. This ensures identical functionality for vanilla recipes (where count == 1), while ensuring
	 * that the crafting will not occur if there is no space for multiple items.
	 *
	 * @param itemStack2Count the original in the furnace output slot.
	 * @param recipeResult the ItemStack resulting from the current smelting recipe.
	 * @return a new count that represents the item stack size after the current recipe is performed (minus 1)
	 */
	@ModifyExpressionValue(
			method = "canAcceptRecipeOutput",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;getCount()I"
			)
	)
	private static int correctFutureAmountByRecipe(int itemStack2Count, @Local(ordinal = 0) ItemStack recipeResult) {
		return itemStack2Count + recipeResult.getCount() - 1;
	}
}
