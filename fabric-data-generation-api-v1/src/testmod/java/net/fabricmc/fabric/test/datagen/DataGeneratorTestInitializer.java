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

package net.fabricmc.fabric.test.datagen;

import static net.fabricmc.fabric.test.datagen.DataGeneratorTestContent.SIMPLE_BLOCK;

import java.util.function.Consumer;

import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorInitializer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockStateDefinitionProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipesProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class DataGeneratorTestInitializer implements DataGeneratorInitializer {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		dataGenerator.install(TestRecipeProvider::new);
		dataGenerator.install(TestBlockStateDefinitionProvider::new);

		TestBlockTagsProvider blockTagsProvider = dataGenerator.install(TestBlockTagsProvider::new);
		dataGenerator.install(new TestItemTagsProvider(dataGenerator, blockTagsProvider));
	}

	private static class TestRecipeProvider extends FabricRecipesProvider {
		private TestRecipeProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		public void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
			offerPlanksRecipe2(exporter, SIMPLE_BLOCK, ItemTags.ACACIA_LOGS);
		}
	}

	private static class TestBlockStateDefinitionProvider extends FabricBlockStateDefinitionProvider {
		private TestBlockStateDefinitionProvider(FabricDataGenerator generator) {
			super(generator);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
			blockStateModelGenerator.registerSimpleCubeAll(SIMPLE_BLOCK);
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator) {
			//itemModelGenerator.register(item, Models.SLAB);
		}
	}

	private static class TestBlockTagsProvider extends FabricTagProvider.Blocks {
		private TestBlockTagsProvider(FabricDataGenerator root) {
			super(root);
		}

		@Override
		protected void configure() {
			getOrCreateTagBuilder(BlockTags.FIRE).add(SIMPLE_BLOCK);
			getOrCreateTagBuilder(BlockTags.ANVIL).setReplace(true).add(SIMPLE_BLOCK);
		}
	}

	private static class TestItemTagsProvider extends FabricTagProvider.Items {
		private TestItemTagsProvider(FabricDataGenerator root, Blocks blockTagProvider) {
			super(root, blockTagProvider);
		}

		@Override
		protected void configure() {
			copy(BlockTags.ANVIL, ItemTags.ANVIL);
		}
	}
}
