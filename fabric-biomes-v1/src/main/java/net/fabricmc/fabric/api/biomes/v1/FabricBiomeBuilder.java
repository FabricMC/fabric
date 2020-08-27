package net.fabricmc.fabric.api.biomes.v1;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

import net.fabricmc.fabric.mixin.biome.BiomeEffectsAccessor;
import net.fabricmc.fabric.mixin.biome.SpawnDensityAccessor;
import net.fabricmc.fabric.mixin.biome.SpawnSettingsAccessor;

public class FabricBiomeBuilder {
	// DynamicRegistryManager
	private DynamicRegistryManager registryManager;

	// Biome settings
	private float depth;
	private float scale;
	private float temperature;
	private float downfall;
	private Biome.Category category;
	private Biome.Precipitation precipitation;
	private Biome.TemperatureModifier temperatureModifier;

	// GenerationSettings
	private Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
	private Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers = Maps.newLinkedHashMap();
	private Map<GenerationStep.Feature, List<Supplier<ConfiguredFeature<?, ?>>>> features = Maps.newLinkedHashMap();
	private List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures = Lists.newArrayList();

	// SpawnSettings
	private Map<SpawnGroup, List<SpawnSettings.SpawnEntry>> spawners = Maps.newLinkedHashMap();
	private Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnDensities = Maps.newLinkedHashMap();
	private float creatureSpawnProbability = 0.1F;
	private boolean playerSpawnFriendly = false;

	// BiomeEffects
	private int fogColor;
	private int waterColor;
	private int waterFogColor;
	private int skyColor;
	private Optional<Integer> foliageColor = Optional.empty();
	private Optional<Integer> grassColor = Optional.empty();
	private BiomeEffects.GrassColorModifier grassColorModifier;
	private Optional<BiomeParticleConfig> particleConfig = Optional.empty();
	private Optional<SoundEvent> loopSound = Optional.empty();
	private Optional<BiomeMoodSound> moodSound = Optional.empty();
	private Optional<BiomeAdditionsSound> additionsSound = Optional.empty();
	private Optional<MusicSound> musicSound = Optional.empty();

	private FabricBiomeBuilder(DynamicRegistryManager registryManager) {
		this.registryManager = registryManager;
	}

	public static FabricBiomeBuilder of(Biome biome, DynamicRegistryManager registryManager) {
		FabricBiomeBuilder builder = new FabricBiomeBuilder(registryManager);
		builder.depth(biome.getDepth());
		builder.category(biome.getCategory());
		builder.precipitation(biome.getPrecipitation());
		builder.downfall(biome.getDownfall());
		builder.scale(biome.getScale());
		builder.temperature(biome.getTemperature());
		builder.temperatureModifier(Biome.TemperatureModifier.NONE);
		//TODO: temperatureModifier

		GenerationSettings generationSettings = biome.getGenerationSettings();

		for (GenerationStep.Carver step : GenerationStep.Carver.values()) {
			for (Supplier<ConfiguredCarver<?>> carver : generationSettings.getCarversForStep(step)) {
				builder.carver(step, carver);
			}
		}

		for (GenerationStep.Feature step : GenerationStep.Feature.values()) {
			if (step.ordinal() < generationSettings.getFeatures().size()) {
				for (Supplier<ConfiguredFeature<?, ?>> feature : generationSettings.getFeatures().get(step.ordinal())) {
					builder.feature(step, feature);
				}
			}
		}

		for (Supplier<ConfiguredStructureFeature<?, ?>> structureFeature : generationSettings.getStructureFeatures()) {
			builder.structureFeature(structureFeature);
		}

		builder.surfaceBuilder(generationSettings.getSurfaceBuilder());

		SpawnSettings spawnSettings = biome.getSpawnSettings();
		builder.playerSpawnFriendly(spawnSettings.isPlayerSpawnFriendly());
		builder.creatureSpawnProbability(spawnSettings.getCreatureSpawnProbability());

		for (SpawnGroup group : SpawnGroup.values()) {
			for (SpawnSettings.SpawnEntry entry : spawnSettings.getSpawnEntry(group)) {
				builder.spawn(group, entry);
			}
		}

		for (Map.Entry<EntityType<?>, SpawnSettings.SpawnDensity> entry : ((SpawnSettingsAccessor) spawnSettings).getSpawnCosts().entrySet()) {
			builder.spawnDensity(entry.getKey(), entry.getValue().getMass(), entry.getValue().getGravityLimit());
		}

		BiomeEffectsAccessor biomeEffects = (BiomeEffectsAccessor) biome.getEffects();
		builder.fogColor(biomeEffects.getFogColor());
		builder.waterColor(biomeEffects.getWaterColor());
		builder.waterFogColor(biomeEffects.getWaterFogColor());
		builder.skyColor(biomeEffects.getSkyColor());
		biomeEffects.getFoliageColor().ifPresent(builder::foliageColor);
		biomeEffects.getGrassColor().ifPresent(builder::grassColor);
		builder.grassColorModifier(biomeEffects.getGrassColorModifier());
		biomeEffects.getParticleConfig().ifPresent(builder::particleConfig);
		biomeEffects.getLoopSound().ifPresent(builder::loopSound);
		biomeEffects.getMoodSound().ifPresent(builder::moodSound);
		biomeEffects.getAdditionsSound().ifPresent(builder::additionsSound);
		biomeEffects.getMusic().ifPresent(builder::music);

		return builder;
	}

