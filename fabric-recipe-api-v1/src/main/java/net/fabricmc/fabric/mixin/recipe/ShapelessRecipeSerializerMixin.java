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
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.recipe.v1.serializer.FabricRecipeSerializer;

@Mixin(ShapelessRecipe.Serializer.class)
public abstract class ShapelessRecipeSerializerMixin implements FabricRecipeSerializer<ShapelessRecipe> {
	@Override
	public JsonObject toJson(ShapelessRecipe recipe) {
		JsonObject root = new JsonObject();
		root.addProperty("type", "minecraft:crafting_shapeless");

		if (!recipe.getGroup().isEmpty()) {
			root.addProperty("group", recipe.getGroup());
		}

		JsonArray ingredients = new JsonArray();
		root.add("ingredients", ingredients);

		for (Ingredient ingredient : recipe.getPreviewInputs()) {
			ingredients.add(ingredient.toJson());
		}

		JsonObject result = new JsonObject();
		result.addProperty("item", Registry.ITEM.getId(recipe.getOutput().getItem()).toString());
		result.addProperty("count", recipe.getOutput().getCount());
		root.add("result", result);

		return root;
	}
}
