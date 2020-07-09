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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;

@Mixin(MultiNoiseBiomeSource.class)
public class MixinMultiNoiseBiomeSource {
	@Inject(method = "method_28467", at = @At("RETURN"))
	private static void modifyNoisePoints(long l, CallbackInfoReturnable<MultiNoiseBiomeSource> cir) {
		MultiNoiseBiomeSource returnedSource = cir.getReturnValue();

		// collect existing noise points in non-immutable map
		List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> existingPoints = new ArrayList<>(((MultiNoiseBiomeSourceAccessor) returnedSource).getBiomePoints());

		// add fabric biome noise point data to list && BiomeSource biome list
		InternalBiomeData.getNetherBiomeNoisePoints().forEach((biome, noisePoint) -> {
			existingPoints.add(Pair.of(noisePoint, () -> biome));
			returnedSource.getBiomes().add(biome);
		});

		// modify MultiNoiseBiomeSource list with updated data
		((MultiNoiseBiomeSourceAccessor) returnedSource).setBiomePoints(existingPoints);
	}
}
