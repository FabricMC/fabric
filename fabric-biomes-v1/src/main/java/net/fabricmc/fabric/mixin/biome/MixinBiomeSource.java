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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;

/**
 * Adds spawn biomes to the base {@link BiomeSource} class.
 */
@Mixin(BiomeSource.class)
public class MixinBiomeSource {
	@Shadow
	@Final
	private static List<Biome> SPAWN_BIOMES;

	@Inject(at = @At("RETURN"), cancellable = true, method = "getSpawnBiomes")
	private void getSpawnBiomes(CallbackInfoReturnable<List<Biome>> info) {
		Set<Biome> biomes = new LinkedHashSet<>(info.getReturnValue());

		if (biomes.addAll(InternalBiomeData.getSpawnBiomes())) {
			info.setReturnValue(new ArrayList<>(biomes));
		}
	}
}
