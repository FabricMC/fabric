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

package net.fabricmc.fabric.api.registry;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlag;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * An extension of {@link BrewingRecipeRegistry.Builder} to support ingredients.
 */
public interface FabricBrewingRecipeRegistryBuilder {
	/**
	 * An event that is called when the brewing recipe registry is being built.
	 */
	Event<FabricBrewingRecipeRegistryBuilder.BuildCallback> BUILD = EventFactory.createArrayBacked(FabricBrewingRecipeRegistryBuilder.BuildCallback.class, listeners -> builder -> {
		for (FabricBrewingRecipeRegistryBuilder.BuildCallback listener : listeners) {
			listener.build(builder);
		}
	});

	default void registerItemRecipe(Item input, Ingredient ingredient, Item output, FeatureFlag... requiredFeatures) {
		throw new AssertionError("Must be implemented via interface injection");
	}

	default void registerPotionRecipe(RegistryEntry<Potion> input, Ingredient ingredient, RegistryEntry<Potion> output, FeatureFlag... requiredFeatures) {
		throw new AssertionError("Must be implemented via interface injection");
	}

	default void registerRecipes(Ingredient ingredient, RegistryEntry<Potion> potion, FeatureFlag... requiredFeatures) {
		throw new AssertionError("Must be implemented via interface injection");
	}

	/**
	 * Use this event to register custom brewing recipes.
	 */
	@FunctionalInterface
	interface BuildCallback {
		/**
		 * Called when the brewing recipe registry is being built.
		 *
		 * @param builder the {@link BrewingRecipeRegistry} instance
		 */
		void build(BrewingRecipeRegistry.Builder builder);
	}
}
