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

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.recipe.Ingredient;

import net.fabricmc.fabric.impl.recipe.ingredient.builtin.AllIngredient;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.DifferenceIngredient;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.NbtIngredient;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.AnyIngredient;

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
	 * Creates an ingredient that wraps another ingredient to also check for stack NBT.
	 * This check can either be strict (the exact NBT must match) or non-strict aka. partial (the ingredient NBT must be a subset of the stack NBT).
	 *
	 * <p>In strict mode, passing a {@code null} {@code nbt} is allowed, and will only match stacks with {@code null} NBT.
	 * In partial mode, passing a {@code null} {@code nbt} is <strong>not</strong> allowed, as it would always match.
	 *
	 * <p>See {@link NbtHelper#matches} for the non-strict matching.
	 *
	 * <p>The JSON format is as follows:
	 * <pre>{@code
	 * {
	 *    "fabric:type": "fabric:nbt",
	 *    "base": // base ingredient,
	 *    "nbt": // NBT tag to match, either in JSON directly or a string representation (default: null),
	 *    "strict": // whether to use strict matching (default: false)
	 * }
	 * }</pre>
	 *
	 * @throws IllegalArgumentException if {@code strict} is {@code false} and the NBT is {@code null}
	 */
	public static Ingredient nbt(Ingredient base, @Nullable NbtCompound nbt, boolean strict) {
		Objects.requireNonNull(base, "Base ingredient cannot be null");

		return new NbtIngredient(base, nbt, strict).toVanilla();
	}

	/**
	 * Creates an ingredient that matches the passed template stack, including NBT.
	 * Note that the count of the stack is ignored.
	 *
	 * @see #nbt(Ingredient, NbtCompound, boolean)
	 */
	public static Ingredient nbt(ItemStack stack, boolean strict) {
		Objects.requireNonNull(stack, "Stack cannot be null");

		return nbt(Ingredient.ofItems(stack.getItem()), stack.getNbt(), strict);
	}

	private DefaultCustomIngredients() {
	}
}
