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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

/**
 * Provides some recipe builders for Vanilla recipes.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public final class VanillaRecipeBuilders {
	private VanillaRecipeBuilders() {
		throw new UnsupportedOperationException("Someone tampered with the universe.");
	}

	/**
	 * Returns the list of ingredients for shaped crafting recipes.
	 *
	 * @param pattern the pattern of the shaped crafting recipe
	 * @param keys    the keys and ingredients of the recipe
	 * @param width   the width of the shaped crafting recipe
	 * @param height  the height of the shaped crafting recipe
	 * @return the ingredients
	 * @throws IllegalStateException if a key has no assigned ingredient or if there is an ingredient but no assigned key
	 */
	public static DefaultedList<Ingredient> getIngredients(String[] pattern, Char2ObjectMap<Ingredient> keys, int width, int height) {
		DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY);
		CharSet patternSet = new CharArraySet(keys.keySet());
		patternSet.remove(' ');

		for (int i = 0; i < pattern.length; ++i) {
			for (int j = 0; j < pattern[i].length(); ++j) {
				char key = pattern[i].charAt(j);
				Ingredient ingredient = keys.get(key);

				if (ingredient == null) {
					throw new IllegalStateException("Pattern references symbol '" + key + "' but it's not defined in the key");
				}

				patternSet.remove(key);
				ingredients.set(j + width * i, ingredient);
			}
		}

		if (!patternSet.isEmpty()) {
			throw new IllegalStateException("Key defines symbols that aren't used in pattern: " + patternSet);
		} else {
			return ingredients;
		}
	}

	/**
	 * Returns a new shaped crafting recipe builder.
	 *
	 * @param pattern the pattern of the shaped crafting recipe
	 * @return the builder
	 */
	public static ShapedRecipeBuilder shapedRecipe(String[] pattern) {
		return new ShapedRecipeBuilder(pattern);
	}

	/**
	 * Returns a new shapeless crafting recipe builder.
	 *
	 * @param output the output stack
	 * @return the builder
	 */
	public static ShapelessRecipeBuilder shapelessRecipe(ItemStack output) {
		return new ShapelessRecipeBuilder(output);
	}

	/**
	 * Returns a new stone cutting recipe.
	 *
	 * @param id     the identifier of the recipe
	 * @param group  the group of the recipe
	 * @param input  the input ingredient
	 * @param output the output item stack
	 * @return the stone cutting recipe
	 */
	public static StonecuttingRecipe stonecuttingRecipe(Identifier id, String group, Ingredient input, ItemStack output) {
		if (input == Ingredient.EMPTY) throw new IllegalArgumentException("Input cannot be empty.");

		return new StonecuttingRecipe(id, group, input, output);
	}

	/**
	 * Returns a new smelting recipe.
	 *
	 * @param id         the identifier of the recipe
	 * @param group      the group of the recipe
	 * @param input      the input ingredient
	 * @param output     the output item stack
	 * @param experience the experience given
	 * @param cookTime   the cook time in ticks
	 * @return the smelting recipe
	 */
	public static SmeltingRecipe smeltingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
		if (input == Ingredient.EMPTY) throw new IllegalArgumentException("Input cannot be empty.");
		if (cookTime < 0) throw new IllegalArgumentException("Cook time must be equal or greater than 0");

		return new SmeltingRecipe(id, group, input, output, experience, cookTime);
	}

	/**
	 * Returns a new blasting recipe.
	 *
	 * @param id         the identifier of the recipe
	 * @param group      the group of the recipe
	 * @param input      the input ingredient
	 * @param output     the output item stack
	 * @param experience the experience given
	 * @param cookTime   the cook time in ticks
	 * @return the blasting recipe
	 */
	public static BlastingRecipe blastingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
		if (input == Ingredient.EMPTY) throw new IllegalArgumentException("Input cannot be empty.");
		if (cookTime < 0) throw new IllegalArgumentException("Cook time must be equal or greater than 0");

		return new BlastingRecipe(id, group, input, output, experience, cookTime);
	}

	/**
	 * Returns a new smoking recipe.
	 *
	 * @param id         the identifier of the recipe
	 * @param group      the group of the recipe
	 * @param input      the input ingredient
	 * @param output     the output item stack
	 * @param experience the experience given
	 * @param cookTime   the cook time in ticks
	 * @return the smoking recipe
	 */
	public static SmokingRecipe smokingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
		if (input == Ingredient.EMPTY) throw new IllegalArgumentException("Input cannot be empty.");
		if (cookTime < 0) throw new IllegalArgumentException("Cook time must be equal or greater than 0");

		return new SmokingRecipe(id, group, input, output, experience, cookTime);
	}

	/**
	 * Returns a new campfire cooking recipe.
	 *
	 * @param id         the identifier of the recipe
	 * @param group      the group of the recipe
	 * @param input      the input ingredient
	 * @param output     the output item stack
	 * @param experience the experience given
	 * @param cookTime   the cook time in ticks
	 * @return the campfire cooking recipe
	 */
	public static CampfireCookingRecipe campfireCookingRecipe(Identifier id, String group, Ingredient input,
															ItemStack output, float experience, int cookTime) {
		if (input == Ingredient.EMPTY) throw new IllegalArgumentException("Input cannot be empty.");
		if (cookTime < 0) throw new IllegalArgumentException("Cook time must be equal or greater than 0");

		return new CampfireCookingRecipe(id, group, input, output, experience, cookTime);
	}

	/**
	 * Represents a shaped crafting recipe builder.
	 */
	public static final class ShapedRecipeBuilder {
		private final String[] pattern;
		private final int width;
		private final int height;
		private final Char2ObjectMap<Ingredient> ingredients = new Char2ObjectOpenHashMap<>();
		private ItemStack output;

		/**
		 * Creates a new shaped recipe builder.
		 *
		 * @param pattern the pattern of the shaped recipe. Each string in this array is a line of ingredients.
		 *                A character represents an ingredient and space is no ingredient
		 */
		public ShapedRecipeBuilder(String[] pattern) {
			this.pattern = pattern;
			this.width = pattern[0].length();
			this.height = pattern.length;
		}

		/**
		 * Puts the specified ingredient at the specified key.
		 *
		 * @param key        the key of the ingredient
		 * @param ingredient the ingredient
		 * @return this builder
		 */
		public ShapedRecipeBuilder ingredient(char key, Ingredient ingredient) {
			boolean success = false;

			for (String line : pattern) {
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);

					if (c == key) {
						this.ingredients.put(key, ingredient);

						success = true;
						break;
					}
				}

				if (success) break;
			}

			return this;
		}

		/**
		 * Puts the specified items as the accepted ingredient at the specified key.
		 *
		 * @param key   the key of the ingredient
		 * @param items the items as ingredient
		 * @return this builder
		 * @see #ingredient(char, Ingredient)
		 */
		public ShapedRecipeBuilder ingredient(char key, ItemConvertible... items) {
			return this.ingredient(key, Ingredient.ofItems(items));
		}

		/**
		 * Puts the specified item tag as the accepted ingredient at the specified key.
		 *
		 * @param key the key of the ingredient
		 * @param tag the item tag as ingredient
		 * @return this builder
		 * @see #ingredient(char, Ingredient)
		 */
		public ShapedRecipeBuilder ingredient(char key, Tag<Item> tag) {
			return this.ingredient(key, Ingredient.fromTag(tag));
		}

		/**
		 * Puts the specified item stacks as the accepted ingredient at the specified key.
		 *
		 * @param key    the key of the ingredient
		 * @param stacks the item stacks as ingredient
		 * @return this builder
		 * @see #ingredient(char, Ingredient)
		 */
		public ShapedRecipeBuilder ingredient(char key, ItemStack... stacks) {
			return this.ingredient(key, Ingredient.ofStacks(stacks));
		}

		/**
		 * Sets the output of the shaped crafting recipe.
		 *
		 * @param stack the output item stack.
		 * @return this builder
		 */
		public ShapedRecipeBuilder output(ItemStack stack) {
			this.output = stack;
			return this;
		}

		/**
		 * Builds the shaped crafting recipe.
		 *
		 * @param id    the identifier of the recipe
		 * @param group the group of the recipe
		 * @return the shaped recipe
		 */
		public ShapedRecipe build(Identifier id, String group) {
			Objects.requireNonNull(this.output, "The output stack cannot be null.");
			DefaultedList<Ingredient> ingredients = getIngredients(this.pattern, this.ingredients, this.width, this.height);
			return new ShapedRecipe(id, group, this.width, this.height, ingredients, this.output);
		}
	}

	public static final class ShapelessRecipeBuilder {
		private final Set<Ingredient> ingredients = new HashSet<>();
		private ItemStack output;

		public ShapelessRecipeBuilder(ItemStack output) {
			this.output = output;
		}

		/**
		 * Adds an ingredient.
		 *
		 * @param ingredient the ingredient
		 * @return this builder
		 */
		public ShapelessRecipeBuilder ingredient(Ingredient ingredient) {
			this.ingredients.add(ingredient);
			return this;
		}

		/**
		 * Puts the specified items as the accepted ingredient at the specified key.
		 *
		 * @param items the items as ingredient
		 * @return this builder
		 * @see #ingredient(Ingredient)
		 */
		public ShapelessRecipeBuilder ingredient(ItemConvertible... items) {
			return this.ingredient(Ingredient.ofItems(items));
		}

		/**
		 * Adds the specified item tag as an ingredient.
		 *
		 * @param tag the item tag as ingredient
		 * @return this builder
		 * @see #ingredient(Ingredient)
		 */
		public ShapelessRecipeBuilder ingredient(Tag<Item> tag) {
			return this.ingredient(Ingredient.fromTag(tag));
		}

		/**
		 * Adds item stacks as an ingredient.
		 *
		 * @param stacks the item stacks as ingredient
		 * @return this builder
		 * @see #ingredient(Ingredient)
		 */
		public ShapelessRecipeBuilder ingredient(ItemStack... stacks) {
			return this.ingredient(Ingredient.ofStacks(stacks));
		}

		/**
		 * Sets the output of the shapeless crafting recipe.
		 *
		 * @param stack the output item stack.
		 * @return this builder
		 */
		public ShapelessRecipeBuilder output(ItemStack stack) {
			this.output = stack;
			return this;
		}

		/**
		 * Builds the shapeless crafting recipe.
		 *
		 * @param id    the identifier of the recipe
		 * @param group the group of the recipe
		 * @return the shapeless crafting recipe
		 */
		public ShapelessRecipe build(Identifier id, String group) {
			Objects.requireNonNull(this.output, "The output stack cannot be null.");

			if (ingredients.size() == 0) throw new IllegalStateException("Cannot build a recipe without ingredients.");

			DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(this.ingredients.size(), Ingredient.EMPTY);
			int i = 0;

			for (Ingredient ingredient : this.ingredients) {
				ingredients.set(i, ingredient);
				i++;
			}

			return new ShapelessRecipe(id, group, this.output, ingredients);
		}
	}
}
