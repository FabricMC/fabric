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

import net.fabricmc.fabric.api.biomes.v1.RiverAssociate;
import net.fabricmc.fabric.impl.biomes.BiomeLists;
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
	private void sample(LayerRandomnessSource layerRandomnessSource_1, LayerSampler layerSampler_1, LayerSampler layerSampler_2, int int_1, int int_2,
			CallbackInfoReturnable<Integer> info)
	{
		int previousBiomeId = layerSampler_1.sample(int_1, int_2);
		int biome_2 = layerSampler_2.sample(int_1, int_2);
		
		Biome prevBiome = Registry.BIOME.get(previousBiomeId);
		
		if (BiomeLists.RIVER_MAP.containsKey(prevBiome) && biome_2 == RIVER_ID)
		{
			RiverAssociate associate = BiomeLists.RIVER_MAP.get(prevBiome);
			
			int returnBiome;
			
			if (associate == RiverAssociate.NONE)
				returnBiome = previousBiomeId;
			else
				returnBiome = associate.getBiome();
			
			info.setReturnValue(returnBiome);
		}
	}
}
