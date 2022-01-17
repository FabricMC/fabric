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

package net.fabricmc.fabric.test.resource.conditions;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

public class ConditionalResourcesTest {
	private static final String MOD_ID = "fabric-resource-conditions-api-v1-testmod";

	private static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)
	public void conditionalRecipes(TestContext context) {
		RecipeManager manager = context.getWorld().getRecipeManager();

		if (manager.get(id("not_loaded")).isPresent()) {
			throw new AssertionError("not_loaded recipe should not have been loaded.");
		}

		if (manager.get(id("loaded")).isEmpty()) {
			throw new AssertionError("loaded recipe should have been loaded.");
		}

		if (manager.get(id("item_tags_populated")).isEmpty()) {
			throw new AssertionError("item_tags_populated recipe should have been loaded.");
		}

		long loadedRecipes = manager.values().stream().filter(r -> r.getId().getNamespace().equals(MOD_ID)).count();
		if (loadedRecipes != 2) throw new AssertionError("Unexpected loaded recipe count: " + loadedRecipes);

		context.complete();
	}
}
