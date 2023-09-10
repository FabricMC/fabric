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

package net.fabricmc.fabric.impl.tag.convention.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.SharedConstants;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.tag.convention.v1.TagUtil;
import net.fabricmc.loader.api.FabricLoader;

public class ConventionLogWarnings implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ConventionLogWarnings.class);
	/**
	 * A config option mainly for developers.
	 * Logs out modded item tags that do not have translations when running on integrated server.
	 * Defaults to DEV_SHORT.
	 */
	private static final LOG_WARNING_MODES LOG_UNTRANSLATED_WARNING_MODE;
	/**
	 * A config option mainly for developers.
	 * Logs out modded tags that are using the 'c' namespace when running on integrated server.
	 * Defaults to DEV_SHORT.
	 */
	private static final LOG_WARNING_MODES LOG_LEGACY_WARNING_MODE;

	private enum LOG_WARNING_MODES {
		SILENCED,
		DEV_SHORT,
		DEV_VERBOSE,
		PROD_SHORT,
		PROD_VERBOSE
	}

	static {
		File configDir = FabricLoader.getInstance().getConfigDir().resolve("fabric").toFile();

		if (!configDir.exists()) {
			if (!configDir.mkdir()) {
				LOGGER.warn("[Fabric Tag Conventions v1] Could not create configuration directory: " + configDir.getAbsolutePath());
			}
		}

		File configFile = new File(configDir, "fabric-tag-conventions-v1.properties");
		Properties properties = new Properties();

		if (configFile.exists()) {
			try (FileInputStream stream = new FileInputStream(configFile)) {
				properties.load(stream);
			} catch (IOException e) {
				LOGGER.warn("[Fabric Tag Conventions v1] Could not read property file '" + configFile.getAbsolutePath() + "'", e);
			}
		}

		LOG_UNTRANSLATED_WARNING_MODE = asEnum((String) properties.computeIfAbsent("log-untranslated-item-tag-warnings", (a) -> "dev_short"), LOG_WARNING_MODES.DEV_SHORT);
		LOG_LEGACY_WARNING_MODE = asEnum((String) properties.computeIfAbsent("log-legacy-tag-warnings", (a) -> "dev_short"), LOG_WARNING_MODES.DEV_SHORT);

		try (FileOutputStream stream = new FileOutputStream(configFile)) {
			properties.store(stream, "Fabric Tag Conventions v1 properties file");
		} catch (IOException e) {
			LOGGER.warn("[Fabric Tag Conventions v1] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
		}
	}

	@Override
	public void onInitializeClient() {
		if (LOG_UNTRANSLATED_WARNING_MODE != LOG_WARNING_MODES.SILENCED) setupUntranslatedItemTagWarning();
		if (LOG_LEGACY_WARNING_MODE != LOG_WARNING_MODES.SILENCED) setupLegacyTagWarning();
	}

	private static void setupUntranslatedItemTagWarning() {
		// Log missing item tag translations only in development environment and not running dedicated server.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			boolean isConfigSetToDev =
					LOG_UNTRANSLATED_WARNING_MODE == LOG_WARNING_MODES.DEV_SHORT
					|| LOG_UNTRANSLATED_WARNING_MODE == LOG_WARNING_MODES.DEV_VERBOSE;

			if (SharedConstants.isDevelopment == isConfigSetToDev) {
				Registry<Item> itemRegistry = server.getRegistryManager().get(RegistryKeys.ITEM);
				List<TagKey<Item>> untranslatedItemTags = new ObjectArrayList<>();
				itemRegistry.streamTags().forEach(itemTagKey -> {
					// We do not translate vanilla's tags at this moment.
					if (itemTagKey.id().getNamespace().equals("minecraft")) {
						return;
					}

					String translationKey = TagUtil.getTagTranslationKey(itemTagKey);

					if (!I18n.hasTranslation(translationKey)) {
						untranslatedItemTags.add(itemTagKey);
					}
				});

				if (!untranslatedItemTags.isEmpty()) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("""
							\n	Dev warning - Untranslated Item Tags detected. Please translate your item tags so other mods such as recipe viewers can properly display your tag's name.
								You can disable this dev message in Fabric API's properties config file by setting log-untranslated-item-tag-warning to "SILENCED" or see individual tags with "DEV_VERBOSE".
							""");

					// Print out all untranslated tags when desired.
					boolean isConfigSetToVerbose =
							LOG_UNTRANSLATED_WARNING_MODE == LOG_WARNING_MODES.DEV_VERBOSE
							|| LOG_UNTRANSLATED_WARNING_MODE == LOG_WARNING_MODES.PROD_VERBOSE;

					if (isConfigSetToVerbose) {
						stringBuilder.append("\nUntranslated item tags:");

						for (TagKey<Item> tagKey : untranslatedItemTags) {
							stringBuilder.append("\n     ").append(tagKey.id());
						}
					}

					LOGGER.warn(stringBuilder.toString());
				}
			}
		});
	}

	// Remove in 1.22
	private static void setupLegacyTagWarning() {
		// Log tags that are still using legacy 'c' namespace
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			boolean isConfigSetToDev =
					LOG_LEGACY_WARNING_MODE == LOG_WARNING_MODES.DEV_SHORT
					|| LOG_LEGACY_WARNING_MODE == LOG_WARNING_MODES.DEV_VERBOSE;

			if (SharedConstants.isDevelopment == isConfigSetToDev) {
				List<TagKey<?>> legacyTags = new ObjectArrayList<>();
				DynamicRegistryManager.Immutable dynamicRegistries = server.getRegistryManager();

				// We only care about vanilla registries
				dynamicRegistries.streamAllRegistries().forEach(registryEntry -> {
					if (registryEntry.key().getValue().getNamespace().equals("minecraft")) {
						registryEntry.value().streamTags().forEach(tagKey -> {
							// Grab tags under 'forge' namespace
							if (tagKey.id().getNamespace().equals("c")) {
								legacyTags.add(tagKey);
							}
						});
					}
				});

				if (!legacyTags.isEmpty()) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("""
							\n	Dev warning - Legacy Tags detected. Please migrate your 'c' namespace tags to 'common' namespace! See net.minecraftforge.common.Tags.java for all tags.
								NOTE: Many tags have been moved around or renamed. Some new ones were added so please review the new tags. And make sure you follow tag conventions for new tags!
								You can disable this message in Fabric API's properties config file by setting log-legacy-tag-warnings to "SILENCED" or see individual tags with "DEV_VERBOSE".
							""");

					// Print out all legacy tags when desired.
					boolean isConfigSetToVerbose =
							LOG_LEGACY_WARNING_MODE == LOG_WARNING_MODES.DEV_VERBOSE
							|| LOG_LEGACY_WARNING_MODE == LOG_WARNING_MODES.PROD_VERBOSE;

					if (isConfigSetToVerbose) {
						stringBuilder.append("\nLegacy tags:");

						for (TagKey<?> tagKey : legacyTags) {
							stringBuilder.append("\n     ").append(tagKey.id());
						}
					}

					LOGGER.warn(stringBuilder.toString());
				}
			}
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T extends Enum> T asEnum(String property, T defValue) {
		if (property == null || property.isEmpty()) {
			return defValue;
		} else {
			for (Enum obj : defValue.getClass().getEnumConstants()) {
				if (property.equalsIgnoreCase(obj.name())) {
					//noinspection unchecked
					return (T) obj;
				}
			}

			return defValue;
		}
	}
}
