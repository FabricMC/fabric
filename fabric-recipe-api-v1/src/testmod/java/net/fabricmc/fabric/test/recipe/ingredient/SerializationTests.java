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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.recipe.Ingredient;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

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
			Ingredient.fromJson(json);
			throw new GameTestException("Using a custom ingredient inside an array ingredient should have failed.");
		} catch (IllegalArgumentException e) {
			context.complete();
		}
	}
}
