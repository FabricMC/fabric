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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.screen.ScreenHandler;
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
		Identifier recipeId = new Identifier("fabric-recipe-api-v1-testmod", "test_shapeless_match");
		ShapelessRecipe recipe = (ShapelessRecipe) context.getWorld().getRecipeManager().get(recipeId).get().value();

		ItemStack undamagedPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
		ItemStack damagedPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
		damagedPickaxe.setDamage(100);

		CraftingInventory craftingInv = new CraftingInventory(new ScreenHandler(null, 0) {
			@Override
			public ItemStack quickMove(PlayerEntity player, int slot) {
				return ItemStack.EMPTY;
			}

			@Override
			public boolean canUse(PlayerEntity player) {
				return false;
			}
		}, 3, 3);

		// Test that damaged only doesn't work
		for (int i = 0; i < 9; ++i) {
			craftingInv.setStack(i, damagedPickaxe);
		}

		if (recipe.matches(craftingInv, context.getWorld())) {
			throw new GameTestException("Recipe should not match with only damaged pickaxes");
		}

		craftingInv.setStack(1, undamagedPickaxe);

		if (!recipe.matches(craftingInv, context.getWorld())) {
			throw new GameTestException("Recipe should match with at least one undamaged pickaxe");
		}

		context.complete();
	}
}
