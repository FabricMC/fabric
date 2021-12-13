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

import static net.fabricmc.fabric.test.datagen.DataGeneratorTestContent.MOD_ID;
import static net.fabricmc.fabric.test.datagen.DataGeneratorTestContent.SIMPLE_BLOCK;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementsProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTablesProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockStateDefinitionProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipesProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.fabricmc.fabric.api.tag.TagFactory;

public class DataGeneratorTestEntrypoint implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		dataGenerator.addProvider(TestRecipeProvider::new);
		dataGenerator.addProvider(TestBlockStateDefinitionProvider::new);
		dataGenerator.addProvider(TestAdvancementsProvider::new);
		dataGenerator.addProvider(TestBlockLootTablesProvider::new);
		dataGenerator.addProvider(TestBarterLootTablesProvider::new);

		TestBlockTagsProvider blockTagsProvider = dataGenerator.addProvider(TestBlockTagsProvider::new);
		dataGenerator.addProvider(new TestItemTagsProvider(dataGenerator, blockTagsProvider));
		dataGenerator.addProvider(TestBiomeTagsProvider::new);

		try {
			new FabricTagProvider<>(dataGenerator, BuiltinRegistries.BIOME, "biomes", "Biome Tags") {
				@Override
				protected void generateTags() {
				}
			};
		} catch (IllegalArgumentException e) {
			// no-op
		}

		try {
			new FabricTagProvider.DynamicRegistryTagProvider<>(dataGenerator, Registry.ITEM_KEY, "items", "Item Tags") {
				@Override
				protected void generateTags() {
				}
			};
		} catch (IllegalArgumentException e) {
			// no-op
		}
	}

	private static class TestRecipeProvider extends FabricRecipesProvider {
		private TestRecipeProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
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

	private static class TestBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
		private TestBlockTagsProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		protected void generateTags() {
			getOrCreateTagBuilder(BlockTags.FIRE).add(SIMPLE_BLOCK);
			getOrCreateTagBuilder(BlockTags.ANVIL).setReplace(true).add(SIMPLE_BLOCK);
		}
	}

	private static class TestItemTagsProvider extends FabricTagProvider.ItemTagProvider {
		private TestItemTagsProvider(FabricDataGenerator dataGenerator, BlockTagProvider blockTagProvider) {
			super(dataGenerator, blockTagProvider);
		}

		@Override
		protected void generateTags() {
			copy(BlockTags.ANVIL, ItemTags.ANVIL);
		}
	}

	private static class TestBiomeTagsProvider extends FabricTagProvider.DynamicRegistryTagProvider<Biome> {
		private TestBiomeTagsProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, Registry.BIOME_KEY, "biomes", "Biome Tags");
		}

		@Override
		protected void generateTags() {
			FabricTagBuilder<Biome> builder = getOrCreateTagBuilder(TagFactory.BIOME.create(new Identifier(MOD_ID, "biome_tag_test")))
					.add(BiomeKeys.BADLANDS, BiomeKeys.BAMBOO_JUNGLE)
					.add(BiomeKeys.BASALT_DELTAS);

			try {
				builder.add(BuiltinBiomes.PLAINS);
			} catch (UnsupportedOperationException e) {
				// no-op
			}
		}
	}

	private static class TestAdvancementsProvider extends FabricAdvancementsProvider {
		private TestAdvancementsProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		public void generateAdvancement(Consumer<Advancement> consumer) {
			Advancement root = Advancement.Task.create()
					.display(
							SIMPLE_BLOCK,
							new TranslatableText("advancements.test.root.title"),
							new TranslatableText("advancements.test.root.description"),
							new Identifier("textures/gui/advancements/backgrounds/end.png"),
							AdvancementFrame.TASK,
							false, false, false)
					.criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity())
					.build(consumer, MOD_ID + ":test/root");
		}
	}

	private static class TestBlockLootTablesProvider extends FabricBlockLootTablesProvider {
		private TestBlockLootTablesProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator);
		}

		@Override
		protected void generateBlockLootTables() {
			addDrop(SIMPLE_BLOCK);
		}
	}

	private static class TestBarterLootTablesProvider extends SimpleFabricLootTableProvider {
		private TestBarterLootTablesProvider(FabricDataGenerator dataGenerator) {
			super(dataGenerator, LootContextTypes.BARTER);
		}

		@Override
		public void accept(BiConsumer<Identifier, LootTable.Builder> consumer) {
			consumer.accept(
					LootTables.PIGLIN_BARTERING_GAMEPLAY,
					LootTable.builder().pool(
							LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(SIMPLE_BLOCK))
					)
			);
		}
	}
}
