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

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
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
	@Shadow
	@Final
	private long seed;
	@Unique
	private LayerRandomnessSource randomnessSource = new SimpleLayerRandomnessSource(seed);

	@Inject(method = "getBiomeForNoiseGen", at = @At("RETURN"), cancellable = true)
	private void fabric_getWeightedEndBiome(int biomeX, int biomeY, int biomeZ, CallbackInfoReturnable<Biome> cir) {
		Biome vanillaBiome = cir.getReturnValue();

		// Since all vanilla biomes are added to the registry, this will never fail.
		RegistryKey<Biome> vanillaKey = biomeRegistry.getKey(vanillaBiome).get();
		// Since the pickers are statically populated by InternalBiomeData, picker will never be null.
		WeightedBiomePicker picker = InternalBiomeData.getEndBiomesMap().get(vanillaKey);
		RegistryKey<Biome> biomeKey = picker.pickFromNoise(randomnessSource, biomeX/64.0, 0, biomeZ/64.0);

		cir.setReturnValue(biomeRegistry.get(biomeKey));
	}
}
