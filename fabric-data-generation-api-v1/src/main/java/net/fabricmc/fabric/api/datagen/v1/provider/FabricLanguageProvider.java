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
import java.nio.file.Path;
import java.util.HashMap;

import com.google.gson.JsonObject;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Extend this class and implement {@link FabricLanguageProvider#generateLanguages(LanguageConsumer)}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}
 */
public abstract class FabricLanguageProvider implements DataProvider {
	protected final FabricDataGenerator dataGenerator;
	private final DataGenerator.PathResolver pathResolver;

	protected FabricLanguageProvider(FabricDataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
		this.pathResolver = dataGenerator.createPathResolver(DataGenerator.OutputType.RESOURCE_PACK, "lang");
	}

	/**
	 * Implement this method to register languages.
	 *
	 * <p>Call {@link LanguageConsumer#addLanguage(String, String, String)} to add a language entry.
	 */
	public abstract void generateLanguages(LanguageConsumer languageConsumer);

	@Override
	public void run(DataWriter writer) throws IOException {
		HashMap<String, HashMap<String, String>> languageEntries = new HashMap<>();

		generateLanguages((languageCode, languageKey, value) -> {
			if (!languageEntries.containsKey(languageCode)) {
				HashMap<String, String> languageKeyValues = new HashMap<>();
				languageKeyValues.put(languageKey, value);
				languageEntries.put(languageCode, languageKeyValues);
			} else {
				languageEntries.get(languageCode).put(languageKey, value);
			}
		});

		for (String langCode : languageEntries.keySet()) {
			HashMap<String, String> entries = languageEntries.get(langCode);
			JsonObject langEntryJson = new JsonObject();

			entries.forEach(langEntryJson::addProperty);

			DataProvider.writeToPath(writer, langEntryJson, getLangFilePath(langCode));
		}
	}

	private Path getLangFilePath(String code) {
		// Note, namespace must be [mod id]-lang to prevent conflicts with pre-existing language files.
		return dataGenerator.getOutput().resolve("assets/%s/lang/%s.json".formatted(dataGenerator.getModId() + "-lang", code));
	}

	@Override
	public String getName() {
		return "Languages";
	}
}
