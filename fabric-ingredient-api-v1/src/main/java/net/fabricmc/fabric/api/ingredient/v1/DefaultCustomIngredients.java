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

package net.fabricmc.fabric.api.ingredient.v1;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.recipe.Ingredient;

import net.fabricmc.fabric.impl.ingredient.builtin.AndIngredient;
import net.fabricmc.fabric.impl.ingredient.builtin.DifferenceIngredient;
import net.fabricmc.fabric.impl.ingredient.builtin.NbtIngredient;
import net.fabricmc.fabric.impl.ingredient.builtin.OrIngredient;

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
	 *     "fabric:type": "fabric:and",
	 *     "ingredients": [
	 *         // sub-ingredient 1,
	 *         // sub-ingredient 2,
	 *         // etc...
	 *     ]
	 * }
	 * }</pre>
	 */
	public static Ingredient and(Ingredient... ingredients) {
		for (Ingredient ing : ingredients) Objects.requireNonNull(ing);

		return new AndIngredient(ingredients).toVanilla();
	}

	/**
	 * Creates an ingredient that matches when any of its sub-ingredients matches.
	 *
	 * <p>The JSON format is as follows:
	 * <pre>{@code
	 * {
	 *     "fabric:type": "fabric:or",
	 *     "ingredients": [
	 *         // sub-ingredient 1,
	 *         // sub-ingredient 2,
	 *         // etc...
	 *     ]
	 * }
	 * }</pre>
	 */
	public static Ingredient or(Ingredient... ingredients) {
		for (Ingredient ing : ingredients) Objects.requireNonNull(ing);

		return new OrIngredient(ingredients).toVanilla();
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
		Objects.requireNonNull(base);
		Objects.requireNonNull(subtracted);

		return new DifferenceIngredient(base, subtracted).toVanilla();
	}

	/**
	 * Creates an ingredient that wraps another ingredient to also check for stack NBT.
	 * This check can either be strict (the exact NBT must match) or non-strict (the ingredient NBT must be a subset of the stack NBT).
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
	 */
	public static Ingredient nbt(Ingredient base, @Nullable NbtCompound nbt, boolean strict) {
		Objects.requireNonNull(base);

		return new NbtIngredient(base, nbt, strict).toVanilla();
	}

	/**
	 * Creates an ingredient that matches the passed template stack, including NBT.
	 * This check can either be strict (the exact NBT must match) or non-strict (the template NBT must be a subset of the stack NBT).
	 *
	 * <p>See {@link NbtHelper#matches} for the non-strict matching.
	 */
	public static Ingredient nbt(ItemStack stack, boolean strict) {
		Objects.requireNonNull(stack);

		return nbt(Ingredient.ofItems(stack.getItem()), stack.getNbt(), strict);
	}

	private DefaultCustomIngredients() {
	}
}
