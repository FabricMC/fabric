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

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.biome.InternalBiomeUtils;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.EaseBiomeEdgeLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

@Mixin(EaseBiomeEdgeLayer.class)
public class MixinEaseBiomeEdgeLayer {
	
	@Inject(at = @At("HEAD"), method = "sample", cancellable = true)
	private void sample(LayerRandomnessSource random, int north, int east, int south, int west, int center, CallbackInfoReturnable<Integer> info) {
		Biome biome = Registry.BIOME.get(center);
		
		// not allowing duplicate borders, for efficiency
		Set<Biome> borders = new HashSet<>();
		
		if (north != center) {
			borders.add(Registry.BIOME.get(north));
		}
		if (east != center) {
			borders.add(Registry.BIOME.get(east));
		}
		if (south != center) {
			borders.add(Registry.BIOME.get(south));
		}
		if (west != center) {
			borders.add(Registry.BIOME.get(west));
		}
		
		if (!borders.isEmpty()) {
			InternalBiomeUtils.transformLargeEdge(random, biome, borders.toArray(new Biome[0]), info::setReturnValue);
		}
	}
}
