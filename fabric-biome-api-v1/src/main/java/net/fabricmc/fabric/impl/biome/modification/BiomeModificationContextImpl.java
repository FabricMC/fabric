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

package net.fabricmc.fabric.impl.biome.modification;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.mixin.biome.modification.BiomeAccessor;
import net.fabricmc.fabric.mixin.biome.modification.BiomeEffectsAccessor;
import net.fabricmc.fabric.mixin.biome.modification.BiomeWeatherAccessor;
import net.fabricmc.fabric.mixin.biome.modification.GenerationSettingsAccessor;
import net.fabricmc.fabric.mixin.biome.modification.SpawnDensityAccessor;
import net.fabricmc.fabric.mixin.biome.modification.SpawnSettingsAccessor;

@ApiStatus.Internal
public class BiomeModificationContextImpl implements BiomeModificationContext {
	private final DynamicRegistryManager registries;
	private final Biome biome;
	private final BiomeAccessor biomeAccessor;
	private final WeatherContext weather;
	private final EffectsContext effects;
	private final GenerationSettingsContextImpl generationSettings;
	private final SpawnSettingsContextImpl spawnSettings;

	@SuppressWarnings("ConstantConditions")
	public BiomeModificationContextImpl(DynamicRegistryManager registries, Biome biome) {
		this.registries = registries;
		this.biome = biome;
		this.biomeAccessor = (BiomeAccessor) (Object) biome;
		this.weather = new WeatherContextImpl();
		this.effects = new EffectsContextImpl();
		this.generationSettings = new GenerationSettingsContextImpl();
		this.spawnSettings = new SpawnSettingsContextImpl();
	}

	@Override
	public void setDepth(float depth) {
		biomeAccessor.fabric_setDepth(depth);
	}

	@Override
	public void setScale(float scale) {
		biomeAccessor.fabric_setScale(scale);
	}

	@Override
	public void setCategory(Biome.Category category) {
		biomeAccessor.fabric_setCategory(category);
	}

	@Override
	public WeatherContext getWeather() {
		return weather;
	}

	@Override
	public EffectsContext getEffects() {
		return effects;
	}

	@Override
	public GenerationSettingsContext getGenerationSettings() {
		return generationSettings;
	}

	@Override
	public SpawnSettingsContext getSpawnSettings() {
		return spawnSettings;
	}

	/**
	 * Re-freeze any immutable lists and perform general post-modification cleanup.
	 */
	void freeze() {
		generationSettings.freeze();
		spawnSettings.freeze();
	}

	private class WeatherContextImpl implements WeatherContext {
		private final BiomeWeatherAccessor accessor = (BiomeWeatherAccessor) biomeAccessor.fabric_getWeather();

		@Override
		public void setPrecipitation(Biome.Precipitation precipitation) {
			Objects.requireNonNull(precipitation);
			accessor.setPrecipitation(precipitation);
		}

		@Override
		public void setTemperature(float temperature) {
			accessor.setTemperature(temperature);
		}

		@Override
		public void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier) {
			Objects.requireNonNull(temperatureModifier);
			accessor.setTemperatureModifier(temperatureModifier);
		}

