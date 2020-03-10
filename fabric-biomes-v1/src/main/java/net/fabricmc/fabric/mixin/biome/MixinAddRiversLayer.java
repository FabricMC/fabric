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

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.AddRiversLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.impl.biome.InternalBiomeData;

/**
 * Sets river biomes specified with {@link OverworldBiomes#setRiverBiome(Biome, Biome)}.
 */
@Mixin(AddRiversLayer.class)
public class MixinAddRiversLayer {
	@Shadow
	@Final
	private static int RIVER_ID;

	@Inject(at = @At("HEAD"), method = "sample", cancellable = true)
	private void sample(LayerRandomnessSource rand, LayerSampler landSampler, LayerSampler riverSampler, int x, int z, CallbackInfoReturnable<Integer> info) {
		int landBiomeId = landSampler.sample(x, z);
		Biome landBiome = Registry.BIOME.get(landBiomeId);

		int riverBiomeId = riverSampler.sample(x, z);
		Map<Biome, Biome> overworldRivers = InternalBiomeData.getOverworldRivers();

		if (overworldRivers.containsKey(landBiome) && riverBiomeId == RIVER_ID) {
			Biome riverBiome = overworldRivers.get(landBiome);
			info.setReturnValue(riverBiome == null ? landBiomeId : Registry.BIOME.getRawId(riverBiome));
		}
	}
}
