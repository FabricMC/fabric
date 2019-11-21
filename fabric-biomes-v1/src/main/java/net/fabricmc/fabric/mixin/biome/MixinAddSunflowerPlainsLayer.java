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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.biome.InternalBiomeUtils;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.AddSunflowerPlainsLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

@Mixin(AddSunflowerPlainsLayer.class)
public class MixinAddSunflowerPlainsLayer {
	
	@Inject(at = @At("TAIL"), method = "sample", cancellable = true)
	private void sample(LayerRandomnessSource random, int previous, CallbackInfoReturnable<Integer> info) {
		Biome biome = Registry.BIOME.get(previous);
		
		InternalBiomeUtils.transformSubBiome(random, biome, info::setReturnValue);
	}
}
