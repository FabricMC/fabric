package net.fabricmc.fabric.api.datagen.v1.provider;

import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
			if(!languageEntries.containsKey(languageCode)) {
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
