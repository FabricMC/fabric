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
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonObject;

import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Extend this class and implement {@link FabricLanguageProvider#generateLanguages(LanguageConsumer)}.
 * Make sure to use {@link FabricLanguageProvider#FabricLanguageProvider(FabricDataGenerator, String)} FabricLanguageProvider} to declare what language code is being generated if it isn't {@code en_us}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}
 */
public abstract class FabricLanguageProvider implements DataProvider {
	protected final FabricDataGenerator dataGenerator;
	private final String languageCode;

	protected FabricLanguageProvider(FabricDataGenerator dataGenerator) {
		this(dataGenerator, "en_us");
	}

	protected FabricLanguageProvider(FabricDataGenerator dataGenerator, String languageCode) {
		this.dataGenerator = dataGenerator;
		this.languageCode = languageCode;
	}

	/**
	 * Implement this method to register languages.
	 *
	 * <p>Call {@link LanguageConsumer#addLanguage(String, String)} to add a language entry.
	 */
	public abstract void generateLanguages(LanguageConsumer languageConsumer);

	@Override
	public void run(DataWriter writer) throws IOException {
		TreeMap<String, String> languageEntries = new TreeMap<>();

		generateLanguages(languageEntries::put);

		JsonObject langEntryJson = new JsonObject();

		for (Map.Entry<String, String> entry : languageEntries.entrySet()) {
			langEntryJson.addProperty(entry.getKey(), entry.getValue());
		}

		DataProvider.writeToPath(writer, langEntryJson, getLangFilePath(this.languageCode));
	}

	private Path getLangFilePath(String code) {
		return dataGenerator.getOutput().resolve("assets/%s/lang/%s.json".formatted(dataGenerator.getModId(), code));
	}

	@Override
	public String getName() {
		return "Languages";
	}
}
