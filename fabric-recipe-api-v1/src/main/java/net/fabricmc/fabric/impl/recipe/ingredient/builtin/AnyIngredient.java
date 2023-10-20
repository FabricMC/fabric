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

package net.fabricmc.fabric.impl.recipe.ingredient.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

public class AnyIngredient extends CombinedIngredient {
	private static final Codec<AnyIngredient> ALLOW_EMPTY_CODEC = createCodec(Ingredient.ALLOW_EMPTY_CODEC);
	private static final Codec<AnyIngredient> DISALLOW_EMPTY_CODEC = createCodec(Ingredient.DISALLOW_EMPTY_CODEC);

	private static Codec<AnyIngredient> createCodec(Codec<Ingredient> ingredientCodec) {
		return ingredientCodec
				.listOf()
				.fieldOf("ingredients")
				.xmap(AnyIngredient::new, AnyIngredient::getIngredients)
				.codec();
	}

	public static final CustomIngredientSerializer<AnyIngredient> SERIALIZER =
			new CombinedIngredient.Serializer<>(new Identifier("fabric", "any"), AnyIngredient::new, ALLOW_EMPTY_CODEC, DISALLOW_EMPTY_CODEC);

	public AnyIngredient(List<Ingredient> ingredients) {
		super(ingredients);
	}

	@Override
	public boolean test(ItemStack stack) {
		for (Ingredient ingredient : ingredients) {
			if (ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		List<ItemStack> previewStacks = new ArrayList<>();

		for (Ingredient ingredient : ingredients) {
			previewStacks.addAll(Arrays.asList(ingredient.getMatchingStacks()));
		}

		return previewStacks;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
