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

package net.fabricmc.fabric.impl.recipe.ingredient.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class AllIngredient extends CombinedIngredient {
	public static final CustomIngredientSerializer<AllIngredient> SERIALIZER =
			new Serializer<>(new Identifier("fabric", "all"), AllIngredient::new);

	public AllIngredient(Ingredient[] ingredients) {
		super(ingredients);
	}

	@Override
	public boolean test(ItemStack stack) {
		for (Ingredient ingredient : ingredients) {
			if (!ingredient.test(stack)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		// There's always at least one sub ingredient, so accessing ingredients[0] is safe.
		List<ItemStack> previewStacks = new ArrayList<>(Arrays.asList(ingredients[0].getMatchingStacks()));

		for (int i = 1; i < ingredients.length; ++i) {
			Ingredient ing = ingredients[i];
			previewStacks.removeIf(stack -> !ing.test(stack));
		}

		return previewStacks;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
