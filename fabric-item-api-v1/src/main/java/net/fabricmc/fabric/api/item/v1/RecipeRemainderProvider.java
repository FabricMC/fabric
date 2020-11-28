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

package net.fabricmc.fabric.api.item.v1;

import org.jetbrains.annotations.Nullable;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.impl.item.ItemExtensions;

/**
 * Allows an item to conditionally specify the recipe remainder.
 * The recipe remainder is an {@link ItemStack} instead of an {@link Item}.
 * This can be used to allow your item to get damaged instead of
 * getting removed when used in crafting.
 *
 * <p>Recipe remainder providers can be set with {@link FabricItemSettings#recipeRemainder(RecipeRemainderProvider)}</p>
 */
@FunctionalInterface
public interface RecipeRemainderProvider {
	/**
	 * An {@link ItemStack} aware version of {@link Item#getRecipeRemainder()}.
	 *
	 * @param original The input item stack.
	 * @param inventory The inventory that the stack is in.
	 * @param type The recipe type being used.
	 * @param world The world in which the inventory is in.
	 * @param pos The position at which the inventory is.
	 * @return the recipe remainder
	 */
	ItemStack getRecipeRemainder(ItemStack original, Inventory inventory, @Nullable RecipeType<?> type, World world, @Nullable BlockPos pos);

	/**
	 * Returns the recipe remainder of an item stack.
	 * If the item's recipe remainder provider is null, the
	 * vanilla recipe remainder is used.
	 *
	 * @param original The input item stack.
	 * @param type The recipe type being used.
	 * @param inventory The inventory that the stack is in.
	 * @param world The world in which the inventory is in.
	 * @param pos The position at which the inventory is.
	 * @return the recipe remainder
	 */
	static ItemStack getRecipeRemainder(ItemStack original, @Nullable RecipeType<?> type, Inventory inventory, World world, @Nullable BlockPos pos) {
		Item item = original.getItem();

		if (((ItemExtensions) item).fabric_getRecipeRemainderProvider() != null) {
			//noinspection ConstantConditions
			return ((ItemExtensions) item).fabric_getRecipeRemainderProvider().getRecipeRemainder(original, inventory, type, world, pos);
		} else if (item.hasRecipeRemainder()) {
			return new ItemStack(item.getRecipeRemainder());
		}

		return ItemStack.EMPTY;
	}
}
