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

import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import net.fabricmc.fabric.impl.biome.NetherBiomeData;

/**
 * This Mixin is responsible for adding mod-biomes to the NETHER preset in the MultiNoiseBiomeSource.
 */
@Mixin(MultiNoiseBiomeSource.Preset.class)
public class MixinMultiNoiseBiomeSource {
	// NOTE: This is a lambda-function in the NETHER preset field initializer
	@Inject(method = "method_31088", at = @At("RETURN"), cancellable = true)
	private static void appendNetherBiomes(Registry<Biome> registry, CallbackInfoReturnable<MultiNoiseUtil.Entries<Supplier<Biome>>> cri) {
		MultiNoiseUtil.Entries<Supplier<Biome>> biomes = cri.getReturnValue();
		List<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<Biome>>> entries = new ArrayList<>(biomes.getEntries());

		// add fabric biome noise point data to list && BiomeSource biome list
		NetherBiomeData.getNetherBiomeNoisePoints().forEach((biomeKey, noisePoint) -> {
			Biome biome = registry.getOrThrow(biomeKey);
			// NOTE: Even though we have to pass in suppliers, BiomeSource's ctor will resolve them immediately
			entries.add(Pair.of(noisePoint, () -> biome));
		});

		cri.setReturnValue(new MultiNoiseUtil.Entries<>(entries));
	}
}
