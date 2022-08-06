package net.fabricmc.fabric.api.datagen.v1.provider;

/**
 * A consumer used by {@link FabricLanguageProvider#generateLanguages(LanguageConsumer)}
 */
@FunctionalInterface
public interface LanguageConsumer {
	/**
	 * Add a language entry.
	 * @param languageCode The code of the language, eg: en_us or de_de etc.
	 * @param languageKey The key of the language entry.
	 * @param value The value of the entry.
	 */
	void addLanguage(String languageCode, String languageKey, String value);
}
