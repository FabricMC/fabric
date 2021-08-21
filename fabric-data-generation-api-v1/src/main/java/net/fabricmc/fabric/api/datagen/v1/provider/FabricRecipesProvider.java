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
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import net.minecraft.data.DataCache;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;

/**
 * Extend this class and implement {@link FabricRecipesProvider#generateRecipes}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}
 */
public abstract class FabricRecipesProvider extends RecipesProvider {
	protected final FabricDataGenerator dataGenerator;

	public FabricRecipesProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
		this.dataGenerator = dataGenerator;
	}

	/**
	 * Implement this method and then use the range of methods in {@link RecipesProvider} or from one of the recipe json factories such as {@link ShapedRecipeJsonFactory} & {@link ShapelessRecipeJsonFactory}.
	 */
	protected abstract void generateRecipes(Consumer<RecipeJsonProvider> exporter);

	/**
	 * Return a new exporter that applies the specified conditions to any recipe json provider it receives.
	 */
	protected Consumer<RecipeJsonProvider> withConditions(Consumer<RecipeJsonProvider> exporter, ConditionJsonProvider... conditions) {
		Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
		return json -> {
			FabricDataGenHelper.addConditions(json, conditions);
			exporter.accept(json);
		};
	}

	@Override
	public void run(DataCache cache) {
		Path path = this.root.getOutput();
		Set<Identifier> generatedRecipes = Sets.newHashSet();
		generateRecipes(provider -> {
			Identifier identifier = getRecipeIdentifier(provider.getRecipeId());

			if (!generatedRecipes.add(identifier)) {
				throw new IllegalStateException("Duplicate recipe " + identifier);
			}

			JsonObject recipeJson = provider.toJson();
			ConditionJsonProvider[] conditions = FabricDataGenHelper.consumeConditions(provider);
			ConditionJsonProvider.write(recipeJson, conditions);

			saveRecipe(cache, recipeJson, path.resolve("data/" + identifier.getNamespace() + "/recipes/" + identifier.getPath() + ".json"));
			JsonObject advancementJson = provider.toAdvancementJson();

			if (advancementJson != null) {
				ConditionJsonProvider.write(advancementJson, conditions);
				saveRecipeAdvancement(cache, advancementJson, path.resolve("data/" + identifier.getNamespace() + "/advancements/" + provider.getAdvancementId().getPath() + ".json"));
			}
		});
	}

	/**
	 * Override this method to change the recipe identifier. The default implementation normalizes the namespace to the mod ID.
	 */
	protected Identifier getRecipeIdentifier(Identifier identifier) {
		return new Identifier(dataGenerator.getModId(), identifier.getPath());
	}
}
