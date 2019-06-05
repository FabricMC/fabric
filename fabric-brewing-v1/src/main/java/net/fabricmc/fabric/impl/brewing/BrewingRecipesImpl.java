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

package net.fabricmc.fabric.impl.brewing;

import net.fabricmc.fabric.api.brewing.BrewingRecipe;
import net.fabricmc.fabric.api.brewing.BrewingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BrewingRecipesImpl implements BrewingRecipes {
	public List<BrewingRecipe> getRelevantRecipes(World world, ItemStack stack, boolean checkingIngredient) {
		if(world == null) throw new IllegalStateException("Tried to get custom brewing recipes with no available world");

		Stream<BrewingRecipe> recipes = world.getRecipeManager().values().stream()
			.filter(recipe -> recipe.getType() == FabricBrewingInit.BREWING_RECIPE_TYPE)
			.map(recipe -> (BrewingRecipe)recipe);
		Predicate<ItemStack> stacksMatch =
			s -> stack.getItem() == s.getItem() && ItemStack.areTagsEqual(stack, s);
		Stream<BrewingRecipe> relevant = checkingIngredient
			? recipes.filter(r -> Arrays.stream(r.getInput().asIngredient().getStackArray()).anyMatch(stacksMatch))
			: recipes.filter(r -> Arrays.stream(r.getBasePotion().asIngredient().getStackArray()).anyMatch(stacksMatch));

		return relevant.collect(Collectors.toList());
	}
}