	public Biome build() {
		Biome.Builder biomeBuilder = new Biome.Builder();
		biomeBuilder.depth(depth);
		biomeBuilder.category(category);
		biomeBuilder.precipitation(precipitation);
		biomeBuilder.downfall(downfall);
		biomeBuilder.scale(scale);
		biomeBuilder.temperature(temperature);
		biomeBuilder.temperatureModifier(temperatureModifier);

		GenerationSettings.Builder generationSettingsBuilder = new GenerationSettings.Builder();
		generationSettingsBuilder.surfaceBuilder(surfaceBuilder);

		for (Map.Entry<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> entry : carvers.entrySet()) {
			for (Supplier<ConfiguredCarver<?>> carver : entry.getValue()) {
				generationSettingsBuilder.carver(entry.getKey(), carver.get());
			}
		}

		for (Map.Entry<GenerationStep.Feature, List<Supplier<ConfiguredFeature<?, ?>>>> entry : features.entrySet()) {
			for (Supplier<ConfiguredFeature<?, ?>> feature : entry.getValue()) {
				generationSettingsBuilder.feature(entry.getKey().ordinal(), feature);
			}
		}

		for (Supplier<ConfiguredStructureFeature<?, ?>> structureFeature : structureFeatures) {
			generationSettingsBuilder.structureFeature(structureFeature.get());
		}

		biomeBuilder.generationSettings(generationSettingsBuilder.build());

		SpawnSettings.Builder spawnSettingsBuilder = new SpawnSettings.Builder();
		spawnSettingsBuilder.creatureSpawnProbability(creatureSpawnProbability);

		if (playerSpawnFriendly) {
			spawnSettingsBuilder.playerSpawnFriendly();
		}

		for (Map.Entry<SpawnGroup, List<SpawnSettings.SpawnEntry>> entry : spawners.entrySet()) {
			for (SpawnSettings.SpawnEntry spawnEntry : entry.getValue()) {
				spawnSettingsBuilder.spawn(entry.getKey(), spawnEntry);
			}
		}

		for (Map.Entry<EntityType<?>, SpawnSettings.SpawnDensity> entry : spawnDensities.entrySet()) {
			spawnSettingsBuilder.spawnCost(entry.getKey(), entry.getValue().getMass(), entry.getValue().getGravityLimit());
		}

		biomeBuilder.spawnSettings(spawnSettingsBuilder.build());

		BiomeEffects.Builder biomeEffectsBuilder = new BiomeEffects.Builder();
		biomeEffectsBuilder.fogColor(fogColor);
		biomeEffectsBuilder.waterColor(waterColor);
		biomeEffectsBuilder.waterFogColor(waterFogColor);
		biomeEffectsBuilder.skyColor(skyColor);
		foliageColor.ifPresent(biomeEffectsBuilder::foliageColor);
		grassColor.ifPresent(biomeEffectsBuilder::grassColor);
		biomeEffectsBuilder.grassColorModifier(grassColorModifier);
		particleConfig.ifPresent(biomeEffectsBuilder::particleConfig);
		loopSound.ifPresent(biomeEffectsBuilder::loopSound);
		moodSound.ifPresent(biomeEffectsBuilder::moodSound);
		additionsSound.ifPresent(biomeEffectsBuilder::additionsSound);
		musicSound.ifPresent(biomeEffectsBuilder::music);
		biomeBuilder.effects(biomeEffectsBuilder.build());

		return biomeBuilder.build();
	}

	public FabricBiomeBuilder precipitation(Biome.Precipitation precipitation) {
		this.precipitation = precipitation;
		return this;
	}

	public FabricBiomeBuilder category(Biome.Category category) {
		this.category = category;
		return this;
	}

	public FabricBiomeBuilder depth(float depth) {
		this.depth = depth;
		return this;
	}

	public FabricBiomeBuilder scale(float scale) {
		this.scale = scale;
		return this;
	}

	public FabricBiomeBuilder temperature(float temperature) {
		this.temperature = temperature;
		return this;
	}

	public FabricBiomeBuilder downfall(float downfall) {
		this.downfall = downfall;
		return this;
	}

