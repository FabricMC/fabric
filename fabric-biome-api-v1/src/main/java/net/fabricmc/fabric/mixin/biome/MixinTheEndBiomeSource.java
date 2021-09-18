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

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.ChunkRandom;

import net.fabricmc.fabric.impl.biome.TheEndBiomeData;

@Mixin(TheEndBiomeSource.class)
public class MixinTheEndBiomeSource {
	@Shadow
	@Final
	private Registry<Biome> biomeRegistry;
	@Shadow
	@Final
	private long seed;
	@Unique
	private PerlinNoiseSampler sampler = new PerlinNoiseSampler(new ChunkRandom(seed));

	@Inject(method = "method_38109", at = @At("RETURN"), cancellable = true)
	private void getWeightedEndBiome(int biomeX, int biomeY, int biomeZ, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler, CallbackInfoReturnable<Biome> cir) {
		Biome vanillaBiome = cir.getReturnValue();

		// Since all vanilla biomes are added to the registry, this will never fail.
		RegistryKey<Biome> vanillaKey = biomeRegistry.getKey(vanillaBiome).get();
		RegistryKey<Biome> replacementKey = TheEndBiomeData.pickEndBiome(biomeX, biomeY, biomeZ, sampler, vanillaKey);

		cir.setReturnValue(biomeRegistry.get(replacementKey));
	}

}
