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

@FunctionalInterface
public interface RecipeRemainderProvider {
	/**
	 * An {@link ItemStack} aware version of {@link Item#getRecipeRemainder().
	 *
	 * @param original The input item stack.
	 * @param inventory The inventory that the stack is in.
	 * @param type The recipe type being used.
	 * @param world The world in which the inventory is in.
	 * @param pos The position at which the inventory is.
	 * @return
	 */
	ItemStack getRecipeRemainder(ItemStack original, Inventory inventory, RecipeType<?> type, World world, @Nullable BlockPos pos);
}