	public FabricBiomeBuilder temperatureModifier(Biome.TemperatureModifier temperatureModifier) {
		this.temperatureModifier = temperatureModifier;
		return this;
	}

	public FabricBiomeBuilder carver(GenerationStep.Carver step, Supplier<ConfiguredCarver<?>> carver) {
		this.carvers.computeIfAbsent(step, (s) -> Lists.newArrayList()).add(carver);
		return this;
	}

	public FabricBiomeBuilder carver(GenerationStep.Carver step, RegistryKey<ConfiguredCarver<?>> carver) {
		carver(step, () -> registryManager.get(Registry.CONFIGURED_CARVER_WORLDGEN).get(carver));
		return this;
	}

	public FabricBiomeBuilder feature(GenerationStep.Feature step, Supplier<ConfiguredFeature<?, ?>> feature) {
		this.features.computeIfAbsent(step, (s) -> Lists.newArrayList()).add(feature);
		return this;
	}

	public FabricBiomeBuilder feature(GenerationStep.Feature step, RegistryKey<ConfiguredFeature<?, ?>> feature) {
		feature(step, () -> registryManager.get(Registry.CONFIGURED_FEATURE_WORLDGEN).get(feature));
		return this;
	}

	public FabricBiomeBuilder structureFeature(Supplier<ConfiguredStructureFeature<?, ?>> structureFeature) {
		this.structureFeatures.add(structureFeature);
		return this;
	}

	public FabricBiomeBuilder structureFeature(RegistryKey<ConfiguredStructureFeature<?, ?>> structureFeature) {
		structureFeature(() -> registryManager.get(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN).get(structureFeature));
		return this;
	}

	public FabricBiomeBuilder surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder) {
		this.surfaceBuilder = surfaceBuilder;
		return this;
	}

	public FabricBiomeBuilder surfaceBuilder(RegistryKey<ConfiguredSurfaceBuilder<?>> surfaceBuilder) {
		surfaceBuilder(() -> registryManager.get(Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN).get(surfaceBuilder));
		return this;
	}

	public FabricBiomeBuilder playerSpawnFriendly() {
		this.playerSpawnFriendly = true;
		return this;
	}

	public FabricBiomeBuilder playerSpawnFriendly(boolean playerSpawnFriendly) {
		this.playerSpawnFriendly = playerSpawnFriendly;
		return this;
	}

	public FabricBiomeBuilder creatureSpawnProbability(float probability) {
		this.creatureSpawnProbability = probability;
		return this;
	}

	public FabricBiomeBuilder spawn(SpawnGroup spawnGroup, SpawnSettings.SpawnEntry spawnEntry) {
		this.spawners.computeIfAbsent(spawnGroup, (s) -> Lists.newArrayList()).add(spawnEntry);
		return this;
	}

	public FabricBiomeBuilder spawnDensity(EntityType<?> entityType, double mass, double maxDensity) {
		this.spawnDensities.put(entityType, SpawnDensityAccessor.create(maxDensity, mass));
		return this;
	}

	public FabricBiomeBuilder fogColor(int fogColor) {
		this.fogColor = fogColor;
		return this;
	}

	public FabricBiomeBuilder waterColor(int waterColor) {
		this.waterColor = waterColor;
		return this;
	}

	public FabricBiomeBuilder waterFogColor(int waterFogColor) {
		this.waterFogColor = waterFogColor;
		return this;
	}

	public FabricBiomeBuilder skyColor(int skyColor) {
		this.skyColor = skyColor;
		return this;
	}

	public FabricBiomeBuilder foliageColor(int foliageColor) {
		this.foliageColor = Optional.of(foliageColor);
		return this;
	}

	public FabricBiomeBuilder grassColor(int grassColor) {
		this.grassColor = Optional.of(grassColor);
		return this;
	}

	public FabricBiomeBuilder grassColorModifier(BiomeEffects.GrassColorModifier grassColorModifier) {
		this.grassColorModifier = grassColorModifier;
		return this;
	}

	public FabricBiomeBuilder particleConfig(BiomeParticleConfig particleConfig) {
		this.particleConfig = Optional.of(particleConfig);
		return this;
	}

	public FabricBiomeBuilder loopSound(SoundEvent sound) {
		this.loopSound = Optional.of(sound);
		return this;
	}

	public FabricBiomeBuilder moodSound(BiomeMoodSound moodSound) {
		this.moodSound = Optional.of(moodSound);
		return this;
	}

	public FabricBiomeBuilder additionsSound(BiomeAdditionsSound additionsSound) {
		this.additionsSound = Optional.of(additionsSound);
		return this;
	}

	public FabricBiomeBuilder music(MusicSound music) {
		this.musicSound = Optional.of(music);
		return this;
	}
}
