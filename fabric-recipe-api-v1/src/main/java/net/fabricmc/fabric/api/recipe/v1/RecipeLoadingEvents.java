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

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Represents the recipe loading events.
 * <p>Triggered when the recipes are loaded.</p>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public final class RecipeLoadingEvents {
	/**
	 * Recipe loading event, triggered when the recipes are loaded.
	 */
	public static final Event<RecipeLoadingCallback> REGISTER = EventFactory.createArrayBacked(RecipeLoadingCallback.class,
			callbacks -> handler -> {
				for (RecipeLoadingCallback callback : callbacks) {
					callback.onRecipeLoading(handler);
				}
			});
	public static final Event<RecipeModifyCallback> MODIFY = EventFactory.createArrayBacked(RecipeModifyCallback.class,
			callbacks -> handler -> {
				for (RecipeModifyCallback callback : callbacks) {
					callback.onRecipeModify(handler);
				}
			});

	private RecipeLoadingEvents() {
		throw new UnsupportedOperationException("Someone tampered with the universe.");
	}

	/**
	 * Callback called to register additional recipes when recipes are loaded.
	 */
	@FunctionalInterface
	public interface RecipeLoadingCallback {
		/**
		 * Called when recipes are loaded.
		 * <p>{@code handler} is used to add recipes into the {@linkplain net.minecraft.recipe.RecipeManager recipe manager}.</p>
		 *
		 * @param handler the recipe handler
		 */
		void onRecipeLoading(RecipeHandler handler);

		/**
		 * This interface should not be extended by users.
		 */
		@ApiStatus.NonExtendable
		interface RecipeHandler {
			/**
			 * Registers a recipe into the {@link net.minecraft.recipe.RecipeManager}.
			 *
			 * <p>The recipe factory is only called if the recipe can be registered.</p>
			 *
			 * @param id      identifier of the recipe
			 * @param factory the recipe factory
			 */
			void register(Identifier id, Function<Identifier, Recipe<?>> factory);
		}
	}

	/**
	 * Callback called to modify or replace recipes after recipes are loaded.
	 */
	@FunctionalInterface
	public interface RecipeModifyCallback {
		/**
		 * Called after recipes are loaded to modify and replace recipes.
		 *
		 * @param handler the recipe handler
		 */
		void onRecipeModify(RecipeHandler handler);

		@ApiStatus.NonExtendable
		interface RecipeHandler {
			/**
			 * Replaces a recipe in the {@link net.minecraft.recipe.RecipeManager}.
			 *
			 * @param id     identifier of the recipe to replace
			 * @param recipe the recipe
			 */
			void replace(Recipe<?> recipe);

			/**
			 * Returns the recipe type of the specified recipe.
			 *
			 * @param id the identifier of the recipe
			 * @return the recipe type if the recipe is present, else {@code null}
			 */
			@Nullable RecipeType<?> getTypeOf(Identifier id);

			/**
			 * Returns whether or not the {@link net.minecraft.recipe.RecipeManager} contains the specified recipe.
			 *
			 * @param id the identifier of the recipe
			 * @return {@code true} if the recipe is present in the {@link net.minecraft.recipe.RecipeManager}, else {@code false}
			 */
			boolean contains(Identifier id);

			/**
			 * Returns whether or not the {@link net.minecraft.recipe.RecipeManager} contains the specified recipe of the specified recipe type.
			 *
			 * @param id   the identifier of the recipe
			 * @param type the type of the recipe
			 * @return {@code true} if the recipe is present in the {@link net.minecraft.recipe.RecipeManager}, else {@code false}
			 */
			boolean contains(Identifier id, RecipeType<?> type);

			/**
			 * Returns the recipe in {@link net.minecraft.recipe.RecipeManager} from its identifier.
			 *
			 * @param id the identifier of the recipe
			 * @return the recipe if present, else {@code null}
			 */
			@Nullable Recipe<?> getRecipe(Identifier id);

			/**
			 * Returns the recipe of the specified recipe type in {@link net.minecraft.recipe.RecipeManager} from its identifier.
			 *
			 * @param id   the identifier of the recipe
			 * @param type the type of the recipe
			 * @return the recipe if present and of the correct type, else {@code null}
			 */
			@Nullable <T extends Recipe<?>> T getRecipe(Identifier id, RecipeType<T> type);
		}
	}
}
