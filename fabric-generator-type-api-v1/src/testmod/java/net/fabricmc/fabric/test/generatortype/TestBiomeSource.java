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

package net.fabricmc.fabric.test.generatortype;

import java.util.Collections;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;

import net.fabricmc.fabric.api.generatortype.v1.FabricBiomeSource;

final class TestBiomeSource extends FabricBiomeSource {
	public static final Codec<TestBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(FabricBiomeSource::getBiomeRegistry),
			Codec.LONG.fieldOf("seed").stable().forGetter(FabricBiomeSource::getSeed))
			.apply(instance, instance.stable(TestBiomeSource::new)));

	TestBiomeSource(Registry<Biome> biomeRegistry, long seed) {
		super(biomeRegistry, seed, Collections.emptyList());
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return this;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.getBiomeRegistry().getOrThrow(BiomeKeys.PLAINS);
	}
}
