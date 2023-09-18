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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;

/**
 * Extend this class and implement {@link FabricRecipeProvider#generate}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}.
 */
public abstract class FabricRecipeProvider extends RecipeProvider {
	protected final FabricDataOutput output;

	public FabricRecipeProvider(FabricDataOutput output) {
		super(output);
		this.output = output;
	}

	/**
	 * Implement this method and then use the range of methods in {@link RecipeProvider} or from one of the recipe json factories such as {@link ShapedRecipeJsonBuilder} or {@link ShapelessRecipeJsonBuilder}.
	 */
	@Override
	public abstract void generate(RecipeExporter exporter);

	/**
	 * Return a new exporter that applies the specified conditions to any recipe json provider it receives.
	 */
	protected RecipeExporter withConditions(RecipeExporter exporter, ConditionJsonProvider... conditions) {
		Preconditions.checkArgument(conditions.length > 0, "Must add at least one condition.");
		return new RecipeExporter() {
			@Override
			public void accept(RecipeJsonProvider provider) {
				FabricDataGenHelper.addConditions(provider, conditions);
				exporter.accept(provider);
			}

			@Override
			public Advancement.Builder getAdvancementBuilder() {
				return exporter.getAdvancementBuilder();
			}
		};
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Set<Identifier> generatedRecipes = Sets.newHashSet();
		List<CompletableFuture<?>> list = new ArrayList<>();
		generate(new RecipeExporter() {
			@Override
			public void accept(RecipeJsonProvider provider) {
				Identifier identifier = getRecipeIdentifier(provider.id());

				if (!generatedRecipes.add(identifier)) {
					throw new IllegalStateException("Duplicate recipe " + identifier);
				}

				JsonObject recipeJson = provider.toJson();
				ConditionJsonProvider[] conditions = FabricDataGenHelper.consumeConditions(provider);
				ConditionJsonProvider.write(recipeJson, conditions);

				list.add(DataProvider.writeToPath(writer, recipeJson, recipesPathResolver.resolveJson(identifier)));

				AdvancementEntry advancement = provider.advancement();

				if (advancement != null) {
					JsonObject advancementJson = advancement.value().toJson();
					ConditionJsonProvider.write(advancementJson, conditions);
					list.add(DataProvider.writeToPath(writer, advancementJson, advancementsPathResolver.resolveJson(getRecipeIdentifier(advancement.id()))));
				}
			}

			@Override
			public Advancement.Builder getAdvancementBuilder() {
				return Advancement.Builder.createUntelemetered().parent(CraftingRecipeJsonBuilder.ROOT);
			}
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	/**
	 * Override this method to change the recipe identifier. The default implementation normalizes the namespace to the mod ID.
	 */
	protected Identifier getRecipeIdentifier(Identifier identifier) {
		return new Identifier(output.getModId(), identifier.getPath());
	}
}
