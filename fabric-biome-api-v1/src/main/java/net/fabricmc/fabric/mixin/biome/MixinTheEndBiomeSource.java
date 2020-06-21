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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;

import net.fabricmc.fabric.impl.biome.EndRegion;
import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.fabricmc.fabric.impl.biome.SimpleLayerRandomnessSource;
import net.fabricmc.fabric.impl.biome.WeightedBiomePicker;

@Mixin(TheEndBiomeSource.class)
public class MixinTheEndBiomeSource {
	@Shadow
	@Final
	private long field_24731;
	private final Map<Biome, EndRegion> BIOME_REGION_MAP = new HashMap<>();
	private final LayerRandomnessSource randomnessSource = new SimpleLayerRandomnessSource(field_24731);

	@Inject(method = "<init>", at = @At("TAIL"))
	private void fabric_addDefaultEndBiomes(long l, CallbackInfo ci) {
		BIOME_REGION_MAP.put(Biomes.THE_END, EndRegion.MAIN_ISLAND);
		BIOME_REGION_MAP.put(Biomes.END_HIGHLANDS, EndRegion.HIGHLANDS);
		BIOME_REGION_MAP.put(Biomes.END_MIDLANDS, EndRegion.MIDLANDS);
		BIOME_REGION_MAP.put(Biomes.END_BARRENS, EndRegion.BARRENS);
		BIOME_REGION_MAP.put(Biomes.SMALL_END_ISLANDS, EndRegion.SMALL_ISLANDS);

		for (Map.Entry<Biome, EndRegion> entry : BIOME_REGION_MAP.entrySet()) {
			Biome biome = entry.getKey();
			EndRegion region = entry.getValue();
			InternalBiomeData.addEndBiome(biome, region, 1.0);
		}
	}

	@Inject(method = "getBiomeForNoiseGen", at = @At("RETURN"), cancellable = true)
	private void fabric_getModdedEndBiome(int biomeX, int biomeY, int biomeZ, CallbackInfoReturnable<Biome> cir) {
		Biome vanillaBiome = cir.getReturnValue();

		if (BIOME_REGION_MAP.containsKey(vanillaBiome)) {
			EndRegion region = BIOME_REGION_MAP.get(vanillaBiome);
			WeightedBiomePicker picker = InternalBiomeData.getEndRegionBiomePickers().get(region);

			cir.setReturnValue(picker.pickFromNoise(randomnessSource, biomeX, 0, biomeZ));
		}
	}
}
