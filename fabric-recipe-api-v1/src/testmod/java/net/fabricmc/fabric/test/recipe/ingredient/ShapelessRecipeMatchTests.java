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

package net.fabricmc.fabric.test.recipe.ingredient;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

public class ShapelessRecipeMatchTests {
	/**
	 * The recipe requires at least one undamaged pickaxe.
	 */
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testShapelessMatch(TestContext context) {
		RegistryKey<Recipe<?>> recipeKey = RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("fabric-recipe-api-v1-testmod", "test_shapeless_match"));
		ShapelessRecipe recipe = (ShapelessRecipe) context.getWorld().getRecipeManager().get(recipeKey).get().value();

		ItemStack undamagedPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
		ItemStack damagedPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
		damagedPickaxe.setDamage(100);

		List<ItemStack> damagedPickaxes = Collections.nCopies(9, damagedPickaxe);

		if (recipe.matches(CraftingRecipeInput.create(3, 3, damagedPickaxes), context.getWorld())) {
			throw new GameTestException("Recipe should not match with only damaged pickaxes");
		}

		List<ItemStack> oneUndamagedPickaxe = new LinkedList<>(damagedPickaxes);
		oneUndamagedPickaxe.set(0, undamagedPickaxe);

		if (!recipe.matches(CraftingRecipeInput.create(3, 3, oneUndamagedPickaxe), context.getWorld())) {
			throw new GameTestException("Recipe should match with at least one undamaged pickaxe");
		}

		context.complete();
	}
}
