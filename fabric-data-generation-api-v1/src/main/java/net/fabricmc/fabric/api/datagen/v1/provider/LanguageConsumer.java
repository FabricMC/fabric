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

/**
 * A consumer used by {@link FabricLanguageProvider#generateLanguages(LanguageConsumer)}.
 */
@FunctionalInterface
public interface LanguageConsumer {
	/**
	 * Adds a language entry.
	 *
	 * @param languageCode The code of the language, eg: en_us or de_de etc.
	 * @param languageKey  The key of the language entry.
	 * @param value        The value of the entry.
	 */
	void addLanguage(String languageCode, String languageKey, String value);
}
