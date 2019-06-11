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

package net.fabricmc.fabric.mixin.biomes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.biomes.InternalBiomeData;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.AddRiversLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.LayerSampler;

@Mixin(AddRiversLayer.class)
public class RiversLayerMixin
{
	@Shadow
	@Final
	private static int RIVER_ID;
	
	@Inject(at = @At("HEAD"), method = "sample", cancellable = true)
	private void sample(LayerRandomnessSource rand, LayerSampler landLayerSampler, LayerSampler riverLayerSampler, int x, int z, CallbackInfoReturnable<Integer> info)
	{
		int fabric_landBiomeId = landLayerSampler.sample(x, z);
		int fabric_riverBiomeId = riverLayerSampler.sample(x, z);
		
		Biome fabric_landBiome = Registry.BIOME.get(fabric_landBiomeId);
		
		if (InternalBiomeData.RIVER_MAP.containsKey(fabric_landBiome) && fabric_riverBiomeId == RIVER_ID)
		{
			Biome river = InternalBiomeData.RIVER_MAP.get(fabric_landBiome);
			
			info.setReturnValue(river == null ? fabric_landBiomeId : Registry.BIOME.getRawId(river));
		}
	}
}
