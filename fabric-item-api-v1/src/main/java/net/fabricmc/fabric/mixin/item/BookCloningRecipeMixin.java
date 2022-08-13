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
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.recipe.BookCloningRecipe;
import net.minecraft.util.collection.DefaultedList;

@Mixin(BookCloningRecipe.class)
public class BookCloningRecipeMixin {
	/**
	 * @author FabricMC
	 * @reason support stack aware recipe remainders
	 */
	@Overwrite
	public DefaultedList<ItemStack> getRemainder(CraftingInventory craftingInventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); ++i) {
			ItemStack itemStack = craftingInventory.getStack(i);

			if (itemStack.getItem().hasRecipeRemainder(itemStack)) {
				defaultedList.set(i, itemStack.getItem().getRecipeRemainder(itemStack));
			} else if (itemStack.getItem() instanceof WrittenBookItem) {
				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setCount(1);
				defaultedList.set(i, itemStack2);
				break;
			}
		}

		return defaultedList;
	}
}
