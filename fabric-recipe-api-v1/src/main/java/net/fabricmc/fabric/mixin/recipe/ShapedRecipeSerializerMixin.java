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

package net.fabricmc.fabric.mixin.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.collection.DefaultedList;

import net.fabricmc.fabric.api.recipe.v1.serializer.FabricRecipeSerializer;

@Mixin(ShapedRecipe.Serializer.class)
public abstract class ShapedRecipeSerializerMixin implements FabricRecipeSerializer<ShapedRecipe> {
	@Override
	public JsonObject toJson(ShapedRecipe recipe) {
		ShapedRecipeJsonFactory factory = new ShapedRecipeJsonFactory(recipe.getOutput().getItem(), recipe.getOutput().getCount());

		factory.criterion("dummy", null);

		factory.group(recipe.getGroup());

		DefaultedList<Ingredient> recipeIngredients = recipe.getPreviewInputs();
		Object2CharMap<Ingredient> ingredients = new Object2CharOpenHashMap<>();
		ingredients.defaultReturnValue(' ');
		char currentChar = 'A';

		for (Ingredient ingredient : recipeIngredients) {
			if (!ingredient.isEmpty()
					&& ingredients.putIfAbsent(ingredient, currentChar) == ingredients.defaultReturnValue()) {
				currentChar++;
			}
		}

		StringBuilder patternLine = new StringBuilder();

		for (int i = 0; i < recipeIngredients.size(); i++) {
			if (i != 0 && i % recipe.getWidth() == 0) {
				factory.pattern(patternLine.toString());
				patternLine.setLength(0);
			}

			Ingredient ingredient = recipeIngredients.get(i);
			patternLine.append(ingredients.getChar(ingredient));
		}

		factory.pattern(patternLine.toString());

		ingredients.forEach(((ingredient, keyName) -> factory.input(keyName, ingredient)));

		JsonObject[] root = new JsonObject[1];
		factory.offerTo(provider -> root[0] = provider.toJson());
		return root[0];
	}
}
