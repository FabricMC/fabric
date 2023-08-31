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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

/**
 * Base class for ALL and ANY ingredients.
 */
abstract class CombinedIngredient implements CustomIngredient {
	protected final List<Ingredient> ingredients;

	protected CombinedIngredient(List<Ingredient> ingredients) {
		if (ingredients.isEmpty()) {
			throw new IllegalArgumentException("ALL or ANY ingredient must have at least one sub-ingredient");
		}

		this.ingredients = ingredients;
	}

	@Override
	public boolean requiresTesting() {
		for (Ingredient ingredient : ingredients) {
			if (ingredient.requiresTesting()) {
				return true;
			}
		}

		return false;
	}

	List<Ingredient> getIngredients() {
		return ingredients;
	}

	static class Serializer<I extends CombinedIngredient> implements CustomIngredientSerializer<I> {
		private final Identifier identifier;
		private final Function<List<Ingredient>, I> factory;
		private final Codec<I> allowEmptyCodec;
		private final Codec<I> disallowEmptyCodec;

		Serializer(Identifier identifier, Function<List<Ingredient>, I> factory, Codec<I> allowEmptyCodec, Codec<I> disallowEmptyCodec) {
			this.identifier = identifier;
			this.factory = factory;
			this.allowEmptyCodec = allowEmptyCodec;
			this.disallowEmptyCodec = disallowEmptyCodec;
		}

		@Override
		public Identifier getIdentifier() {
			return identifier;
		}

		@Override
		public Codec<I> getCodec(boolean allowEmpty) {
			return allowEmpty ? allowEmptyCodec : disallowEmptyCodec;
		}

		@Override
		public I read(PacketByteBuf buf) {
			int size = buf.readVarInt();
			List<Ingredient> ingredients = new ArrayList<>(size);

			for (int i = 0; i < size; i++) {
				ingredients.add(Ingredient.fromPacket(buf));
			}

			return factory.apply(Collections.unmodifiableList(ingredients));
		}

		@Override
		public void write(PacketByteBuf buf, I ingredient) {
			buf.writeVarInt(ingredient.ingredients.size());

			for (Ingredient value : ingredient.ingredients) {
				value.write(buf);
			}
		}
	}
}
