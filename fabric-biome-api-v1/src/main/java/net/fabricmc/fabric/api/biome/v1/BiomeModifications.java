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

package net.fabricmc.fabric.api.biome.v1;

import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

/**
 * Provides an API to modify Biomes after they have been loaded and before they are used in the World.
 *
 * <p>Any modifications made to biomes will not be available for use in server.properties (as of 1.16.1),
 * or the demo level.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 * Because of the volatility of world generation in Minecraft 1.16, this API is marked experimental
 * since it is likely to change in future Minecraft versions.
 */
@ApiStatus.Experimental
public final class BiomeModifications {
	/**
	 * The suggested order for modifiers that enrich biomes by adding to them without relying on
	 * other information in the biome, or removing other features.
	 *
	 * <p><b>Examples:</b> New ores, new vegetation, new structures
	 */
	public static final int ORDER_ADDITIONS = 1000;

	/**
	 * The suggested order for  modifiers that remove features or other aspects of biomes (i.e. removal of spawns,
	 * removal of features, etc.).
	 *
	 * <p><b>Examples:</b> Remove iron ore from plains, remove ghasts
	 */
	public static final int ORDER_REMOVALS = 1000000;

	/**
	 * The suggested order for modifiers that replace existing features with modified features.
	 *
	 * <p><b>Examples:</b> Replace mineshafts with biome-specific mineshafts
	 */
	public static final int ORDER_REPLACEMENTS = 10000000;

	/**
	 * The suggested order for modifiers that perform wide-reaching biome postprocessing.
	 *
	 * <p><b>Examples:</b> Mods that allow modpack authors to customize world generation
	 */
	public static final int ORDER_POST_PROCESSING = 100000000;

	/**
	 * Add a feature to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addFeature(Predicate<BiomeSelectionContext> biomeSelector, GenerationStep.Feature step, RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey) {
		create(configuredFeatureKey.getValue()).add(ORDER_ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addFeature(step, configuredFeatureKey);
		});
	}

	/**
	 * Add a structure to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addStructure(Predicate<BiomeSelectionContext> biomeSelector, RegistryKey<ConfiguredStructureFeature<?, ?>> configuredStructureKey) {
		create(configuredStructureKey.getValue()).add(ORDER_ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addStructure(configuredStructureKey);
		});
	}

	/**
	 * Add a carver to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addCarver(Predicate<BiomeSelectionContext> biomeSelector, GenerationStep.Carver step, RegistryKey<ConfiguredCarver<?>> configuredCarverKey) {
		create(configuredCarverKey.getValue()).add(ORDER_ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addCarver(step, configuredCarverKey);
		});
	}

	/**
	 * Create a new biome modification which will be applied whenever biomes are loaded from datapacks.
	 *
	 * @param id An identifier for the new set of biome modifications that is returned. Is used for
	 *           guaranteeing consistent ordering between the biome modifications added by different mods
	 *           (assuming they otherwise have the same order).
	 */
	public static BiomeModification create(Identifier id) {
		return new BiomeModification(id);
	}
}

