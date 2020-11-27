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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.layer.SetBaseBiomesLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.fabricmc.fabric.impl.biome.InternalBiomeUtils;

/**
 * Injects biomes into the arrays of biomes in the {@link SetBaseBiomesLayer}.
 */
@Mixin(SetBaseBiomesLayer.class)
public class MixinSetBaseBiomesLayer {
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

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/biome/layer/SetBaseBiomesLayer;chosenGroup1:[I"), method = "sample", cancellable = true)
	private void injectDryBiomes(LayerRandomnessSource random, int value, CallbackInfoReturnable<Integer> info) {
		InternalBiomeUtils.injectBiomesIntoClimate(random, DRY_BIOMES, OverworldClimate.DRY, info::setReturnValue);
	}

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/biome/layer/SetBaseBiomesLayer;TEMPERATE_BIOMES:[I"), method = "sample", cancellable = true)
	private void injectTemperateBiomes(LayerRandomnessSource random, int value, CallbackInfoReturnable<Integer> info) {
		InternalBiomeUtils.injectBiomesIntoClimate(random, TEMPERATE_BIOMES, OverworldClimate.TEMPERATE, info::setReturnValue);
	}

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/biome/layer/SetBaseBiomesLayer;SNOWY_BIOMES:[I"), method = "sample", cancellable = true)
	private void injectSnowyBiomes(LayerRandomnessSource random, int value, CallbackInfoReturnable<Integer> info) {
		InternalBiomeUtils.injectBiomesIntoClimate(random, SNOWY_BIOMES, OverworldClimate.SNOWY, info::setReturnValue);
	}

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/biome/layer/SetBaseBiomesLayer;COOL_BIOMES:[I"), method = "sample", cancellable = true)
	private void injectCoolBiomes(LayerRandomnessSource random, int value, CallbackInfoReturnable<Integer> info) {
		InternalBiomeUtils.injectBiomesIntoClimate(random, COOL_BIOMES, OverworldClimate.COOL, info::setReturnValue);
	}

	@Inject(at = @At("RETURN"), method = "sample", cancellable = true)
	private void transformVariants(LayerRandomnessSource random, int value, CallbackInfoReturnable<Integer> info) {
		int biomeId = info.getReturnValueI();
		RegistryKey<Biome> biome = BuiltinBiomes.fromRawId(biomeId);

		// Determine what special case this is...
		OverworldClimate climate;

		if (biome == BiomeKeys.BADLANDS_PLATEAU || biome == BiomeKeys.WOODED_BADLANDS_PLATEAU) {
			climate = OverworldClimate.DRY;
		} else if (biome == BiomeKeys.JUNGLE) {
			climate = OverworldClimate.TEMPERATE;
		} else if (biome == BiomeKeys.GIANT_TREE_TAIGA) {
			climate = OverworldClimate.TEMPERATE;
		} else {
			climate = null;
		}

		info.setReturnValue(InternalBiomeUtils.transformBiome(random, biome, climate));
	}
}
