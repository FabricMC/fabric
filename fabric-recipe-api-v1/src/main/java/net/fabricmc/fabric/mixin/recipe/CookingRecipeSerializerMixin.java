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
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.recipe.v1.serializer.FabricRecipeSerializer;

@Mixin(CookingRecipeSerializer.class)
public abstract class CookingRecipeSerializerMixin<T extends AbstractCookingRecipe> implements FabricRecipeSerializer<T> {
	@Override
	public JsonObject toJson(T recipe) {
		Identifier typeId = Registry.RECIPE_SERIALIZER.getId(this);

		if (typeId == null) {
			throw new IllegalStateException("Tried to serialize recipe with an unregistered recipe serializer.");
		}

		JsonObject root = new JsonObject();
		root.addProperty("type", typeId.toString());

		if (!recipe.getGroup().isEmpty()) {
			root.addProperty("group", recipe.getGroup());
		}

		root.add("ingredient", recipe.getPreviewInputs().get(0).toJson());

		JsonObject result = new JsonObject();
		result.addProperty("item", Registry.ITEM.getId(recipe.getOutput().getItem()).toString());
		result.addProperty("count", recipe.getOutput().getCount());
		root.add("result", result);

		root.addProperty("cookingtime", recipe.getCookTime());
		root.addProperty("experience", recipe.getExperience());

		return root;
	}
}
