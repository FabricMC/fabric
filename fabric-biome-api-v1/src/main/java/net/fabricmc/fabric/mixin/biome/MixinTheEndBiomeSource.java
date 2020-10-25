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

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.fabricmc.fabric.impl.biome.SimpleLayerRandomnessSource;
import net.fabricmc.fabric.impl.biome.WeightedBiomePicker;

@Mixin(TheEndBiomeSource.class)
public class MixinTheEndBiomeSource {
	@Shadow
	@Final
	private Registry<Biome> biomeRegistry;
	@Unique
	private final Map<RegistryKey<Biome>, RegistryKey<Biome>> BIOME_REGION_MAP = new HashMap<>();
	@Unique
	private LayerRandomnessSource randomnessSource;

	@Inject(method = "<init>(Lnet/minecraft/util/registry/Registry;JLnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/biome/Biome;)V", at = @At("TAIL"))
	private void fabric_addDefaultEndBiomes(Registry<Biome> biomeRegistry, long seed, Biome centerBiome, Biome highlandsBiome, Biome midlandsBiome, Biome smallIslandsBiome, Biome barrensBiome, CallbackInfo ci) {
		randomnessSource = new SimpleLayerRandomnessSource(seed);

		BIOME_REGION_MAP.put(BiomeKeys.THE_END, BiomeKeys.THE_END);
		BIOME_REGION_MAP.put(BiomeKeys.END_HIGHLANDS, BiomeKeys.END_HIGHLANDS);
		BIOME_REGION_MAP.put(BiomeKeys.END_MIDLANDS, BiomeKeys.END_MIDLANDS);
		BIOME_REGION_MAP.put(BiomeKeys.END_BARRENS, BiomeKeys.END_BARRENS);
		BIOME_REGION_MAP.put(BiomeKeys.SMALL_END_ISLANDS, BiomeKeys.SMALL_END_ISLANDS);

		for (Map.Entry<RegistryKey<Biome>, RegistryKey<Biome>> entry : BIOME_REGION_MAP.entrySet()) {
			RegistryKey<Biome> biome = entry.getKey();
			RegistryKey<Biome> region = entry.getValue();
			InternalBiomeData.addEndBiomeReplacement(biome, region, 1.0);
		}
	}

	@Inject(method = "getBiomeForNoiseGen", at = @At("RETURN"), cancellable = true)
	private void fabric_getWeightedEndBiome(int biomeX, int biomeY, int biomeZ, CallbackInfoReturnable<Biome> cir) {
		Biome vanillaBiome = cir.getReturnValue();

		RegistryKey<Biome> vanillaKey = biomeRegistry.getKey(vanillaBiome).get();
		// Since the pickers are populated by this mixin, picker will never be null.
		WeightedBiomePicker picker = InternalBiomeData.getEndVariants().get(vanillaKey);
		RegistryKey<Biome> biomeKey = picker.pickFromNoise(randomnessSource, biomeX/64.0, 0, biomeZ/64.0);

		cir.setReturnValue(biomeRegistry.get(biomeKey));
	}
}
