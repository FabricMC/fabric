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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;

/**
 * A consumer used by {@link FabricLanguageProvider#generateLanguages(LanguageConsumer)}.
 */
@FunctionalInterface
public interface LanguageConsumer {
	/**
	 * Adds a language entry.
	 *
	 * @param languageKey  The key of the language entry.
	 * @param value        The value of the entry.
	 */
	void addLanguage(String languageKey, String value);

	/**
	 * Adds a language entry for an {@link Item}.
	 * @param item The {@link Item} to get the language entry key from.
	 * @param value The value of the entry.
	 */
	default void addLanguage(Item item, String value) {
		addLanguage(item.getTranslationKey(), value);
	};

	/**
	 * Adds a language entry for a {@link Block}.
	 * @param block The {@link Block} to get the language entry key from.
	 * @param value The value of the entry.
	 */
	default void addLanguage(Block block, String value) {
		addLanguage(block.getTranslationKey(), value);
	}

	/**
	 * Adds a language entry for an {@link ItemGroup}.
	 * @param group The {@link ItemGroup} to get the language entry key from.
	 * @param value The value of the entry.
	 */
	default void addLanguage(ItemGroup group, String value) {
		addLanguage("itemGroup." + group.getName(), value);
	}

	/**
	 * Adds a language entry for an {@link EntityType}.
	 * @param entityType The {@link EntityType} to get the language entry key from.
	 * @param value The value of the entry.
	 */
	default void addLanguage(EntityType<?> entityType, String value) {
		addLanguage(entityType.getTranslationKey(), value);
	}

	/**
	 * Adds a language entry for an {@link Enchantment}.
	 * @param enchantment The {@link Enchantment} to get the language entry key from.
	 * @param value The value of the entry.
	 */
	default void addLanguage(Enchantment enchantment, String value) {
		addLanguage(enchantment.getTranslationKey(), value);
	}

	/**
	 * Adds a language entry for an {@link EntityAttribute}.
	 * @param entityAttribute The {@link EntityAttribute} to get the language entry key from.
	 * @param value The value of the entry.
	 */
	default void addLanguage(EntityAttribute entityAttribute, String value) {
		addLanguage(entityAttribute.getTranslationKey(), value);
	}

	/**
	 * Adds a language entry for a {@link StatType}.
	 * @param statType The {@link StatType} to get the language entry key from.
	 * @param value The value of the entry.
	 */
	default void addLanguage(StatType<?> statType, String value) {
		addLanguage(statType.getTranslationKey(), value);
	}

	/**
	 * Adds a language entry for a {@link StatusEffect}.
	 * @param statusEffect The {@link StatusEffect} to get the language entry key from.
	 * @param value The value of the entry.
	 */
	default void addLanguage(StatusEffect statusEffect, String value) {
		addLanguage(statusEffect.getTranslationKey(), value);
	}

	/**
	 * Adds a language entry for an {@link Identifier}.
	 * @param identifier The {@link Identifier} to get the language entry key from.
	 * @param value The value of the entry.
	 */
	default void addLanguage(Identifier identifier, String value) {
		addLanguage(identifier.toTranslationKey(), value);
	}

	/**
	 * Merges an existing language file into the data generated language file.
	 * @param existingLanguageFile The path to the existing language file.
	 * @throws IOException if reading the file failed.
	 */
	default void addLanguage(Path existingLanguageFile) throws IOException {
		Gson gson = new Gson();

		JsonObject langEntryJson = gson.fromJson(Files.readString(existingLanguageFile), JsonObject.class);

		for (Map.Entry<String, JsonElement> stringJsonElementEntry : langEntryJson.entrySet()) {
			addLanguage(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
		}
	}
}
