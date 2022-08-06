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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

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
}
