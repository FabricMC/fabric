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

package net.fabricmc.fabric.api.registry;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;

/**
 * Counterpart of {@link BrewingRecipeRegistry} with methods that allow adding recipes which use Ingredients instead of Items.
 */
public final class FabricBrewingRecipeRegistry {
	private FabricBrewingRecipeRegistry() {
	}

	/**
	 * Register a recipe for brewing one potion type into another (e.g. regular to splash).
	 * Only one recipe is necessary for all potions of the input type to be brewable into the output type using the ingredient.
	 * Use {@link BrewingRecipeRegistry#registerPotionType(Item)} to register new potion types.
	 * @param input the input potion type (e.g. regular potion)
	 * @param ingredient the required ingredient (e.g. gunpowder)
	 * @param output the output type (e.g. splash potion)
	 * @see BrewingRecipeRegistry#registerItemRecipe(Item, Item, Item)
	 */
	public static void registerItemRecipe(PotionItem input, Ingredient ingredient, PotionItem output) {
		Objects.requireNonNull(input, "Input cannot be null!");
		Objects.requireNonNull(ingredient, "Ingredient cannot be null!");
		Objects.requireNonNull(output, "Output cannot be null!");

		BrewingRecipeRegistry.ITEM_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(input, ingredient, output));
	}

	/**
	 * Register a recipe for converting from one potion to another (e.g. awkward to instant health).
	 * This does not automatically create long or strong versions of the output potion.
	 * They require separate recipes.
	 * @param input input potion (e.g. awkward)
	 * @param ingredient the required ingredient (e.g. glistering melon)
	 * @param output output potion (e.g. instant health)
	 * @see BrewingRecipeRegistry#registerPotionRecipe(Potion, Item, Potion)
	 */
	public static void registerPotionRecipe(Potion input, Ingredient ingredient, Potion output) {
		Objects.requireNonNull(input, "Input cannot be null!");
		Objects.requireNonNull(ingredient, "Ingredient cannot be null!");
		Objects.requireNonNull(output, "Output cannot be null");

		BrewingRecipeRegistry.POTION_RECIPES.add(new BrewingRecipeRegistry.Recipe<>(input, ingredient, output));
	}
}
