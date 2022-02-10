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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.class_6880;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

import net.fabricmc.fabric.impl.biome.TheEndBiomeData;

@Mixin(TheEndBiomeSource.class)
public class MixinTheEndBiomeSource {
	// TODO 22w06a, this isnt really a thing now.
	@Shadow
	@Final
	private long seed;
	@Unique
	private final PerlinNoiseSampler sampler = new PerlinNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(seed)));

	@Inject(method = "getBiome", at = @At("RETURN"), cancellable = true)
	private void getWeightedEndBiome(int biomeX, int biomeY, int biomeZ, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler, CallbackInfoReturnable<class_6880<Biome>> cir) {
		class_6880<Biome> vanillaBiome = cir.getReturnValue();

		class_6880<Biome> replacementBiome = TheEndBiomeData.pickEndBiome(biomeX, biomeY, biomeZ, sampler, vanillaBiome);

		cir.setReturnValue(replacementBiome);
	}
}
