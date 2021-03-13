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

package net.fabricmc.fabric.api.recipe.v1;

import net.minecraft.recipe.Recipe;

import net.fabricmc.fabric.api.recipe.v1.event.RecipeLoadingCallback;
import net.fabricmc.fabric.impl.recipe.RecipeManagerImpl;

/**
 * Represents a helper for the {@link net.minecraft.recipe.RecipeManager}.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class RecipeManagerHelper {
	private RecipeManagerHelper() {
		throw new UnsupportedOperationException("Someone tampered with the universe.");
	}

	/**
	 * Registers a static recipe.
	 * <p>A static recipe is a recipe that is registered at mod startup (or later) and is kept during the whole lifecycle
	 * of the game.</p>
	 * <p>Static recipes are automatically added to the {@linkplain net.minecraft.recipe.RecipeManager recipe manager}
	 * when recipes are loaded, and only is added if no other recipe with the same identifier is already registered.</p>
	 *
	 * @param recipe the recipe to register
	 * @return the registered recipe
	 * @throws IllegalStateException if another recipe with the same identifier is already registered
	 */
	public static Recipe<?> registerStaticRecipe(Recipe<?> recipe) {
		RecipeManagerImpl.registerStaticRecipe(recipe);
		return recipe;
	}

	/**
	 * Register a dynamic recipes provider.
	 * <p>The dynamic recipes provider is called when the recipes are loaded.</p>
	 *
	 * @param callback the dynamic recipes provider
	 */
	public static void registerDynamicRecipes(RecipeLoadingCallback callback) {
		RecipeLoadingCallback.EVENT.register(callback);
	}
}
