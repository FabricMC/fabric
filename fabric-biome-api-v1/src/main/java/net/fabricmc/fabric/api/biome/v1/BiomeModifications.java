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

import com.google.common.base.Preconditions;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.SpawnSettings;
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
 */
@Deprecated
public final class BiomeModifications {
	/**
	 * Convenience method to add a feature to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addFeature(Predicate<BiomeSelectionContext> biomeSelector, GenerationStep.Feature step, RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey) {
		create(configuredFeatureKey.getValue()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addFeature(step, configuredFeatureKey);
		});
	}

	/**
	 * Convenience method to add a structure to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addStructure(Predicate<BiomeSelectionContext> biomeSelector, RegistryKey<ConfiguredStructureFeature<?, ?>> configuredStructureKey) {
		create(configuredStructureKey.getValue()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addStructure(configuredStructureKey);
		});
	}

	/**
	 * Convenience method to add a carver to one or more biomes.
	 *
	 * @see BiomeSelectors
	 */
	public static void addCarver(Predicate<BiomeSelectionContext> biomeSelector, GenerationStep.Carver step, RegistryKey<ConfiguredCarver<?>> configuredCarverKey) {
		create(configuredCarverKey.getValue()).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getGenerationSettings().addCarver(step, configuredCarverKey);
		});
	}

	/**
	 * Convenience method to add an entity spawn to one or more biomes.
	 *
	 * @see BiomeSelectors
	 * @see net.minecraft.world.biome.SpawnSettings.Builder#spawn(SpawnGroup, SpawnSettings.SpawnEntry)
	 */
	public static void addSpawn(Predicate<BiomeSelectionContext> biomeSelector,
								SpawnGroup spawnGroup, EntityType<?> entityType,
								int weight, int minGroupSize, int maxGroupSize) {
		// See constructor of SpawnSettings.SpawnEntry for context
		Preconditions.checkArgument(entityType.getSpawnGroup() != SpawnGroup.MISC,
				"Cannot add spawns for entities with spawnGroup=MISC since they'd be replaced by pigs.");

		// We need the entity type to be registered or we cannot deduce an ID otherwisea
		Identifier id = Registry.ENTITY_TYPE.getId(entityType);
		Preconditions.checkState(id != Registry.ENTITY_TYPE.getDefaultId(), "Unregistered entity type: %s", entityType);

		create(id).add(ModificationPhase.ADDITIONS, biomeSelector, context -> {
			context.getSpawnSettings().addSpawn(spawnGroup, new SpawnSettings.SpawnEntry(entityType, weight, minGroupSize, maxGroupSize));
		});
	}

	/**
	 * Create a new biome modification which will be applied whenever biomes are loaded from datapacks.
	 *
	 * @param id An identifier for the new set of biome modifications that is returned. Is used for
	 *           guaranteeing consistent ordering between the biome modifications added by different mods
	 *           (assuming they otherwise have the same phase).
	 */
	public static BiomeModification create(Identifier id) {
		return new BiomeModification(id);
	}
}