		@Override
		public void setDownfall(float downfall) {
			accessor.setDownfall(downfall);
		}
	}

	private class EffectsContextImpl implements EffectsContext {
		private final BiomeEffectsAccessor accessor = (BiomeEffectsAccessor) biome.getEffects();

		@Override
		public void setFogColor(int color) {
			accessor.fabric_setFogColor(color);
		}

		@Override
		public void setWaterColor(int color) {
			accessor.fabric_setWaterColor(color);
		}

		@Override
		public void setWaterFogColor(int color) {
			accessor.fabric_setWaterFogColor(color);
		}

		@Override
		public void setSkyColor(int color) {
			accessor.fabric_setSkyColor(color);
		}

		@Override
		public void setFoliageColor(Optional<Integer> color) {
			Objects.requireNonNull(color);
			accessor.fabric_setFoliageColor(color);
		}

		@Override
		public void setGrassColor(Optional<Integer> color) {
			Objects.requireNonNull(color);
			accessor.fabric_setGrassColor(color);
		}

		@Override
		public void setGrassColorModifier(@NotNull BiomeEffects.GrassColorModifier colorModifier) {
			Objects.requireNonNull(colorModifier);
			accessor.fabric_setGrassColorModifier(colorModifier);
		}

		@Override
		public void setParticleConfig(Optional<BiomeParticleConfig> particleConfig) {
			Objects.requireNonNull(particleConfig);
			accessor.fabric_setParticleConfig(particleConfig);
		}

		@Override
		public void setAmbientSound(Optional<SoundEvent> sound) {
			Objects.requireNonNull(sound);
			accessor.fabric_setLoopSound(sound);
		}

		@Override
		public void setMoodSound(Optional<BiomeMoodSound> sound) {
			Objects.requireNonNull(sound);
			accessor.fabric_setMoodSound(sound);
		}

		@Override
		public void setAdditionsSound(Optional<BiomeAdditionsSound> sound) {
			Objects.requireNonNull(sound);
			accessor.fabric_setAdditionsSound(sound);
		}

		@Override
		public void setMusic(Optional<MusicSound> sound) {
			Objects.requireNonNull(sound);
			accessor.fabric_setMusic(sound);
		}
	}

	private class GenerationSettingsContextImpl implements GenerationSettingsContext {
		private final Registry<ConfiguredCarver<?>> carvers = registries.get(Registry.CONFIGURED_CARVER_WORLDGEN);
		private final Registry<ConfiguredFeature<?, ?>> features = registries.get(Registry.CONFIGURED_FEATURE_WORLDGEN);
		private final Registry<ConfiguredStructureFeature<?, ?>> structures = registries.get(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN);
		private final Registry<ConfiguredSurfaceBuilder<?>> surfaceBuilders = registries.get(Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN);
		private final GenerationSettings generationSettings = biome.getGenerationSettings();
		private final GenerationSettingsAccessor accessor = (GenerationSettingsAccessor) generationSettings;

		/**
		 * Unfreeze the immutable lists found in the generation settings, and make sure they're filled up to every
		 * possible step if they're dense lists.
		 */
		GenerationSettingsContextImpl() {
			unfreezeCarvers();
			unfreezeFeatures();
			unfreezeFlowerFeatures();
			unfreezeStructures();
		}

		private void unfreezeCarvers() {
			Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carversByStep = new EnumMap<>(GenerationStep.Carver.class);
			carversByStep.putAll(accessor.fabric_getCarvers());

			for (GenerationStep.Carver step : GenerationStep.Carver.values()) {
				List<Supplier<ConfiguredCarver<?>>> carvers = carversByStep.get(step);

				if (carvers == null) {
					carvers = new ArrayList<>();
				} else {
					carvers = new ArrayList<>(carvers);
				}

				carversByStep.put(step, carvers);
			}

			accessor.fabric_setCarvers(carversByStep);
		}

		private void unfreezeFeatures() {
			List<List<Supplier<ConfiguredFeature<?, ?>>>> features = accessor.fabric_getFeatures();
			features = new ArrayList<>(features);

			for (int i = 0; i < features.size(); i++) {
				features.set(i, new ArrayList<>(features.get(i)));
			}

			accessor.fabric_setFeatures(features);
		}

		private void unfreezeFlowerFeatures() {
			accessor.fabric_setFlowerFeatures(new ArrayList<>(accessor.fabric_getFlowerFeatures()));
		}

		private void unfreezeStructures() {
			accessor.fabric_setStructureFeatures(new ArrayList<>(accessor.fabric_getStructureFeatures()));
		}

		/**
		 * Re-freeze the lists in the generation settings to immutable variants, also fixes the flower features.
		 */
		public void freeze() {
			freezeCarvers();
			freezeFeatures();
			freezeFlowerFeatures();
			freezeStructures();
		}

		private void freezeCarvers() {
			Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carversByStep = accessor.fabric_getCarvers();

			for (GenerationStep.Carver step : GenerationStep.Carver.values()) {
				carversByStep.put(step, ImmutableList.copyOf(carversByStep.get(step)));
			}

			accessor.fabric_setCarvers(ImmutableMap.copyOf(carversByStep));
		}

		private void freezeFeatures() {
			List<List<Supplier<ConfiguredFeature<?, ?>>>> featureSteps = accessor.fabric_getFeatures();

			for (int i = 0; i < featureSteps.size(); i++) {
				featureSteps.set(i, ImmutableList.copyOf(featureSteps.get(i)));
			}

			accessor.fabric_setFeatures(ImmutableList.copyOf(featureSteps));
		}

		private void freezeFlowerFeatures() {
			accessor.fabric_setFlowerFeatures(ImmutableList.copyOf(accessor.fabric_getFlowerFeatures()));
		}

		private void freezeStructures() {
			accessor.fabric_setStructureFeatures(ImmutableList.copyOf(accessor.fabric_getStructureFeatures()));
		}

		@Override
		public void setSurfaceBuilder(RegistryKey<ConfiguredSurfaceBuilder<?>> surfaceBuilderKey) {
			// We do not need to delay evaluation of this since the registries are already fully built
			ConfiguredSurfaceBuilder<?> surfaceBuilder = surfaceBuilders.getOrThrow(surfaceBuilderKey);
			accessor.fabric_setSurfaceBuilder(() -> surfaceBuilder);
		}

		@Override
		public boolean removeFeature(GenerationStep.Feature step, RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey) {
			ConfiguredFeature<?, ?> configuredFeature = features.getOrThrow(configuredFeatureKey);

			int stepIndex = step.ordinal();
			List<List<Supplier<ConfiguredFeature<?, ?>>>> featureSteps = accessor.fabric_getFeatures();

			if (stepIndex >= featureSteps.size()) {
				return false; // The step was not populated with any features yet
			}

			List<Supplier<ConfiguredFeature<?, ?>>> featuresInStep = featureSteps.get(stepIndex);

			if (featuresInStep.removeIf(supplier -> supplier.get() == configuredFeature)) {
				rebuildFlowerFeatures();
				return true;
			}

			return false;
		}

		@Override
		public void addFeature(GenerationStep.Feature step, RegistryKey<ConfiguredFeature<?, ?>> configuredFeatureKey) {
			// We do not need to delay evaluation of this since the registries are already fully built
			ConfiguredFeature<?, ?> configuredFeature = features.getOrThrow(configuredFeatureKey);

			List<List<Supplier<ConfiguredFeature<?, ?>>>> featureSteps = accessor.fabric_getFeatures();
			int index = step.ordinal();

			// Add new empty lists for the generation steps that have no features yet
			while (index >= featureSteps.size()) {
				featureSteps.add(new ArrayList<>());
			}

			featureSteps.get(index).add(() -> configuredFeature);

			// Ensure the list of flower features is up to date
			rebuildFlowerFeatures();
		}

		@Override
		public void addCarver(GenerationStep.Carver step, RegistryKey<ConfiguredCarver<?>> carverKey) {
			// We do not need to delay evaluation of this since the registries are already fully built
			ConfiguredCarver<?> carver = carvers.getOrThrow(carverKey);
			accessor.fabric_getCarvers().get(step).add(() -> carver);
		}

		@Override
		public boolean removeCarver(GenerationStep.Carver step, RegistryKey<ConfiguredCarver<?>> configuredCarverKey) {
			ConfiguredCarver<?> carver = carvers.getOrThrow(configuredCarverKey);
			return accessor.fabric_getCarvers().get(step).removeIf(supplier -> supplier.get() == carver);
		}

		@Override
		public void addStructure(RegistryKey<ConfiguredStructureFeature<?, ?>> configuredStructureKey) {
			ConfiguredStructureFeature<?, ?> configuredStructure = structures.getOrThrow(configuredStructureKey);

			// Remove the same feature-type before adding it back again, i.e. a jungle and normal village
			// are mutually exclusive.
			removeStructure(configuredStructure.feature);

			accessor.fabric_getStructureFeatures().add(() -> configuredStructure);
		}

		@Override
		public boolean removeStructure(RegistryKey<ConfiguredStructureFeature<?, ?>> configuredStructureKey) {
			ConfiguredStructureFeature<?, ?> structure = structures.getOrThrow(configuredStructureKey);

			return accessor.fabric_getStructureFeatures().removeIf(s -> s.get() == structure);
		}

		@Override
		public boolean removeStructure(StructureFeature<?> structure) {
			return accessor.fabric_getStructureFeatures().removeIf(s -> s.get().feature == structure);
		}

		/**
		 * See the constructor of {@link GenerationSettings} for reference.
		 */
		private void rebuildFlowerFeatures() {
			List<ConfiguredFeature<?, ?>> flowerFeatures = accessor.fabric_getFlowerFeatures();
			flowerFeatures.clear();

			for (List<Supplier<ConfiguredFeature<?, ?>>> features : accessor.fabric_getFeatures()) {
				for (Supplier<ConfiguredFeature<?, ?>> supplier : features) {
					supplier.get().method_30648()
							.filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER)
							.forEachOrdered(flowerFeatures::add);
				}
			}
		}
	}

	private class SpawnSettingsContextImpl implements SpawnSettingsContext {
		private final SpawnSettingsAccessor accessor = (SpawnSettingsAccessor) biome.getSpawnSettings();

		SpawnSettingsContextImpl() {
			unfreezeSpawners();
			unfreezeSpawnCost();
		}

		private void unfreezeSpawners() {
			EnumMap<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawners = new EnumMap<>(SpawnGroup.class);
			spawners.putAll(accessor.fabric_getSpawners());

			for (SpawnGroup spawnGroup : SpawnGroup.values()) {
				List<SpawnSettings.SpawnEntry> entries = spawners.get(spawnGroup);

				if (entries != null) {
					spawners.put(spawnGroup, new ArrayList<>(entries));
				} else {
					spawners.put(spawnGroup, new ArrayList<>());
				}
			}

			accessor.fabric_setSpawners(spawners);
		}

		private void unfreezeSpawnCost() {
			accessor.fabric_setSpawnCosts(new HashMap<>(accessor.fabric_getSpawnCosts()));
		}

		public void freeze() {
			freezeSpawners();
			freezeSpawnCosts();
		}

		private void freezeSpawners() {
			Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawners = accessor.fabric_getSpawners();

			for (Map.Entry<SpawnGroup, List<SpawnSettings.SpawnEntry>> entry : spawners.entrySet()) {
				entry.setValue(ImmutableList.copyOf(entry.getValue()));
			}

			accessor.fabric_setSpawners(ImmutableMap.copyOf(spawners));
		}

		private void freezeSpawnCosts() {
			accessor.fabric_setSpawnCosts(ImmutableMap.copyOf(accessor.fabric_getSpawnCosts()));
		}

		@Override
		public void setPlayerSpawnFriendly(boolean playerSpawnFriendly) {
			accessor.fabric_setPlayerSpawnFriendly(playerSpawnFriendly);
		}

		@Override
		public void setCreatureSpawnProbability(float probability) {
			accessor.fabric_setCreatureSpawnProbability(probability);
		}

		@Override
		public void addSpawn(SpawnGroup spawnGroup, SpawnSettings.SpawnEntry spawnEntry) {
			Objects.requireNonNull(spawnGroup);
			Objects.requireNonNull(spawnEntry);

			accessor.fabric_getSpawners().get(spawnGroup).add(spawnEntry);
		}

		@Override
		public boolean removeSpawns(BiPredicate<SpawnGroup, SpawnSettings.SpawnEntry> predicate) {
			Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawners = accessor.fabric_getSpawners();
			boolean anyRemoved = false;

			for (SpawnGroup group : SpawnGroup.values()) {
				if (spawners.get(group).removeIf(entry -> predicate.test(group, entry))) {
					anyRemoved = true;
				}
			}

			return anyRemoved;
		}

		@Override
		public void setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
			Objects.requireNonNull(entityType);
			accessor.fabric_getSpawnCosts().put(entityType, SpawnDensityAccessor.create(gravityLimit, mass));
		}

		@Override
		public void clearSpawnCost(EntityType<?> entityType) {
			accessor.fabric_getSpawnCosts().remove(entityType);
		}
	}
}
