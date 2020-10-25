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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.layer.AddRiversLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.fabricmc.fabric.impl.biome.InternalBiomeUtils;

/**
 * Sets river biomes specified with {@link OverworldBiomes#setRiverBiome(RegistryKey, RegistryKey)}.
 */
@Mixin(AddRiversLayer.class)
public class MixinAddRiversLayer {
	@Inject(at = @At("HEAD"), method = "sample", cancellable = true)
	private void sample(LayerRandomnessSource rand, LayerSampler landSampler, LayerSampler riverSampler, int x, int z, CallbackInfoReturnable<Integer> info) {
		int landBiomeId = landSampler.sample(x, z);
		RegistryKey<Biome> landBiomeKey = BuiltinBiomes.fromRawId(landBiomeId);

		int riverBiomeId = riverSampler.sample(x, z);
		Map<RegistryKey<Biome>, RegistryKey<Biome>> overworldRivers = InternalBiomeData.getOverworldRivers();

		if (overworldRivers.containsKey(landBiomeKey) && BuiltinBiomes.fromRawId(riverBiomeId) == BiomeKeys.RIVER) {
			RegistryKey<Biome> riverBiome = overworldRivers.get(landBiomeKey);
			info.setReturnValue(riverBiome == null ? landBiomeId : InternalBiomeUtils.getRawId(riverBiome));
		}
	}
}
