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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Adds the biomes in world gen to the array for the vanilla layered biome source.
 * This helps with {@link VanillaLayeredBiomeSource#hasStructureFeature(StructureFeature)} returning correctly for modded biomes as well as in {@link VanillaLayeredBiomeSource#getTopMaterials()}}
 */
@Mixin(VanillaLayeredBiomeSource.class)
public class MixinVanillaLayeredBiomeSource {
	@Shadow
	@Final
	@Mutable
	private static Set<Biome> BIOMES;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void cinit(CallbackInfo info) {
		BIOMES = new HashSet<>(BIOMES);
	}

	//Called via reflection
	private static void fabric_injectBiome(Biome biome) {
		BIOMES.add(biome);
	}
}
