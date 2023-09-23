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

import static net.fabricmc.fabric.impl.tag.convention.ConventionLogWarningConfigs.LOG_LEGACY_WARNING_MODE;
import static net.fabricmc.fabric.impl.tag.convention.ConventionLogWarningConfigs.LOG_WARNING_MODES;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

// To be removed in 1.22 Minecraft
public class ConventionLogWarnings implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ConventionLogWarnings.class);
	private static final Set<String> LEGACY_C_TAGS = Set.of(
			"movement_restricted",
			"quartz_ores",
			"wooden_barrels",
			"sandstone_blocks",
			"sandstone_slabs",
			"sandstone_stairs",
			"red_sandstone_blocks",
			"red_sandstone_slabs",
			"red_sandstone_stairs",
			"uncolored_sandstone_blocks",
			"uncolored_sandstone_slabs",
			"uncolored_sandstone_stairs",

			"black_dyes",
			"blue_dyes",
			"brown_dyes",
			"green_dyes",
			"red_dyes",
			"white_dyes",
			"yellow_dyes",
			"light_blue_dyes",
			"light_gray_dyes",
			"lime_dyes",
			"magenta_dyes",
			"orange_dyes",
			"pink_dyes",
			"cyan_dyes",
			"gray_dyes",
			"purple_dyes",
			"raw_iron_ores",
			"raw_copper_ores",
			"raw_gold_ores",
			"diamonds",
			"lapis",
			"emeralds",
			"quartz",
			"shears",
			"spears",
			"bows",
			"shields",

			"in_nether",
			"in_the_end",
			"in_the_overworld",
			"caves",
			"climate_cold",
			"climate_temperate",
			"climate_hot",
			"climate_wet",
			"climate_dry",
			"vegetation_dense",
			"vegetation_sparse",
			"tree_coniferous",
			"tree_deciduous",
			"tree_jungle",
			"tree_savanna",
			"mountain_peak",
			"mountain_slope",
			"end_islands",
			"nether_forests",
			"flower_forests"
	);

	@Override
	public void onInitialize() {
		if (LOG_LEGACY_WARNING_MODE != LOG_WARNING_MODES.SILENCED) setupLegacyTagWarning();
	}

	// Remove in 1.22
	private static void setupLegacyTagWarning() {
		// Log tags that are still using legacy conventions under 'c' namespace
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			boolean isConfigSetToDev =
					LOG_LEGACY_WARNING_MODE == LOG_WARNING_MODES.DEV_SHORT
					|| LOG_LEGACY_WARNING_MODE == LOG_WARNING_MODES.DEV_VERBOSE;

			if (FabricLoader.getInstance().isDevelopmentEnvironment() == isConfigSetToDev) {
				List<TagKey<?>> legacyTags = new ObjectArrayList<>();
				DynamicRegistryManager.Immutable dynamicRegistries = server.getRegistryManager();

				// We only care about vanilla registries
				dynamicRegistries.streamAllRegistries().forEach(registryEntry -> {
					if (registryEntry.key().getValue().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
						registryEntry.value().streamTags().forEach(tagKey -> {
							// Grab legacy tags under 'c' namespace
							if (tagKey.id().getNamespace().equals("c") && LEGACY_C_TAGS.contains(tagKey.id().getPath())) {
								legacyTags.add(tagKey);
							}
						});
					}
				});

				if (!legacyTags.isEmpty()) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("""
							\n	Dev warning - Legacy Tags detected. Please migrate your old `c` tags to our new `c` tags that follows better conventions! See classes under net.fabricmc.fabric.api.tag.convention.v1 package for all tags.
								NOTE: Many tags have been moved around or renamed. Some new ones were added so please review the new tags.
								And make sure you follow tag conventions for new tags! The convention is `c` with nouns generally being plural and adjectives being singular.
								You can disable this message in Fabric API's properties config file by setting log-legacy-tag-warnings to "SILENCED" or see individual tags with "DEV_VERBOSE".
							""");

					// Print out all legacy tags when desired.
					boolean isConfigSetToVerbose =
							LOG_LEGACY_WARNING_MODE == LOG_WARNING_MODES.DEV_VERBOSE
							|| LOG_LEGACY_WARNING_MODE == LOG_WARNING_MODES.PROD_VERBOSE;

					if (isConfigSetToVerbose) {
						stringBuilder.append("\nLegacy tags:");

						for (TagKey<?> tagKey : legacyTags) {
							stringBuilder.append("\n     ").append(tagKey);
						}
					}

					LOGGER.warn(stringBuilder.toString());
				}
			}
		});
	}
}
