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
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.fabricmc.loader.api.FabricLoader;

// To be removed in 1.22 Minecraft
public class ConventionLogWarnings implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ConventionLogWarnings.class);

	// Old `c` tags that we migrated to a new tag under a new convention.
	// May also contain commonly used `c` tags that are not following convention.
	private static final Set<TagKey<?>> LEGACY_C_TAGS = Set.<TagKey<?>>of(
			// Old v1 tags that are discouraged
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "movement_restricted")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "quartz_ores")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "wooden_barrels")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "sandstone_blocks")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "sandstone_stairs")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "sandstone_slabs")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "red_sandstone_blocks")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "red_sandstone_stairs")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "red_sandstone_slabs")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "uncolored_sandstone_blocks")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "uncolored_sandstone_stairs")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "uncolored_sandstone_slabs")),

			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "black_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "blue_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "brown_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "green_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "red_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "white_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "yellow_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "light_blue_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "light_gray_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "lime_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "magenta_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "orange_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "pink_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "cyan_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "gray_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "purple_dyes")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "raw_iron_ores")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "raw_gold_ores")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "diamonds")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "lapis")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "emeralds")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "quartz")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "shears")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "spears")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "bows")),
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "shields")),

			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "in_nether")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "in_the_end")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "in_overworld")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "caves")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "climate_cold")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "climate_temperate")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "climate_hot")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "climate_wet")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "climate_dry")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "vegetation_dense")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "vegetation_sparse")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "tree_coniferous")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "tree_deciduous")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "tree_jungle")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "tree_savanna")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "mountain_peak")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "mountain_slope")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "end_islands")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "nether_forests")),
			TagKey.of(RegistryKeys.BIOME, new Identifier(TagUtil.C_TAG_NAMESPACE, "flower_forests")),

			// Commonly used `c` tags that are using discouraged conventions. (Not plural or not folder form)
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "barrel")), // Should be using barrels
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "chest")), // Should be using chests
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "glass")), // Should be using glass_blocks
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "glass_pane")), // Should be using glass_blocks
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "immobile")), // Should be using relocation_not_supported
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "wooden_chests")), // Should be using chests/wooden
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "workbench")),
			TagKey.of(RegistryKeys.BLOCK, new Identifier(TagUtil.C_TAG_NAMESPACE, "stone")), // Should be using stones

			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "axes")), // Should be using vanilla's equivalent tag
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "pickaxes")), // Should be using vanilla's equivalent tag
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "hoes")), // Should be using vanilla's equivalent tag
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "shovels")), // Should be using vanilla's equivalent tag
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "swords")), // Should be using vanilla's equivalent tag
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "barrel")), // Should be using barrels
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "chest")), // Should be using chests
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "glass")), // Should be using glass_blocks
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "glass_pane")), // Should be using glass_blocks
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "glowstone_dusts")), // Should be using dusts/glowstone
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "redstone_dusts")), // Should be using dusts/redstone
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "stone")), // Should be using stones
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "string")), // Should be using strings
			TagKey.of(RegistryKeys.ITEM, new Identifier(TagUtil.C_TAG_NAMESPACE, "wooden_rods")) // Should be using rods/wooden
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
							// Grab legacy tags we migrated or discourage
							if (LEGACY_C_TAGS.contains(tagKey)) {
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
