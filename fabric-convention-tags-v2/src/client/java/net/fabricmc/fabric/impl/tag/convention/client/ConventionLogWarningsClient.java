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

import static net.fabricmc.fabric.impl.tag.convention.ConventionLogWarningConfigs.LOG_WARNING_MODES;
import static net.fabricmc.fabric.impl.tag.convention.ConventionLogWarningConfigs.LOG_UNTRANSLATED_WARNING_MODE;

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
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.fabricmc.loader.api.FabricLoader;

public class ConventionLogWarningsClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ConventionLogWarningsClient.class);

	@Override
	public void onInitializeClient() {
		if (LOG_UNTRANSLATED_WARNING_MODE != LOG_WARNING_MODES.SILENCED) setupUntranslatedItemTagWarning();
	}

	private static void setupUntranslatedItemTagWarning() {
		// Log missing item tag translations only in development environment and not running dedicated server.
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			boolean isConfigSetToDev =
					LOG_UNTRANSLATED_WARNING_MODE == LOG_WARNING_MODES.DEV_SHORT
					|| LOG_UNTRANSLATED_WARNING_MODE == LOG_WARNING_MODES.DEV_VERBOSE;

			if (FabricLoader.getInstance().isDevelopmentEnvironment() == isConfigSetToDev) {
				Registry<Item> itemRegistry = server.getRegistryManager().get(RegistryKeys.ITEM);
				List<TagKey<Item>> untranslatedItemTags = new ObjectArrayList<>();
				itemRegistry.streamTags().forEach(itemTagKey -> {
					// We do not translate vanilla's tags at this moment.
					if (itemTagKey.id().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
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
								The format desired is tag.item.<namespace>.<path> for the translation key with slashes in path turned into periods.
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
}
