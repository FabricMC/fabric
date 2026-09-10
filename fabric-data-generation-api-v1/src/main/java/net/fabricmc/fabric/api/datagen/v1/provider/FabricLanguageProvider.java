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

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

/**
 * Extend this class and implement {@link FabricLanguageProvider#generateTranslations}.
 * Make sure to use {@link FabricLanguageProvider#FabricLanguageProvider(FabricPackOutput, String, CompletableFuture) FabricLanguageProvider} to declare what language code is being generated if it isn't {@code en_us}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}.
 */
public abstract class FabricLanguageProvider implements DataProvider {
	protected final FabricPackOutput packOutput;
	private final String languageCode;
	private final CompletableFuture<HolderLookup.Provider> registryLookup;

	protected FabricLanguageProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		this(packOutput, "en_us", registryLookup);
	}

	protected FabricLanguageProvider(FabricPackOutput packOutput, String languageCode, CompletableFuture<HolderLookup.Provider> registryLookup) {
		this.packOutput = packOutput;
		this.languageCode = languageCode;
		this.registryLookup = registryLookup;
	}

	/**
	 * Implement this method to register languages.
	 *
	 * <p>Call {@link TranslationBuilder#add(String, String)} to add a translation.
	 */
	public abstract void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder);

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return this.registryLookup.thenCompose(lookup -> {
			TreeMap<String, String> translationEntries = new TreeMap<>();

			generateTranslations(lookup, new TranslationBuilder() {
				@Override
				public boolean has(String translationKey) {
					Objects.requireNonNull(translationKey, "translationKey");
					return translationEntries.containsKey(translationKey);
				}

				@Override
				@Nullable
				public String overwrite(String translationKey, String value) {
					Objects.requireNonNull(translationKey, "translationKey");
					Objects.requireNonNull(value, "value");
					return translationEntries.put(translationKey, value);
				}
			});

			JsonObject langEntryJson = new JsonObject();

			for (Map.Entry<String, String> entry : translationEntries.entrySet()) {
				langEntryJson.addProperty(entry.getKey(), entry.getValue());
			}

			return DataProvider.saveStable(output, langEntryJson, getLangFilePath(this.languageCode));
		});
	}

	/**
	 * Override this method to change where the generated language file is placed.
	 *
	 * @param code The language code (like "en_us") of the translations.
	 */
	protected Path getLangFilePath(String code) {
		return packOutput
				.createPathProvider(PackOutput.Target.RESOURCE_PACK, "lang")
				.json(Identifier.fromNamespaceAndPath(packOutput.getModId(), code));
	}

	@Override
	public String getName() {
		return "Language (%s)".formatted(languageCode);
	}

	/**
	 * A consumer used by {@link FabricLanguageProvider#generateTranslations}.
	 */
	@ApiStatus.NonExtendable
	public interface TranslationBuilder {
		/**
		 * Returns whether this data generator has already added the given translation key.
		 *
		 * @param translationKey The key of the translation.
		 * @return Whether this data generator has already added the key.
		 */
		boolean has(String translationKey);

		/**
		 * Adds a translation, overwriting a translation if it exists.
		 *
		 * @param translationKey The key of the translation.
		 * @param value The value of the entry.
		 * @return The overwritten value, or {@code null} if no value was overwritten.
		 */
		@Nullable
		String overwrite(String translationKey, String value);

		/**
		 * Adds a translation.
		 *
		 * @param translationKey The key of the translation.
		 * @param value          The value of the entry.
		 * @throws IllegalStateException If the translation key already exists.
		 */
		default void add(String translationKey, String value) {
			String overwrittenValue = overwrite(translationKey, value);

			if (overwrittenValue != null) {
				// we overwrote something, restore back to that value and then throw
				overwrite(translationKey, overwrittenValue);
				throw new IllegalStateException("Existing translation key found - " + translationKey + " - Duplicate will be ignored.");
			}
		}

		/**
		 * Adds a translation for an {@link Item}.
		 *
		 * @param item  The {@link Item} to get the translation key from.
		 * @param value The value of the entry.
		 */
		default void add(Item item, String value) {
			add(item.getDescriptionId(), value);
		}

		/**
		 * Adds a translation for a {@link Block}.
		 *
		 * @param block The {@link Block} to get the translation key from.
		 * @param value The value of the entry.
		 */
		default void add(Block block, String value) {
			add(block.getDescriptionId(), value);
		}

		/**
		 * Adds a translation for an {@link CreativeModeTab}.
		 *
		 * @param resourceKey The {@link ResourceKey} to get the translation key from.
		 * @param value The value of the entry.
		 * @deprecated Use {@link #addCreativeModeTab} instead.
		 */
		@Deprecated
		default void add(ResourceKey<CreativeModeTab> resourceKey, String value) {
			addCreativeModeTab(resourceKey, value);
		}

		/**
		 * Adds a translation for an {@link CreativeModeTab}.
		 *
		 * @param resourceKey The {@link ResourceKey} to get the translation key from.
		 * @param value The value of the entry.
		 */
		default void addCreativeModeTab(ResourceKey<CreativeModeTab> resourceKey, String value) {
			final CreativeModeTab group = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(resourceKey);
			final ComponentContents content = group.getDisplayName().getContents();

			if (content instanceof TranslatableContents translatableContent) {
				add(translatableContent.getKey(), value);
				return;
			}

			throw new UnsupportedOperationException("Cannot add language entry for CreativeModeTab (%s) as the display name is not translatable.".formatted(group.getDisplayName().getString()));
		}

		/**
		 * Adds a translation for an {@link EntityType}.
		 *
		 * @param entityType The {@link EntityType} to get the translation key from.
		 * @param value      The value of the entry.
		 */
		default void add(EntityType<?> entityType, String value) {
			add(entityType.getDescriptionId(), value);
		}

		/**
		 * Adds a translation for an {@link Enchantment}.
		 *
		 * @param enchantment The {@link Enchantment} to get the translation key from.
		 * @param value       The value of the entry.
		 */
		default void addEnchantment(ResourceKey<Enchantment> enchantment, String value) {
			add(Util.makeDescriptionId("enchantment", enchantment.identifier()), value);
		}

		/**
		 * Adds a translation for an {@link Attribute}.
		 *
		 * @param attribute The {@link Attribute} to get the translation key from.
		 * @param value     The value of the entry.
		 * @deprecated Use {@link #addAttribute} instead.
		 */
		@Deprecated
		default void add(Holder<Attribute> attribute, String value) {
			addAttribute(attribute, value);
		}

		/**
		 * Adds a translation for an {@link Attribute}.
		 *
		 * @param attribute The {@link Attribute} to get the translation key from.
		 * @param value     The value of the entry.
		 */
		default void addAttribute(Holder<Attribute> attribute, String value) {
			add(attribute.value().getDescriptionId(), value);
		}

		/**
		 * Adds a translation for a {@link StatType}.
		 *
		 * @param statType The {@link StatType} to get the translation key from.
		 * @param value    The value of the entry.
		 */
		default void add(StatType<?> statType, String value) {
			add("stat_type." + BuiltInRegistries.STAT_TYPE.getKey(statType).toString().replace(':', '.'), value);
		}

		/**
		 * Adds a translation for a {@link MobEffect}.
		 *
		 * @param mobEffect The {@link MobEffect} to get the translation key from.
		 * @param value     The value of the entry.
		 */
		default void add(MobEffect mobEffect, String value) {
			add(mobEffect.getDescriptionId(), value);
		}

		/**
		 * Adds a translation for an {@link Identifier}.
		 *
		 * @param identifier The {@link Identifier} to get the translation key from.
		 * @param value      The value of the entry.
		 */
		default void add(Identifier identifier, String value) {
			add(identifier.toLanguageKey(), value);
		}

		/**
		 * Adds a translation for a {@link TagKey}.
		 *
		 * @param tagKey the {@link TagKey} to get the translation key from
		 * @param value  the value of the entry
		 */
		default void add(TagKey<?> tagKey, String value) {
			add(tagKey.getTranslationKey(), value);
		}

		/**
		 * Adds a subtitle translation for a {@link SoundEvent} of the form
		 * {@code subtitles.<namespace>.<path>}. If the sound event uses a non-standard
		 * translation key for its subtitle, use {@link #add(String, String)} instead.
		 *
		 * @param sound The {@link SoundEvent} to get the translation key from
		 * @param value The value of the entry
		 */
		default void add(SoundEvent sound, String value) {
			add(Util.makeDescriptionId("subtitles", sound.location()), value);
		}

		/**
		 * Merges an existing language file into the generated language file.
		 *
		 * @param existingLanguageFile The path to the existing language file.
		 * @throws IOException If loading the language file failed.
		 * @throws IllegalStateException If the translation file contains any translation key that already exists.
		 */
		default void add(Path existingLanguageFile) throws IOException {
			try (Reader reader = Files.newBufferedReader(existingLanguageFile)) {
				JsonObject translations = StrictJsonParser.parse(reader).getAsJsonObject();

				for (String key : translations.keySet()) {
					add(key, translations.get(key).getAsString());
				}
			}
		}

		/**
		 * Merges an existing language file into the generated language file, overwriting existing translations.
		 *
		 * @param existingLanguageFile The path to the existing language file.
		 * @throws IOException If loading the language file failed.
		 */
		default void overwriteWith(Path existingLanguageFile) throws IOException {
			try (Reader reader = Files.newBufferedReader(existingLanguageFile)) {
				JsonObject translations = StrictJsonParser.parse(reader).getAsJsonObject();

				for (String key : translations.keySet()) {
					overwrite(key, translations.get(key).getAsString());
				}
			}
		}
	}
}
