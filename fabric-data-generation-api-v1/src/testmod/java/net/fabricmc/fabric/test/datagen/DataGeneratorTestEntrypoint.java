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

import static net.fabricmc.fabric.test.datagen.DataGeneratorTestContent.BLOCK_WITHOUT_ITEM;
import static net.fabricmc.fabric.test.datagen.DataGeneratorTestContent.BLOCK_WITHOUT_LOOT_TABLE;
import static net.fabricmc.fabric.test.datagen.DataGeneratorTestContent.MOD_ID;
import static net.fabricmc.fabric.test.datagen.DataGeneratorTestContent.SIMPLE_BLOCK;
import static net.fabricmc.fabric.test.datagen.DataGeneratorTestContent.SIMPLE_ITEM_GROUP;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricSoundProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;

public class DataGeneratorTestEntrypoint implements DataGeneratorEntrypoint {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataGeneratorTestEntrypoint.class);
	private static final ConditionJsonProvider NEVER_LOADED = DefaultResourceConditions.allModsLoaded("a");
	private static final ConditionJsonProvider ALWAYS_LOADED = DefaultResourceConditions.not(NEVER_LOADED);

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		final FabricDataGenerator.Pack pack = dataGenerator.createPack();

		pack.addProvider(TestRecipeProvider::new);
		pack.addProvider(TestModelProvider::new);
		pack.addProvider(TestAdvancementProvider::new);
		pack.addProvider(TestBlockLootTableProvider::new);
		pack.addProvider(TestBarterLootTableProvider::new);
		pack.addProvider(ExistingEnglishLangProvider::new);
		pack.addProvider(JapaneseLangProvider::new);
		pack.addProvider(TestSoundProvider::new);

		TestBlockTagProvider blockTagProvider = pack.addProvider(TestBlockTagProvider::new);
		pack.addProvider((output, registries) -> new TestItemTagProvider(output, registries, blockTagProvider));
		pack.addProvider(TestBiomeTagProvider::new);
	}

	private static class TestRecipeProvider extends FabricRecipeProvider {
		private TestRecipeProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generate(Consumer<RecipeJsonProvider> exporter) {
			offerPlanksRecipe2(exporter, SIMPLE_BLOCK, ItemTags.ACACIA_LOGS, 1);

			ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.GOLD_INGOT).input(Items.DIRT).criterion("has_dirt", conditionsFromItem(Items.DIRT)).offerTo(withConditions(exporter, NEVER_LOADED));
			ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.DIAMOND).input(Items.STICK).criterion("has_stick", conditionsFromItem(Items.STICK)).offerTo(withConditions(exporter, ALWAYS_LOADED));
		}
	}

	private static class ExistingEnglishLangProvider extends FabricLanguageProvider {
		private ExistingEnglishLangProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateTranslations(TranslationBuilder translationBuilder) {
			translationBuilder.add(SIMPLE_BLOCK, "Simple Block");
			translationBuilder.add(new Identifier(MOD_ID, "identifier_test"), "Identifier Test");
			translationBuilder.add(EntityType.ALLAY, "Allay");
			translationBuilder.add(EntityAttributes.GENERIC_ARMOR, "Generic Armor");

			try {
				Optional<Path> path = dataOutput.getModContainer().findPath("assets/testmod/lang/en_us.base.json");

				if (path.isPresent()) {
					translationBuilder.add(path.get());
				} else {
					throw new RuntimeException("The existing language file could not be found in the testmod assets!");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			try {
				translationBuilder.add(EntityType.ALLAY, "Allay Duplicate Test");
			} catch (RuntimeException e) {
				LOGGER.info("Duplicate test passed.");
			}
		}
	}

	private static class JapaneseLangProvider extends FabricLanguageProvider {
		private JapaneseLangProvider(FabricDataOutput output) {
			super(output, "ja_jp");
		}

		@Override
		public void generateTranslations(TranslationBuilder translationBuilder) {
			translationBuilder.add(SIMPLE_BLOCK, "シンプルブロック");
			translationBuilder.add(SIMPLE_ITEM_GROUP, "データ生成項目");
			translationBuilder.add("this.is.a.test", "こんにちは");
		}
	}

	private static class TestModelProvider extends FabricModelProvider {
		private TestModelProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
			blockStateModelGenerator.registerSimpleCubeAll(SIMPLE_BLOCK);
			blockStateModelGenerator.registerSimpleCubeAll(BLOCK_WITHOUT_ITEM);
			blockStateModelGenerator.registerSimpleCubeAll(BLOCK_WITHOUT_LOOT_TABLE);
		}

		@Override
		public void generateItemModels(ItemModelGenerator itemModelGenerator) {
			//itemModelGenerator.register(item, Models.SLAB);
		}
	}

	private static class TestBlockTagProvider extends FabricTagProvider.BlockTagProvider {
		TestBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup registries) {
			getOrCreateTagBuilder(BlockTags.FIRE).add(SIMPLE_BLOCK);
			getOrCreateTagBuilder(BlockTags.ANVIL).setReplace(true).add(SIMPLE_BLOCK);
			getOrCreateTagBuilder(BlockTags.ACACIA_LOGS).forceAddTag(BlockTags.ANIMALS_SPAWNABLE_ON);
		}
	}

	private static class TestItemTagProvider extends FabricTagProvider.ItemTagProvider {
		private TestItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, BlockTagProvider blockTagProvider) {
			super(output, registriesFuture, blockTagProvider);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup registries) {
			copy(BlockTags.ANVIL, ItemTags.ANVIL);
		}
	}

	private static class TestBiomeTagProvider extends FabricTagProvider<Biome> {
		private TestBiomeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, RegistryKeys.BIOME, registriesFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup registries) {
			getOrCreateTagBuilder(TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "biome_tag_test")))
					.add(BiomeKeys.BADLANDS, BiomeKeys.BAMBOO_JUNGLE)
					.add(BiomeKeys.BASALT_DELTAS);
		}
	}

	private static class TestAdvancementProvider extends FabricAdvancementProvider {
		private TestAdvancementProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateAdvancement(Consumer<Advancement> consumer) {
			Advancement root = Advancement.Builder.create()
					.display(
							SIMPLE_BLOCK,
							Text.translatable("advancements.test.root.title"),
							Text.translatable("advancements.test.root.description"),
							new Identifier("textures/gui/advancements/backgrounds/end.png"),
							AdvancementFrame.TASK,
							false, false, false)
					.criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity())
					.build(consumer, MOD_ID + ":test/root");
			Advancement rootNotLoaded = Advancement.Builder.create()
					.display(
							SIMPLE_BLOCK,
							Text.translatable("advancements.test.root_not_loaded.title"),
							Text.translatable("advancements.test.root_not_loaded.description"),
							new Identifier("textures/gui/advancements/backgrounds/end.png"),
							AdvancementFrame.TASK,
							false, false, false)
					.criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity())
					.build(withConditions(consumer, NEVER_LOADED), MOD_ID + ":test/root_not_loaded");
		}
	}

	private static class TestBlockLootTableProvider extends FabricBlockLootTableProvider {
		private TestBlockLootTableProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generate() {
			addDrop(SIMPLE_BLOCK);
			addDrop(BLOCK_WITHOUT_ITEM, drops(SIMPLE_BLOCK));

			excludeFromStrictValidation(BLOCK_WITHOUT_LOOT_TABLE);
		}
	}

	private static class TestBarterLootTableProvider extends SimpleFabricLootTableProvider {
		private TestBarterLootTableProvider(FabricDataOutput output) {
			super(output, LootContextTypes.BARTER);
		}

		@Override
		public void accept(BiConsumer<Identifier, LootTable.Builder> consumer) {
			withConditions(consumer, ALWAYS_LOADED).accept(
					LootTables.PIGLIN_BARTERING_GAMEPLAY,
					LootTable.builder().pool(
							LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(SIMPLE_BLOCK))
					)
			);
		}
	}

	private static class TestSoundProvider extends FabricSoundProvider {
		private TestSoundProvider(FabricDataOutput dataOutput) {
			super(dataOutput);
		}

		@Override
		public void generateSounds(SoundBuilder soundBuilder) {
			soundBuilder.add(SoundEvents.BLOCK_METAL_BREAK, true, new SoundEntry(new Identifier(MOD_ID, "replacement_sound_1")));
			soundBuilder.add(SoundEvents.BLOCK_METAL_BREAK, true, new SoundEntry(new Identifier(MOD_ID, "replacement_sound_2"), 0.5f, 0.5f, 2, false, 16, false, Type.SOUND));
		}
	}
}
