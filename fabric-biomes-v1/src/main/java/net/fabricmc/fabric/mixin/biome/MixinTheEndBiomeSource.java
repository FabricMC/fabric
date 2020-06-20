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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.fabricmc.fabric.impl.biome.SimpleLayerRandomnessSource;
import net.fabricmc.fabric.impl.biome.WeightedBiomePicker;

@Mixin(TheEndBiomeSource.class)
public class MixinTheEndBiomeSource {
	@Unique
	private static final Logger LOGGER = LogManager.getLogger();
	@Shadow
	@Final
	private Registry<Biome> field_26699;
	@Shadow
	@Final
	private long seed;
	@Unique
	private final LayerRandomnessSource randomnessSource = new SimpleLayerRandomnessSource(seed);

	@Inject(method = "getBiomeForNoiseGen", at = @At("RETURN"), cancellable = true)
	private void fabric_getWeightedEndBiome(int biomeX, int biomeY, int biomeZ, CallbackInfoReturnable<Biome> cir) {
		RegistryKey<Biome> vanillaKey = field_26699.getKey(cir.getReturnValue()).orElse(null);

		if (vanillaKey == null) {
			// Something is broken about the biome source in this case, it's returning biomes it should not even know about
			return;
		}

		WeightedBiomePicker picker = InternalBiomeData.getEndVariants().get(vanillaKey);

		if (picker != null) {
			RegistryKey<Biome> key = picker.pickFromNoise(randomnessSource, biomeX / 16.0, 0, biomeZ / 16.0);
			cir.setReturnValue(field_26699.get(key));
		}
	}
}
