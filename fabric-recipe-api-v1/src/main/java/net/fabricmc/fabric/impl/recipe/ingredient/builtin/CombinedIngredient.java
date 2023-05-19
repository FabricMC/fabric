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

import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;

/**
 * Base class for ALL and ANY ingredients.
 */
abstract class CombinedIngredient implements CustomIngredient {
	protected final Ingredient[] ingredients;

	protected CombinedIngredient(Ingredient[] ingredients) {
		if (ingredients.length == 0) {
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

	static class Serializer<I extends CombinedIngredient> implements CustomIngredientSerializer<I> {
		private final Identifier identifier;
		private final Function<Ingredient[], I> factory;

		Serializer(Identifier identifier, Function<Ingredient[], I> factory) {
			this.identifier = identifier;
			this.factory = factory;
		}

		@Override
		public Identifier getIdentifier() {
			return identifier;
		}

		@Override
		public I read(JsonObject json) {
			JsonArray values = JsonHelper.getArray(json, "ingredients");
			Ingredient[] ingredients = new Ingredient[values.size()];

			for (int i = 0; i < values.size(); i++) {
				ingredients[i] = Ingredient.fromJson(values.get(i));
			}

			return factory.apply(ingredients);
		}

		@Override
		public void write(JsonObject json, I ingredient) {
			JsonArray values = new JsonArray();

			for (Ingredient value : ingredient.ingredients) {
				values.add(value.toJson());
			}

			json.add("ingredients", values);
		}

		@Override
		public I read(PacketByteBuf buf) {
			int size = buf.readVarInt();
			Ingredient[] ingredients = new Ingredient[size];

			for (int i = 0; i < size; i++) {
				ingredients[i] = Ingredient.fromPacket(buf);
			}

			return factory.apply(ingredients);
		}

		@Override
		public void write(PacketByteBuf buf, I ingredient) {
			buf.writeVarInt(ingredient.ingredients.length);

			for (Ingredient value : ingredient.ingredients) {
				value.write(buf);
			}
		}
	}
}
