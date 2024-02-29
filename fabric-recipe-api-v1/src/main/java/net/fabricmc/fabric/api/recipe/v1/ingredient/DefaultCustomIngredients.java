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

package net.fabricmc.fabric.api.recipe.v1.ingredient;

import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import net.fabricmc.fabric.impl.recipe.ingredient.builtin.AllIngredient;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.AnyIngredient;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.ComponentIngredient;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.DifferenceIngredient;

/**
 * Factory methods for the custom ingredients directly provided by Fabric API.
 */
public final class DefaultCustomIngredients {
	/**
	 * Creates an ingredient that matches when its sub-ingredients all match.
	 *
	 * <p>The JSON format is as follows:
	 * <pre>{@code
	 * {
	 *     "fabric:type": "fabric:all",
	 *     "ingredients": [
	 *         // sub-ingredient 1,
	 *         // sub-ingredient 2,
	 *         // etc...
	 *     ]
	 * }
	 * }</pre>
	 *
	 * @throws IllegalArgumentException if the array is empty
	 */
	public static Ingredient all(Ingredient... ingredients) {
		for (Ingredient ing : ingredients) Objects.requireNonNull(ing, "Ingredient cannot be null");

		return new AllIngredient(List.of(ingredients)).toVanilla();
	}

	/**
	 * Creates an ingredient that matches when any of its sub-ingredients matches.
	 *
	 * <p>The JSON format is as follows:
	 * <pre>{@code
	 * {
	 *     "fabric:type": "fabric:any",
	 *     "ingredients": [
	 *         // sub-ingredient 1,
	 *         // sub-ingredient 2,
	 *         // etc...
	 *     ]
	 * }
	 * }</pre>
	 *
	 * @throws IllegalArgumentException if the array is empty
	 */
	public static Ingredient any(Ingredient... ingredients) {
		for (Ingredient ing : ingredients) Objects.requireNonNull(ing, "Ingredient cannot be null");

		return new AnyIngredient(List.of(ingredients)).toVanilla();
	}

	/**
	 * Creates an ingredient that matches if its base ingredient matches, and its subtracted ingredient <strong>does not</strong> match.
	 *
	 * <p>The JSON format is as follows:
	 * <pre>{@code
	 * {
	 *     "fabric:type": "fabric:difference",
	 *     "base": // base ingredient,
	 *     "subtracted": // subtracted ingredient
	 * }
	 * }</pre>
	 */
	public static Ingredient difference(Ingredient base, Ingredient subtracted) {
		Objects.requireNonNull(base, "Base ingredient cannot be null");
		Objects.requireNonNull(subtracted, "Subtracted ingredient cannot be null");

		return new DifferenceIngredient(base, subtracted).toVanilla();
	}

	/**
	 * Creates an ingredient that wraps another ingredient to also check for matching components.
	 *
	 * <p>Use {@link ComponentChanges#builder()} to add or remove components.
	 * Added components are checked to match on the target stack.
	 * Removed components are checked to not exist in the target stack
	 *
	 * @throws IllegalArgumentException if {@link ComponentChanges#isEmpty} is true
	 */
	public static Ingredient components(Ingredient base, ComponentChanges components) {
		Objects.requireNonNull(base, "Base ingredient cannot be null");
		Objects.requireNonNull(components, "Component changes cannot be null");

		return new ComponentIngredient(base, components).toVanilla();
	}

	/**
	 * @see #components(Ingredient, ComponentChanges)
	 */
	public static Ingredient components(Ingredient base, UnaryOperator<ComponentChanges.Builder> operator) {
		return components(base, operator.apply(ComponentChanges.builder()).build());
	}

	/**
	 * Creates an ingredient that matches the passed template stack, including {@link ItemStack#getComponentChanges()}.
	 * Note that the count of the stack is ignored.
	 *
	 * @see #components(Ingredient, ComponentChanges)
	 */
	public static Ingredient components(ItemStack stack) {
		Objects.requireNonNull(stack, "Stack cannot be null");

		return components(Ingredient.ofItems(stack.getItem()), stack.getComponentChanges());
	}

	private DefaultCustomIngredients() {
	}
}
