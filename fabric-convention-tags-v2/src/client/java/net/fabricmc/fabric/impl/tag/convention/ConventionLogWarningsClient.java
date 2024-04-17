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

package net.fabricmc.fabric.impl.tag.convention;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

public class ConventionLogWarningsClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ConventionLogWarningsClient.class);

	/**
	 * A config option mainly for developers.
	 * Logs out modded item tags that do not have translations when running on integrated server.
	 * Defaults to SILENCED.
	 */
	private static final String LOG_UNTRANSLATED_WARNING_MODE = System.getProperty("fabric-tag-conventions-v2.missingTagTranslationWarning", LOG_WARNING_MODES.SILENCED.name());
	private enum LOG_WARNING_MODES {
		SILENCED,
		DEV_SHORT,
		DEV_VERBOSE
	}

	@Override
	public void onInitializeClient() {
		if (!LOG_UNTRANSLATED_WARNING_MODE.equalsIgnoreCase(LOG_WARNING_MODES.SILENCED.name())) {
			setupUntranslatedItemTagWarning();
		}
	}

	private static void setupUntranslatedItemTagWarning() {
		// Log missing item tag translations only in development environment and not running dedicated server.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			boolean isConfigSetToDev =
					LOG_UNTRANSLATED_WARNING_MODE.equalsIgnoreCase(LOG_WARNING_MODES.DEV_SHORT.name())
							|| LOG_UNTRANSLATED_WARNING_MODE.equalsIgnoreCase(LOG_WARNING_MODES.DEV_VERBOSE.name());

			if (FabricLoader.getInstance().isDevelopmentEnvironment() == isConfigSetToDev) {
				Registry<Item> itemRegistry = server.getRegistryManager().get(RegistryKeys.ITEM);
				List<TagKey<Item>> untranslatedItemTags = new ObjectArrayList<>();
				itemRegistry.streamTags().forEach(itemTagKey -> {
					// We do not translate vanilla's tags at this moment.
					if (itemTagKey.id().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
						return;
					}

					String translationKey = itemTagKey.getTagTranslationKey();

					if (!I18n.hasTranslation(translationKey)) {
						untranslatedItemTags.add(itemTagKey);
					}
				});

				if (!untranslatedItemTags.isEmpty()) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("""
							\n	Dev warning - Untranslated Item Tags detected. Please translate your item tags so other mods such as recipe viewers can properly display your tag's name.
								The format desired is tag.item.<namespace>.<path> for the translation key with slashes in path turned into periods.
								To be warned when there is any untranslated item tag, set this system property in your runs: `-Dfabric-tag-conventions-v2.missingTagTranslationWarning=DEV_SHORT`.
								To see individual legacy tags found, set the system property to `-Dfabric-tag-conventions-v2.missingTagTranslationWarning=DEV_VERBOSE`.
								Default is `SILENCED`.
							""");

					// Print out all untranslated tags when desired.
					boolean isConfigSetToVerbose = LOG_UNTRANSLATED_WARNING_MODE.equalsIgnoreCase(LOG_WARNING_MODES.DEV_VERBOSE.name());

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
}
