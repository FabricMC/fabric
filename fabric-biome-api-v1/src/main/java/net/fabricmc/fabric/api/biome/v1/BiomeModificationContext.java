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

import java.util.Optional;
import java.util.function.BiPredicate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;

/**
 * Allows {@link Biome} properties to be modified.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
@Deprecated
public interface BiomeModificationContext {
	/**
	 * @see Biome#getDepth()
	 * @see Biome.Builder#depth(float)
	 */
	void setDepth(float depth);

	/**
	 * @see Biome#getScale()
	 * @see Biome.Builder#scale(float)
	 */
	void setScale(float scale);

	/**
	 * @see Biome#getCategory()
	 * @see Biome.Builder#category(Biome.Category)
	 */
	void setCategory(Biome.Category category);

	/**
	 * Returns the modification context for the biomes weather properties.
	 */
	WeatherContext getWeather();

	/**
	 * Returns the modification context for the biomes effects.
	 */
	EffectsContext getEffects();

	/**
	 * Returns the modification context for the biomes generation settings.
	 */
	GenerationSettingsContext getGenerationSettings();

	/**
	 * Returns the modification context for the biomes spawn settings.
	 */
	SpawnSettingsContext getSpawnSettings();

	interface WeatherContext {
		/**
		 * @see Biome#getPrecipitation()
		 * @see Biome.Builder#precipitation(Biome.Precipitation)
		 */
		void setPrecipitation(Biome.Precipitation precipitation);

		/**
		 * @see Biome#getTemperature()
		 * @see Biome.Builder#temperature(float)
		 */
		void setTemperature(float temperature);

		/**
		 * @see Biome.Builder#temperatureModifier(Biome.TemperatureModifier)
		 */
		void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

		/**
		 * @see Biome#getDownfall()
		 * @see Biome.Builder#downfall(float)
		 */
		void setDownfall(float downfall);
	}

	interface EffectsContext {
		/**
		 * @see BiomeEffects#getFogColor()
		 * @see BiomeEffects.Builder#fogColor(int)
		 */
		void setFogColor(int color);

		/**
		 * @see BiomeEffects#getWaterColor()
		 * @see BiomeEffects.Builder#waterColor(int)
		 */
		void setWaterColor(int color);

		/**
		 * @see BiomeEffects#getWaterFogColor()
		 * @see BiomeEffects.Builder#waterFogColor(int)
		 */
		void setWaterFogColor(int color);

		/**
		 * @see BiomeEffects#getSkyColor()
		 * @see BiomeEffects.Builder#skyColor(int)
		 */
		void setSkyColor(int color);

		/**
		 * @see BiomeEffects#getFoliageColor()
		 * @see BiomeEffects.Builder#foliageColor(int)
		 */
		void setFoliageColor(Optional<Integer> color);

		/**
		 * @see BiomeEffects#getFoliageColor()
		 * @see BiomeEffects.Builder#foliageColor(int)
		 */
		default void setFoliageColor(int color) {
			setFoliageColor(Optional.of(color));
		}

		/**
		 * @see BiomeEffects#getFoliageColor()
		 * @see BiomeEffects.Builder#foliageColor(int)
		 */
		default void clearFoliageColor() {
			setFoliageColor(Optional.empty());
		}

		/**
		 * @see BiomeEffects#getGrassColor()
		 * @see BiomeEffects.Builder#grassColor(int)
		 */
		void setGrassColor(Optional<Integer> color);

		/**
		 * @see BiomeEffects#getGrassColor()
		 * @see BiomeEffects.Builder#grassColor(int)
		 */
		default void setGrassColor(int color) {
			setGrassColor(Optional.of(color));
		}

		/**
		 * @see BiomeEffects#getGrassColor()
		 * @see BiomeEffects.Builder#grassColor(int)
		 */
		default void clearGrassColor() {
			setGrassColor(Optional.empty());
		}

		/**
		 * @see BiomeEffects#getGrassColorModifier()
		 * @see BiomeEffects.Builder#grassColorModifier(BiomeEffects.GrassColorModifier)
		 */
		void setGrassColorModifier(@NotNull BiomeEffects.GrassColorModifier colorModifier);

		/**
		 * @see BiomeEffects#getParticleConfig()
		 * @see BiomeEffects.Builder#particleConfig(BiomeParticleConfig)
		 */
		void setParticleConfig(Optional<BiomeParticleConfig> particleConfig);

		/**
		 * @see BiomeEffects#getParticleConfig()
		 * @see BiomeEffects.Builder#particleConfig(BiomeParticleConfig)
		 */
		default void setParticleConfig(@NotNull BiomeParticleConfig particleConfig) {
			setParticleConfig(Optional.of(particleConfig));
		}

		/**
		 * @see BiomeEffects#getParticleConfig()
		 * @see BiomeEffects.Builder#particleConfig(BiomeParticleConfig)
		 */
		default void clearParticleConfig() {
			setParticleConfig(Optional.empty());
		}

		/**
		 * @see BiomeEffects#getLoopSound()
		 * @see BiomeEffects.Builder#loopSound(SoundEvent)
		 */
		void setAmbientSound(Optional<SoundEvent> sound);

		/**
		 * @see BiomeEffects#getLoopSound()
		 * @see BiomeEffects.Builder#loopSound(SoundEvent)
		 */
		default void setAmbientSound(@NotNull SoundEvent sound) {
			setAmbientSound(Optional.of(sound));
		}

		/**
		 * @see BiomeEffects#getLoopSound()
		 * @see BiomeEffects.Builder#loopSound(SoundEvent)
		 */
		default void clearAmbientSound() {
			setAmbientSound(Optional.empty());
		}

		/**
		 * @see BiomeEffects#getMoodSound()
		 * @see BiomeEffects.Builder#moodSound(BiomeMoodSound)
		 */
		void setMoodSound(Optional<BiomeMoodSound> sound);

		/**
		 * @see BiomeEffects#getMoodSound()
		 * @see BiomeEffects.Builder#moodSound(BiomeMoodSound)
		 */
		default void setMoodSound(@NotNull BiomeMoodSound sound) {
			setMoodSound(Optional.of(sound));
		}

		/**
		 * @see BiomeEffects#getMoodSound()
		 * @see BiomeEffects.Builder#moodSound(BiomeMoodSound)
		 */
		default void clearMoodSound() {
			setMoodSound(Optional.empty());
		}

		/**
		 * @see BiomeEffects#getAdditionsSound()
		 * @see BiomeEffects.Builder#additionsSound(BiomeAdditionsSound)
		 */
		void setAdditionsSound(Optional<BiomeAdditionsSound> sound);

		/**
		 * @see BiomeEffects#getAdditionsSound()
		 * @see BiomeEffects.Builder#additionsSound(BiomeAdditionsSound)
		 */
		default void setAdditionsSound(@NotNull BiomeAdditionsSound sound) {
			setAdditionsSound(Optional.of(sound));
		}

		/**
		 * @see BiomeEffects#getAdditionsSound()
		 * @see BiomeEffects.Builder#additionsSound(BiomeAdditionsSound)
		 */
		default void clearAdditionsSound() {
			setAdditionsSound(Optional.empty());
		}

		/**
		 * @see BiomeEffects#getMusic()
		 * @see BiomeEffects.Builder#music(MusicSound)
		 */
		void setMusic(Optional<MusicSound> sound);

		/**
		 * @see BiomeEffects#getMusic()
		 * @see BiomeEffects.Builder#music(MusicSound)
		 */
		default void setMusic(@NotNull MusicSound sound) {
			setMusic(Optional.of(sound));
		}

		/**
		 * @see BiomeEffects#getMusic()
		 * @see BiomeEffects.Builder#music(MusicSound)
		 */
		default void clearMusic() {
			setMusic(Optional.empty());
		}
	}

	interface GenerationSettingsContext {
		/**
		 * Sets the biomes surface builder to a surface builder registered in {@link BuiltinRegistries#CONFIGURED_SURFACE_BUILDER}.
		 *
		 * <p>This method is intended for use with the surface builders found in {@link ConfiguredSurfaceBuilders}.
		 *
		 * <p><b>NOTE:</b> In case the configured surface builder is overridden in a datapack, the datapacks version
		 * will be used.
		 */
		default void setBuiltInSurfaceBuilder(ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder) {
			setSurfaceBuilder(BuiltInRegistryKeys.get(configuredSurfaceBuilder));
		}

		/**
		 * Sets the biomes surface builder to the surface builder identified by the given key.
		 */
		void setSurfaceBuilder(RegistryKey<ConfiguredSurfaceBuilder<?>> surfaceBuilderKey);

		/**
		 * Removes a feature from one of this biomes generation steps, and returns if any features were removed.
		 */
		boolean removeFeature(GenerationStep.Feature step, RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey);

		/**
		 * Removes a feature from all of this biomes generation steps, and returns if any features were removed.
		 */
		default boolean removeFeature(RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey) {
			boolean anyFound = false;

			for (GenerationStep.Feature step : GenerationStep.Feature.values()) {
				if (removeFeature(step, configuredFeatureKey)) {
					anyFound = true;
				}
			}

			return anyFound;
		}

		/**
		 * {@link #removeFeature(RegistryKey)} for built-in features (see {@link #addBuiltInFeature(GenerationStep.Feature, ConfiguredFeature)}).
		 */
		default boolean removeBuiltInFeature(ConfiguredFeature<?, ?> configuredFeature) {
			return removeFeature(BuiltInRegistryKeys.get(configuredFeature));
		}

		/**
		 * {@link #removeFeature(GenerationStep.Feature, RegistryKey)} for built-in features (see {@link #addBuiltInFeature(GenerationStep.Feature, ConfiguredFeature)}).
		 */
		default boolean removeBuiltInFeature(GenerationStep.Feature step, ConfiguredFeature<?, ?> configuredFeature) {
			return removeFeature(step, BuiltInRegistryKeys.get(configuredFeature));
		}

		/**
		 * Adds a feature to one of this biomes generation steps, identified by the configured feature's registry key.
		 */
		void addFeature(GenerationStep.Feature step, RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey);

		/**
		 * Adds a configured feature from {@link BuiltinRegistries#CONFIGURED_FEATURE} to this biome.
		 *
		 * <p>This method is intended for use with the configured features found in {@link net.minecraft.world.gen.feature.ConfiguredFeatures}.
		 *
		 * <p><b>NOTE:</b> In case the configured feature is overridden in a datapack, the datapacks version
		 * will be used.
		 */
		default void addBuiltInFeature(GenerationStep.Feature step, ConfiguredFeature<?, ?> configuredFeature) {
			addFeature(step, BuiltInRegistryKeys.get(configuredFeature));
		}

		/**
		 * Adds a configured carver to one of this biomes generation steps.
		 */
		void addCarver(GenerationStep.Carver step, RegistryKey<ConfiguredCarver<?>> carverKey);

		/**
		 * Adds a configured carver from {@link BuiltinRegistries#CONFIGURED_CARVER} to this biome.
		 *
		 * <p>This method is intended for use with the configured carvers found in {@link net.minecraft.world.gen.carver.ConfiguredCarvers}.
		 *
		 * <p><b>NOTE:</b> In case the configured carver is overridden in a datapack, the datapacks version
		 * will be used.
		 */
		default void addBuiltInCarver(GenerationStep.Carver step, ConfiguredCarver<?> configuredCarver) {
			addCarver(step, BuiltInRegistryKeys.get(configuredCarver));
		}

		/**
		 * Removes all carvers with the given key from one of this biomes generation steps.
		 *
		 * @return True if any carvers were removed.
		 */
		boolean removeCarver(GenerationStep.Carver step, RegistryKey<ConfiguredCarver<?>> configuredCarverKey);

		/**
		 * Removes all carvers with the given key from all of this biomes generation steps.
		 *
		 * @return True if any carvers were removed.
		 */
		default boolean removeCarver(RegistryKey<ConfiguredCarver<?>> configuredCarverKey) {
			boolean anyFound = false;

			for (GenerationStep.Carver step : GenerationStep.Carver.values()) {
				if (removeCarver(step, configuredCarverKey)) {
					anyFound = true;
				}
			}

			return anyFound;
		}

		/**
		 * {@link #removeCarver(RegistryKey)} for built-in carvers (see {@link #addBuiltInCarver(GenerationStep.Carver, ConfiguredCarver)}).
		 */
		default boolean removeBuiltInCarver(ConfiguredCarver<?> configuredCarver) {
			return removeCarver(BuiltInRegistryKeys.get(configuredCarver));
		}

		/**
		 * {@link #removeCarver(GenerationStep.Carver, RegistryKey)} for built-in carvers (see {@link #addBuiltInCarver(GenerationStep.Carver, ConfiguredCarver)}).
		 */
		default boolean removeBuiltInCarver(GenerationStep.Carver step, ConfiguredCarver<?> configuredCarver) {
			return removeCarver(step, BuiltInRegistryKeys.get(configuredCarver));
		}

		/**
		 * Allows a configured structure to start in this biome.
		 *
		 * <p>Structures added this way may start in this biome, with respect to the {@link net.minecraft.world.gen.chunk.StructureConfig}
		 * set in the {@link net.minecraft.world.gen.chunk.ChunkGenerator}, but structure pieces can always generate in chunks adjacent
		 * to a started structure, regardless of biome.
		 *
		 * <p>Configured structures that have the same underlying {@link StructureFeature} as the given structure will be removed before
		 * adding the new structure, since only one of them could actually generate.
		 *
		 * @see net.minecraft.world.biome.GenerationSettings.Builder#structureFeature(ConfiguredStructureFeature)
		 */
		void addStructure(RegistryKey<ConfiguredStructureFeature<?, ?>> configuredStructureKey);

		/**
		 * Allows a configured structure from {@link BuiltinRegistries#CONFIGURED_STRUCTURE_FEATURE} to start in this biome.
		 *
		 * <p>This method is intended for use with the configured structures found in {@link net.minecraft.world.gen.feature.ConfiguredStructureFeatures}.
		 *
		 * <p><b>NOTE:</b> In case the configured structure is overridden using a datapack, the definition from the datapack
		 * will be added to the biome.
		 *
		 * <p>Structures added this way may start in this biome, with respect to the {@link net.minecraft.world.gen.chunk.StructureConfig}
		 * set in the {@link net.minecraft.world.gen.chunk.ChunkGenerator}, but structure pieces can always generate in chunks adjacent
		 * to a started structure, regardless of biome.
		 *
		 * <p>Configured structures that have the same underlying {@link StructureFeature} as the given structure will be removed before
		 * adding the new structure, since only one of them could actually generate.
		 */
		default void addBuiltInStructure(ConfiguredStructureFeature<?, ?> configuredStructure) {
			addStructure(BuiltInRegistryKeys.get(configuredStructure));
		}

		/**
		 * Removes a configured structure from the structures that are allowed to start in this biome.
		 *
		 * <p>Please see the note on {@link #addStructure(RegistryKey)} about structures pieces still generating
		 * if adjacent biomes allow the structure to start.
		 *
		 * @return True if any structures were removed.
		 */
		boolean removeStructure(RegistryKey<ConfiguredStructureFeature<?, ?>> configuredStructureKey);

		/**
		 * Removes a structure from the structures that are allowed to start in this biome.
		 *
		 * <p>This will remove all configured variations of the given structure from this biome.
		 *
		 * <p>This can be used with modded structures or Vanilla structures from {@link net.minecraft.world.gen.feature.StructureFeature}.
		 *
		 * @return True if any structures were removed.
		 */
		boolean removeStructure(StructureFeature<?> structure);

		/**
		 * {@link #removeStructure(RegistryKey)} for built-in structures (see {@link #addBuiltInStructure(ConfiguredStructureFeature)}).
		 */
		default boolean removeBuiltInStructure(ConfiguredStructureFeature<?, ?> configuredStructure) {
			return removeStructure(BuiltInRegistryKeys.get(configuredStructure));
		}
	}

	interface SpawnSettingsContext {
		/**
		 * Associated JSON property: <code>player_spawn_friendly</code>.
		 *
		 * @see SpawnSettings#isPlayerSpawnFriendly()
		 * @see SpawnSettings.Builder#playerSpawnFriendly()
		 */
		void setPlayerSpawnFriendly(boolean playerSpawnFriendly);

		/**
		 * Associated JSON property: <code>creature_spawn_probability</code>.
		 *
		 * @see SpawnSettings#getCreatureSpawnProbability()
		 * @see SpawnSettings.Builder#creatureSpawnProbability(float)
		 */
		void setCreatureSpawnProbability(float probability);

		/**
		 * Associated JSON property: <code>spawners</code>.
		 *
		 * @see SpawnSettings#getSpawnEntry(SpawnGroup)
		 * @see SpawnSettings.Builder#spawn(SpawnGroup, SpawnSettings.SpawnEntry)
		 */
		void addSpawn(SpawnGroup spawnGroup, SpawnSettings.SpawnEntry spawnEntry);

		/**
		 * Removes any spawns matching the given predicate from this biome, and returns true if any matched.
		 *
		 * <p>Associated JSON property: <code>spawners</code>.
		 */
		boolean removeSpawns(BiPredicate<SpawnGroup, SpawnSettings.SpawnEntry> predicate);

		/**
		 * Removes all spawns of the given entity type.
		 *
		 * <p>Associated JSON property: <code>spawners</code>.
		 *
		 * @return True if any spawns were removed.
		 */
		default boolean removeSpawnsOfEntityType(EntityType<?> entityType) {
			return removeSpawns((spawnGroup, spawnEntry) -> spawnEntry.type == entityType);
		}

		/**
		 * Removes all spawns of the given entity type.
		 *
		 * <p>Associated JSON property: <code>spawners</code>.
		 */
		default void clearSpawns(SpawnGroup group) {
			removeSpawns((spawnGroup, spawnEntry) -> spawnGroup == group);
		}

		/**
		 * Removes all spawns.
		 *
		 * <p>Associated JSON property: <code>spawners</code>.
		 */
		default void clearSpawns() {
			removeSpawns((spawnGroup, spawnEntry) -> true);
		}

		/**
		 * Associated JSON property: <code>spawn_costs</code>.
		 *
		 * @see SpawnSettings#getSpawnDensity(EntityType)
		 * @see SpawnSettings.Builder#spawnCost(EntityType, double, double)
		 */
		void setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit);

		/**
		 * Removes a spawn cost entry for a given entity type.
		 *
		 * <p>Associated JSON property: <code>spawn_costs</code>.
		 */
		void clearSpawnCost(EntityType<?> entityType);
	}
}
