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

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.biomes.v1.BiomeClimate;
import net.fabricmc.fabric.impl.biomes.BiomeLists;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.SetBaseBiomesLayer;

@Mixin(SetBaseBiomesLayer.class)
public class SetBaseBiomesLayerMixin
{
	@Shadow
	@Final
	@Mutable
	private static int[] SNOWY_BIOMES;

	@Shadow
	@Final
	@Mutable
	private static int[] COOL_BIOMES;

	@Shadow
	@Final
	@Mutable
	private static int[] TEMPERATE_BIOMES;

	@Shadow
	@Final
	@Mutable
	private static int[] DRY_BIOMES;

	private static int pointer = 0;

	@Inject(at = @At("HEAD"), method = "sample")
	private void sample(LayerRandomnessSource rand, int value, CallbackInfoReturnable<Integer> info)
	{
		if (pointer < BiomeLists.INJECTED_BIOME_LIST.size())
		{
			update();
		}
	}

	private void update()
	{
		for (; pointer < BiomeLists.INJECTED_BIOME_LIST.size(); ++pointer)
		{
			
			Pair<Integer, BiomeClimate> pair = BiomeLists.INJECTED_BIOME_LIST.get(pointer);
			
			switch (pair.getRight())
			{
			case SNOWY:
				SNOWY_BIOMES = ArrayUtils.addAll(SNOWY_BIOMES, pair.getLeft());
				break;
			case COOL:
				COOL_BIOMES = ArrayUtils.addAll(COOL_BIOMES, pair.getLeft());
				break;
			case DRY:
				DRY_BIOMES = ArrayUtils.addAll(DRY_BIOMES, pair.getLeft());
				break;
			case TEMPERATE:
				TEMPERATE_BIOMES = ArrayUtils.addAll(TEMPERATE_BIOMES, pair.getLeft());
				break;
			default:
				break;
			}
		}
	}
}
