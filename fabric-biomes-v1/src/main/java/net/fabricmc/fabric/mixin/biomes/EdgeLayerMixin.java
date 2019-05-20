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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.biomes.BiomeLists;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.AddEdgeBiomesLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

@Mixin(AddEdgeBiomesLayer.class)
public class EdgeLayerMixin
{
	@Inject(at = @At(value = "HEAD"), method = "sample", cancellable = true)
	private void sample(LayerRandomnessSource rand, int int_1, int int_2, int int_3, int int_4, int int_5,
			CallbackInfoReturnable<Integer> info)
	{
		Biome prevBiome = Registry.BIOME.get(int_5);

		if (BiomeLists.SHORE_MAP.containsKey(prevBiome) && isShoreToGenerate(int_1, int_2, int_3, int_4))
		{
			info.setReturnValue(BiomeLists.SHORE_MAP.get(prevBiome).pickRandomBiome(rand));
		}
		else if (BiomeLists.EDGE_MAP.containsKey(prevBiome) && isEdgeToGenerate(int_1, int_2, int_3, int_4, int_5))
		{
			info.setReturnValue(BiomeLists.EDGE_MAP.get(prevBiome).pickRandomBiome(rand));
		}
	}
	
	private boolean isEdgeToGenerate(int int_1, int int_2, int int_3, int int_4, int biome)
	{
		boolean b = int_1 != biome || int_2 != biome || int_3 != biome || int_4 != biome;
		
		return b;
	}

	private boolean isShoreToGenerate(int int_1, int int_2, int int_3, int int_4)
	{
		return isOceanBiome(int_1) || isOceanBiome(int_2) || isOceanBiome(int_3) || isOceanBiome(int_4);
	}
	
	private boolean isOceanBiome(int int_1)
	{
		Biome biome = Registry.BIOME.get(int_1);
		if (biome != null)
			return biome.getCategory() == Biome.Category.OCEAN;
		else
			return false;
	}
}
