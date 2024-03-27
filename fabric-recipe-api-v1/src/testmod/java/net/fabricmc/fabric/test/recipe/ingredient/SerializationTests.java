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

package net.fabricmc.fabric.test.recipe.ingredient;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;

public class SerializationTests {
	/**
	 * Check that trying to use a custom ingredient inside an array ingredient fails.
	 */
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testArrayDeserialization(TestContext context) {
		String ingredientJson = """
[
	{
		"fabric:type": "fabric:all",
		"ingredients": [
			{
				"item": "minecraft:stone"
			}
		]
	}, {
		"item": "minecraft:dirt"
	}
]
				""";
		JsonElement json = JsonParser.parseString(ingredientJson);

		try {
			Ingredient.DISALLOW_EMPTY_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
			throw new GameTestException("Using a custom ingredient inside an array ingredient should have failed.");
		} catch (JsonParseException e) {
			context.complete();
		}
	}

	/**
	 * Check that we can serialise and deserialize a custom ingredient.
	 */
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testCustomIngredientSerialization(TestContext context) {
		for (boolean allowEmpty : List.of(false, true)) {
			String ingredientJson = """
					{"ingredients":[{"item":"minecraft:stone"}],"fabric:type":"fabric:all"}
					""".trim();

			Ingredient ingredient = DefaultCustomIngredients.all(
					Ingredient.ofItems(Items.STONE)
			);
			Codec<Ingredient> ingredientCodec = allowEmpty ? Ingredient.ALLOW_EMPTY_CODEC : Ingredient.DISALLOW_EMPTY_CODEC;
			JsonObject json = ingredientCodec.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow(IllegalStateException::new).getAsJsonObject();
			context.assertTrue(json.toString().equals(ingredientJson), "Unexpected json: " + json);
			// Make sure that we can deserialize it
			Ingredient deserialized = ingredientCodec.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
			context.assertTrue(deserialized.getCustomIngredient() != null, "Custom ingredient was not deserialized");
			context.assertTrue(deserialized.getCustomIngredient().getSerializer() == ingredient.getCustomIngredient().getSerializer(), "Serializer did not match");
		}

		context.complete();
	}
}
