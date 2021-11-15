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

package net.fabricmc.fabric.api.datagen.v1.provider;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.minecraft.data.DataCache;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public abstract class FabricRecipesProvider extends RecipesProvider {
	private final FabricDataGenerator dataGenerator;

	public FabricRecipesProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
		this.dataGenerator = dataGenerator;
	}

	protected abstract void generateRecipes(Consumer<RecipeJsonProvider> exporter);

	@Override
	public void run(DataCache cache) {
		Path path = this.root.getOutput();
		Set<Identifier> generatedRecipes = new HashSet<>();
		generateRecipes(provider -> {
			Identifier identifier = provider.getRecipeId();

			if (!generatedRecipes.add(identifier)) {
				throw new IllegalStateException("Duplicate recipe " + identifier);
			}

			saveRecipe(cache, provider.toJson(), path.resolve("data/" + identifier.getNamespace() + "/recipes/" + identifier.getPath() + ".json"));
			JsonObject jsonObject = provider.toAdvancementJson();

			if (jsonObject != null) {
				saveRecipeAdvancement(cache, jsonObject, path.resolve("data/" + identifier.getNamespace() + "/advancements/" + provider.getAdvancementId().getPath() + ".json"));
			}
		});
	}
}
