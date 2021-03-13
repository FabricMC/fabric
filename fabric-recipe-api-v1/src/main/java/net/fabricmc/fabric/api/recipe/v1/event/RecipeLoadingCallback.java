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

package net.fabricmc.fabric.api.recipe.v1.event;

import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Represents the recipe loading event.
 * <p>Triggered when the recipes are loaded.</p>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface RecipeLoadingCallback {
	/**
	 * Recipe loading event, triggered when the recipes are loaded.
	 */
	Event<RecipeLoadingCallback> EVENT = EventFactory.createArrayBacked(RecipeLoadingCallback.class,
			callbacks -> recipeConsumer -> {
				for (RecipeLoadingCallback callback : callbacks) {
					callback.onRecipeLoading(recipeConsumer);
				}
			});

	/**
	 * Called when recipes are loaded.
	 * <p>{@code handler} is used to add recipes into the {@linkplain net.minecraft.recipe.RecipeManager recipe manager}.</p>
	 *
	 * @param handler the recipe handler.
	 */
	void onRecipeLoading(RecipeHandler handler);

	interface RecipeHandler {
		/**
		 * Registers a recipe into the {@linkplain net.minecraft.recipe.RecipeManager}.
		 *
		 * @param recipe the recipe to register
		 * @return {@code true} if the recipe has been registered, else {@code false}
		 */
		boolean register(Recipe<?> recipe);

		/**
		 * Returns whether the a recipe with the given identifier can be registered.
		 *
		 * @param id the recipe identifier
		 * @return {@code true} if the recipe can be registered, else {@code false}
		 */
		boolean canRegister(Identifier id);
	}
}
