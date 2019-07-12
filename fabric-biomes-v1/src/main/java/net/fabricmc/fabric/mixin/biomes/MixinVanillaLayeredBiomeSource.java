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

import net.fabricmc.fabric.impl.biomes.InternalBiomeData;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

/**
 * Adds the biomes in world gen to the array for the vanilla layered biome source.
 * This helps with {@link VanillaLayeredBiomeSource#hasStructureFeature(StructureFeature)} returning correctly for modded biomes as well as in {@link VanillaLayeredBiomeSource#getTopMaterials()}}
 */
@Mixin(VanillaLayeredBiomeSource.class)
public class MixinVanillaLayeredBiomeSource {

	@Shadow
	@Final
	@Mutable
	private Biome[] biomes;

	@Unique
	private int injectionCount;

	@Inject(at = @At("HEAD"), method = "hasStructureFeature")
	private void hasStructureFeature(CallbackInfoReturnable<Boolean> info) {
		updateInjections();
	}

	@Inject(at = @At("HEAD"), method = "getTopMaterials")
	private void getTopMaterials(CallbackInfoReturnable<Set<BlockState>> info) {
		updateInjections();
	}

	@Unique
	private void updateInjections() {
		List<Biome> injectedBiomes = InternalBiomeData.getOverworldInjectedBiomes();
		int currentSize = injectedBiomes.size();
		if (this.injectionCount < currentSize) {
			List<Biome> toInject = injectedBiomes.subList(injectionCount, currentSize - 1);

			Biome[] oldBiomes = this.biomes;
			this.biomes = new Biome[oldBiomes.length + toInject.size()];
			System.arraycopy(oldBiomes, 0, this.biomes, 0, oldBiomes.length);

			int index = oldBiomes.length;
			for (Biome injected : toInject) {
				biomes[index++] = injected;
			}

			injectionCount += toInject.size();
		}
	}

}
