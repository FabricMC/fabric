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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.recipe.v1.serializer.FabricRecipeSerializer;

@Mixin(ShapedRecipe.Serializer.class)
public abstract class ShapedRecipeSerializerMixin implements FabricRecipeSerializer<ShapedRecipe> {
	@Override
	public JsonObject toJson(ShapedRecipe recipe) {
		JsonObject root = new JsonObject();
		root.addProperty("type", "minecraft:crafting_shaped");

		if (!recipe.getGroup().isEmpty()) {
			root.addProperty("group", recipe.getGroup());
		}

		JsonArray pattern = new JsonArray();
		root.add("pattern", pattern);
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
				pattern.add(patternLine.toString());
				patternLine.setLength(0);
			}

			Ingredient ingredient = recipeIngredients.get(i);
			patternLine.append(ingredients.getChar(ingredient));
		}

		pattern.add(patternLine.toString());

		JsonObject key = new JsonObject();
		root.add("key", key);

		ingredients.forEach(((ingredient, keyName) -> key.add(String.valueOf(keyName), ingredient.toJson())));

		JsonObject result = new JsonObject();
		result.addProperty("item", Registry.ITEM.getId(recipe.getOutput().getItem()).toString());
		result.addProperty("count", recipe.getOutput().getCount());
		root.add("result", result);

		return root;
	}
}
