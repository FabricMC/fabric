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

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.recipe.v1.RecipeLoadingEvents;

@ApiStatus.Internal
public final class RecipeManagerImpl {
	/**
	 * Stores the static recipes which are added to the {@link net.minecraft.recipe.RecipeManager} when recipes are
	 * loaded.
	 */
	private static final Map<Identifier, Recipe<?>> STATIC_RECIPES = new Object2ObjectOpenHashMap<>();
	private static final boolean DEBUG_MODE = Boolean.getBoolean("fabric-recipe-api-v1--debug");
	private static final Logger LOGGER = LogManager.getLogger();

	private RecipeManagerImpl() {
		throw new UnsupportedOperationException("Someone tampered with the universe.");
	}

	public static void registerStaticRecipe(Recipe<?> recipe) {
		if (STATIC_RECIPES.putIfAbsent(recipe.getId(), recipe) != null) {
			throw new IllegalArgumentException("Cannot register " + recipe.getId()
					+ " as another recipe with the same identifier already exists.");
		}
	}

	public static void apply(Map<Identifier, JsonElement> map,
							Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> builderMap) {
		RegisterRecipeHandlerImpl handler = new RegisterRecipeHandlerImpl(map, builderMap);
		RecipeLoadingEvents.REGISTER.invoker().onRecipeLoading(handler);
		STATIC_RECIPES.values().forEach(handler::register);
		LOGGER.info("Registered {} custom recipes.", handler.registered);
	}

	public static void applyModifications(RecipeManager recipeManager, Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes) {
		ModifyRecipeHandlerImpl handler = new ModifyRecipeHandlerImpl(recipeManager, recipes);
		RecipeLoadingEvents.MODIFY.invoker().onRecipeModify(handler);
		LOGGER.info("Modified {} recipes.", handler.counter);
	}

	private static class RegisterRecipeHandlerImpl implements RecipeLoadingEvents.RecipeLoadingCallback.RecipeHandler {
		private final Map<Identifier, JsonElement> resourceMap;
		private final Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> builderMap;
		int registered = 0;

		private RegisterRecipeHandlerImpl(Map<Identifier, JsonElement> resourceMap,
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

				if (DEBUG_MODE) {
					LOGGER.info("Added recipe {} with type {} in register phase.", recipe.getId(), recipe.getType());
				}
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

				if (DEBUG_MODE) {
					LOGGER.info("Added recipe {} with type {} in register phase.", id, recipe.getType());
				}
			}
		}
	}

	private static class ModifyRecipeHandlerImpl implements RecipeLoadingEvents.RecipeModifyCallback.RecipeHandler {
		final RecipeManager recipeManager;
		final Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;
		int counter = 0;

		private ModifyRecipeHandlerImpl(RecipeManager recipeManager, Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes) {
			this.recipeManager = recipeManager;
			this.recipes = recipes;
		}

		private void add(Recipe<?> recipe) {
			Map<Identifier, Recipe<?>> type = this.recipes.get(recipe.getType());

			if (type == null) {
				throw new IllegalStateException("The given recipe " + recipe.getId()
						+ " do not have its recipe type " + recipe.getType() + " in the recipe manager.");
			}

			type.put(recipe.getId(), recipe);
		}

		@Override
		public void replace(Recipe<?> recipe) {
			RecipeType<?> oldType = this.getTypeOf(recipe.getId());

			if (oldType == null) {
				if (DEBUG_MODE) {
					LOGGER.info("Add new recipe {} with type {} in modify phase.", recipe.getId(), recipe.getType());
				}

				this.add(recipe);
			} else if (oldType == recipe.getType()) {
				if (DEBUG_MODE) {
					LOGGER.info("Replace recipe {} with same type {} in modify phase.", recipe.getId(), recipe.getType());
				}

				this.recipes.get(oldType).put(recipe.getId(), recipe);
			} else {
				if (DEBUG_MODE) {
					LOGGER.info("Replace new recipe {} with type {} (and old type {}) in modify phase.",
							recipe.getId(), recipe.getType(), oldType);
				}

				this.recipes.get(oldType).remove(recipe.getId());
				this.add(recipe);
			}

			this.counter++;
		}

		@Override
		public @Nullable RecipeType<?> getTypeOf(Identifier id) {
			return this.recipes.entrySet().stream()
					.filter(entry -> entry.getValue().containsKey(id))
					.findFirst()
					.map(Map.Entry::getKey)
					.orElse(null);
		}

		@Override
		public boolean contains(Identifier id) {
			for (Map<Identifier, Recipe<?>> recipes : this.recipes.values()) {
				if (recipes.containsKey(id)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean contains(Identifier id, RecipeType<?> type) {
			Map<Identifier, Recipe<?>> recipes = this.recipes.get(type);

			if (recipes == null) return false;

			return recipes.containsKey(id);
		}

		@Override
		public @Nullable Recipe<?> getRecipe(Identifier id) {
			for (Map<Identifier, Recipe<?>> recipes : this.recipes.values()) {
				Recipe<?> recipe = recipes.get(id);

				if (recipe != null) {
					return recipe;
				}
			}

			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends Recipe<?>> @Nullable T getRecipe(Identifier id, RecipeType<T> type) {
			Map<Identifier, Recipe<?>> recipes = this.recipes.get(type);

			if (recipes == null) return null;

			return (T) recipes.get(id);
		}

		@Override
		public Map<RecipeType<?>, Map<Identifier, Recipe<?>>> getRecipes() {
			return this.recipes;
		}

		@Override
		public Collection<Recipe<?>> getRecipesOfType(RecipeType<?> type) {
			Map<Identifier, Recipe<?>> recipes = this.recipes.get(type);

			if (recipes == null) {
				return ImmutableList.of();
			}

			return recipes.values();
		}
	}
}
