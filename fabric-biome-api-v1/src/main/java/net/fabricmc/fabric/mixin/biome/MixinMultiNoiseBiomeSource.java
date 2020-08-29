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

package net.fabricmc.fabric.mixin.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;

/**
 * This Mixin is responsible for adding mod-biomes to the NETHER preset in the MultiNoiseBiomeSource.
 */
@Mixin(MultiNoiseBiomeSource.Preset.class)
public class MixinMultiNoiseBiomeSource {
	// NOTE: This is a lambda-function in the NETHER preset field initializer
	@Inject(method = "method_31088", at = @At("RETURN"))
	private static void modifyNoisePoints(MultiNoiseBiomeSource.Preset preset, Registry<Biome> biomeRegistry, Long seed, CallbackInfoReturnable<MultiNoiseBiomeSource> cir) {
		MultiNoiseBiomeSource returnedSource = cir.getReturnValue();
		MultiNoiseBiomeSourceAccessor sourceAccessor = (MultiNoiseBiomeSourceAccessor) returnedSource;
		BiomeSourceAccessor baseSourceAccessor = (BiomeSourceAccessor) returnedSource;

		// collect existing noise points in non-immutable map
		List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> existingPoints = new ArrayList<>(sourceAccessor.getBiomePoints());
		List<Biome> biomes = new ArrayList<>(returnedSource.getBiomes());

		// add fabric biome noise point data to list && BiomeSource biome list
		InternalBiomeData.getNetherBiomeNoisePoints().forEach((biomeKey, noisePoint) -> {
			Biome biome = biomeRegistry.method_31140(biomeKey);
			// NOTE: Even though we have to pass in suppliers, BiomeSource's ctor will resolve them immediately
			existingPoints.add(Pair.of(noisePoint, () -> biome));
			biomes.add(biome);
		});

		// modify MultiNoiseBiomeSource list with updated data
		sourceAccessor.setBiomePoints(ImmutableList.copyOf(existingPoints));
		baseSourceAccessor.setBiomes(ImmutableList.copyOf(biomes));
	}
}
