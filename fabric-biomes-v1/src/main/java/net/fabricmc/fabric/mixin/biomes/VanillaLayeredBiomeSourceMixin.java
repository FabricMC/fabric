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

import java.util.Set;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.biomes.BiomeLists;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;

@Mixin(VanillaLayeredBiomeSource.class)
public class VanillaLayeredBiomeSourceMixin
{
	@Inject(at = @At("HEAD"), method = "hasStructureFeature", cancellable = true)
	private void hasStructureFeature(StructureFeature<?> structureFeature, CallbackInfoReturnable<Boolean> info)
	{
		Function<StructureFeature<?>, Boolean> b = (structure) -> {
			
			Set<Biome> customBiomeSet = BiomeLists.CUSTOM_BIOMES;
			
			for(Biome biome : customBiomeSet)
			{
				if (biome.hasStructureFeature(structure))
				{
					return true;
				}
			}

			return false;
		};
		
		if (b.apply(structureFeature).booleanValue())
			info.setReturnValue(Boolean.TRUE);
	}
}
