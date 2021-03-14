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

package net.fabricmc.fabric.test.recipe;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.RecipeManagerHelper;

public class RecipeApiTestMod implements ModInitializer {
	public static final String NAMESPACE = "fabric-recipe-api-v1-testmod";
	private static final Random RANDOM = new Random();
	private static final List<Item> RANDOM_ITEMS_POOL = Arrays.asList(
			Items.COMMAND_BLOCK,
			Items.COMMAND_BLOCK_MINECART,
			Items.ELYTRA,
			Items.CHAIN_COMMAND_BLOCK,
			Items.REPEATING_COMMAND_BLOCK
	);

	@Override
	public void onInitialize() {
		// Recipe with stick -> diamond
		RecipeManagerHelper.registerStaticRecipe(
				new ShapelessRecipe(new Identifier(NAMESPACE, "test1"), "",
						new ItemStack(Items.DIAMOND),
						DefaultedList.copyOf(Ingredient.EMPTY, Ingredient.ofItems(Items.STICK))));

		RecipeManagerHelper.registerDynamicRecipes(handler -> {
			handler.register(new Identifier(NAMESPACE, "test2"),
					id -> new ShapedRecipe(id, "",
							2, 2,
							DefaultedList.copyOf(Ingredient.EMPTY,
									Ingredient.ofItems(Items.IRON_INGOT), Ingredient.ofItems(Items.GOLD_INGOT),
									Ingredient.ofItems(Items.COAL), Ingredient.ofItems(Items.CHARCOAL)),
							pickRandomStack()));
		});

		RecipeManagerHelper.modifyRecipes(handler -> {
			handler.replace(new ShapelessRecipe(new Identifier("acacia_button"), "",
					new ItemStack(Items.NETHER_STAR),
					DefaultedList.copyOf(Ingredient.EMPTY, Ingredient.ofItems(Items.ACACIA_PLANKS))));
			handler.replace(new ShapedRecipe(new Identifier("oak_button"), "",
					1, 2,
					DefaultedList.copyOf(Ingredient.EMPTY, Ingredient.ofItems(Items.ACACIA_PLANKS), Ingredient.ofItems(Items.COAL)),
					new ItemStack(Items.NETHER_BRICK)));
		});
	}

	private static ItemStack pickRandomStack() {
		Item item = RANDOM_ITEMS_POOL.get(RANDOM.nextInt(RANDOM_ITEMS_POOL.size()));
		return new ItemStack(item);
	}
}
