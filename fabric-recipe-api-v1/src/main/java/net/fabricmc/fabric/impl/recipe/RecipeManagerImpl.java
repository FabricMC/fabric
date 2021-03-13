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

package net.fabricmc.fabric.impl.recipe;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.recipe.v1.event.RecipeLoadingCallback;

@ApiStatus.Internal
public class RecipeManagerImpl {
	/**
	 * Stores the static recipes which are added to the {@link net.minecraft.recipe.RecipeManager} when recipes are
	 * loaded.
	 */
	private static final Map<Identifier, Recipe<?>> STATIC_RECIPES = new Object2ObjectOpenHashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	public static void registerStaticRecipes(Recipe<?> recipe) {
		if (STATIC_RECIPES.put(recipe.getId(), recipe) != null) {
			throw new IllegalStateException("Cannot register " + recipe.getId()
					+ " as another recipe with the same identifier already exists.");
		}
	}

	public static void apply(Map<Identifier, JsonElement> map,
							Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> builderMap) {
		RecipeHandlerImpl handler = new RecipeHandlerImpl(map, builderMap);
		RecipeLoadingCallback.EVENT.invoker().onRecipeLoading(handler);
		STATIC_RECIPES.values().forEach(handler::register);
		LOGGER.info("Registered {} custom recipes.", handler.registered);
	}

	private static class RecipeHandlerImpl implements RecipeLoadingCallback.RecipeHandler {
		private final Map<Identifier, JsonElement> resourceMap;
		private final Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> builderMap;
		int registered = 0;

		private RecipeHandlerImpl(Map<Identifier, JsonElement> resourceMap,
								Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> builderMap) {
			this.resourceMap = resourceMap;
			this.builderMap = builderMap;
		}

		void register(Recipe<?> recipe) {
			if (!this.resourceMap.containsKey(recipe.getId())) {
				ImmutableMap.Builder<Identifier, Recipe<?>> recipeBuilder =
						this.builderMap.computeIfAbsent(recipe.getType(), o -> ImmutableMap.builder());
				recipeBuilder.put(recipe.getId(), recipe);
				this.registered++;
			}
		}

		@Override
		public void register(Identifier id, Function<Identifier, Recipe<?>> factory) {
			// Add the recipe only if nothing already provides the recipe.
			if (!this.resourceMap.containsKey(id)) {
				Recipe<?> recipe = factory.apply(id);

				if (!id.equals(recipe.getId())) {
					throw new IllegalStateException("The recipe " + recipe.getId() + " tried to be registered as " + id);
				}

				ImmutableMap.Builder<Identifier, Recipe<?>> recipeBuilder =
						this.builderMap.computeIfAbsent(recipe.getType(), o -> ImmutableMap.builder());
				recipeBuilder.put(recipe.getId(), recipe);
				this.registered++;
			}
		}
	}
}
