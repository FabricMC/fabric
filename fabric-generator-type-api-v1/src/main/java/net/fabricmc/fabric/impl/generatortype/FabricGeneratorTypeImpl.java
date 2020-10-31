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

package net.fabricmc.fabric.impl.generatortype;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.generatortype.v1.FabricGeneratorConfig;
import net.fabricmc.loader.api.FabricLoader;

public abstract class FabricGeneratorTypeImpl<T extends FabricGeneratorConfig> {
	public static final Map<String, FabricGeneratorTypeImpl<?>> GENERATOR_TYPE_MAP = new HashMap<>();
	private final boolean debugGeneratorType;

	protected FabricGeneratorTypeImpl(Identifier name, boolean debugGeneratorType) {
		GENERATOR_TYPE_MAP.put(name.toString(), this);
		this.debugGeneratorType = debugGeneratorType;

		if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
			new ClientGeneratorType(name.toString().replace(":", "."));
		}
	}

	public abstract ChunkGenerator getChunkGenerator(BiomeSource biomeSource, Supplier<ChunkGeneratorSettings> generatorSettings, FabricGeneratorConfig generatorConfig, long seed);

	public abstract RegistryKey<ChunkGeneratorSettings> getGeneratorSettings();

	public abstract BiomeSource getBiomeSource(Registry<Biome> biomeRegistry, long seed);

	public @Nullable abstract T getGeneratorConfig(Registry<Biome> biomeRegistry);

	@Environment(EnvType.CLIENT)
	public @Nullable abstract GeneratorType.ScreenProvider getCustomizationScreenFactory();

	@Environment(EnvType.CLIENT)
	public class ClientGeneratorType extends GeneratorType {
		private ClientGeneratorType(String translationKey) {
			super(translationKey);
			VALUES.add(this);
		}

		@Override
		protected ChunkGenerator getChunkGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> generatorSettingsRegistry, long seed) {
			return FabricGeneratorTypeImpl.this.getChunkGenerator(getBiomeSource(biomeRegistry, seed), () -> generatorSettingsRegistry.getOrThrow(FabricGeneratorTypeImpl.this.getGeneratorSettings()),
					FabricGeneratorTypeImpl.this.getGeneratorConfig(biomeRegistry), seed);
		}

		public GeneratorType.ScreenProvider getCustomizationScreenFactory() {
			return FabricGeneratorTypeImpl.this.getCustomizationScreenFactory();
		}

		public boolean isDebugGeneratorType() {
			return FabricGeneratorTypeImpl.this.debugGeneratorType;
		}
	}
}
