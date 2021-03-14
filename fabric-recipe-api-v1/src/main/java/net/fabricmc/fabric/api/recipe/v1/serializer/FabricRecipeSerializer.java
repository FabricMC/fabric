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

package net.fabricmc.fabric.api.recipe.v1.serializer;

import com.google.gson.JsonObject;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;

/**
 * Represents a recipe serializer for mods to implement.
 * <p>This will allow serialization to JSON of recipes. Useful for recipe dumping.</p>
 *
 * @param <T> the recipe
 * @version 1.0.0
 * @since 1.0.0
 */
public interface FabricRecipeSerializer<T extends Recipe<?>> extends RecipeSerializer<T> {
	/**
	 * Serializes the recipe to JSON.
	 *
	 * @param recipe the recipe
	 * @return the serialized recipe
	 */
	JsonObject toJson(T recipe);
}
