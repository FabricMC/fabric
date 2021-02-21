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

package net.fabricmc.fabric.impl.config.lang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Language {
	private static final Logger LOGGER = LogManager.getLogger("Fabric|Language");

	private final String langCode;
	private final Map<String, String> byTranslationKey = new HashMap<>();
	private final Map<String, Map<String, String>> byModId = new HashMap<>();
	private final Multimap<String, String> comments = LinkedHashMultimap.create();

	public Language(String langCode) {
		this.langCode = langCode;
	}

	public void add(String mod, Path languageFile) throws IOException {
		JsonElement json = new JsonParser().parse(Files.newBufferedReader(languageFile));

		if (!json.isJsonObject()) {
			LOGGER.error("Error parsing language file \"{}\": not a JSON object.", languageFile.toString());
			return;
		}

		for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isString()) {
				LOGGER.error("Error parsing language file \"{}\": non-string value \"{}\".", languageFile.toString(), entry.getKey());
				continue;
			}

			String key = entry.getKey();
			String translation = entry.getValue().getAsString();
			this.byTranslationKey.put(key, translation);
			this.byModId.computeIfAbsent(mod, id -> new HashMap<>()).put(key, translation);

			if (key.matches(".*\\.comment.*")) {
				key = key.split("\\.comment", 2)[0];
				comments.put(key, translation);
			}
		}
	}

	public @Nullable String translate(@NotNull String key) {
		return this.byTranslationKey.get(key);
	}

	public @NotNull Collection<String> getComments(@NotNull String key) {
		return this.comments.get(key);
	}
}
