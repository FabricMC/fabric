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

import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.fabricmc.fabric.impl.biomes.BiomeEntry;
import net.fabricmc.fabric.impl.biomes.InternalBiomeData;
import net.fabricmc.fabric.impl.biomes.InternalBiomeUtils;
import net.fabricmc.fabric.impl.biomes.VariantTransformer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.SetBaseBiomesLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

/**
 * Injects biomes into the arrays of biomes in the {@link SetBaseBiomesLayer}
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
		int[] vanillaArray = DRY_BIOMES;
		OverworldClimate climate = OverworldClimate.DRY;
		Double mwt = InternalBiomeData.getOverworldModdedWeightTotals().get(climate);
		if (mwt == null) {
			return;
		}
		double moddedWeightTotal = mwt;
		int vanillaArrayWeight = vanillaArray.length;
		double reqWeightSum = (double) random.nextInt(Integer.MAX_VALUE) * (vanillaArray.length + moddedWeightTotal) / Integer.MAX_VALUE;
		if (reqWeightSum < vanillaArrayWeight) {
			info.setReturnValue(vanillaArray[(int) reqWeightSum]);
		}
		else {
			List<BiomeEntry> moddedBiomes = InternalBiomeData.getOverworldBaseBiomes().get(climate);
			info.setReturnValue(moddedBiomes.get(InternalBiomeUtils.searchForBiome(reqWeightSum, vanillaArrayWeight, moddedWeightTotal, moddedBiomes)).getRawId());
		}
	}

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/biome/layer/SetBaseBiomesLayer;TEMPERATE_BIOMES:[I"), method = "sample", cancellable = true)
	private void injectTemperateBiomes(LayerRandomnessSource random, int value, CallbackInfoReturnable<Integer> info) {
		int[] vanillaArray = TEMPERATE_BIOMES;
		OverworldClimate climate = OverworldClimate.TEMPERATE;
		Double mwt = InternalBiomeData.getOverworldModdedWeightTotals().get(climate);
		if (mwt == null) {
			return;
		}
		double moddedWeightTotal = mwt;
		int vanillaArrayWeight = vanillaArray.length;
		double reqWeightSum = (double) random.nextInt(Integer.MAX_VALUE) * (vanillaArray.length + moddedWeightTotal) / Integer.MAX_VALUE;
		if (reqWeightSum < vanillaArrayWeight) {
			info.setReturnValue(vanillaArray[(int) reqWeightSum]);
		}
		else {
			List<BiomeEntry> moddedBiomes = InternalBiomeData.getOverworldBaseBiomes().get(climate);
			info.setReturnValue(moddedBiomes.get(InternalBiomeUtils.searchForBiome(reqWeightSum, vanillaArrayWeight, moddedWeightTotal, moddedBiomes)).getRawId());
		}
	}

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/biome/layer/SetBaseBiomesLayer;SNOWY_BIOMES:[I"), method = "sample", cancellable = true)
	private void injectSnowyBiomes(LayerRandomnessSource random, int value, CallbackInfoReturnable<Integer> info) {
		int[] vanillaArray = SNOWY_BIOMES;
		OverworldClimate climate = OverworldClimate.SNOWY;
		Double mwt = InternalBiomeData.getOverworldModdedWeightTotals().get(climate);
		if (mwt == null) {
			return;
		}
		double moddedWeightTotal = mwt;
		int vanillaArrayWeight = vanillaArray.length;
		double reqWeightSum = (double) random.nextInt(Integer.MAX_VALUE) * (vanillaArray.length + moddedWeightTotal) / Integer.MAX_VALUE;
		if (reqWeightSum < vanillaArrayWeight) {
			info.setReturnValue(vanillaArray[(int) reqWeightSum]);
		}
		else {
			List<BiomeEntry> moddedBiomes = InternalBiomeData.getOverworldBaseBiomes().get(climate);
			info.setReturnValue(moddedBiomes.get(InternalBiomeUtils.searchForBiome(reqWeightSum, vanillaArrayWeight, moddedWeightTotal, moddedBiomes)).getRawId());
		}
	}

	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/world/biome/layer/SetBaseBiomesLayer;COOL_BIOMES:[I"), method = "sample", cancellable = true)
	private void injectCoolBiomes(LayerRandomnessSource random, int value, CallbackInfoReturnable<Integer> info) {
		int[] vanillaArray = COOL_BIOMES;
		OverworldClimate climate = OverworldClimate.COOL;
		Double mwt = InternalBiomeData.getOverworldModdedWeightTotals().get(climate);
		if (mwt == null) {
			return;
		}
		double moddedWeightTotal = mwt;
		int vanillaArrayWeight = vanillaArray.length;
		double reqWeightSum = (double) random.nextInt(Integer.MAX_VALUE) * (vanillaArray.length + moddedWeightTotal) / Integer.MAX_VALUE;
		if (reqWeightSum < vanillaArrayWeight) {
			info.setReturnValue(vanillaArray[(int) reqWeightSum]);
		}
		else {
			List<BiomeEntry> moddedBiomes = InternalBiomeData.getOverworldBaseBiomes().get(climate);
			info.setReturnValue(moddedBiomes.get(InternalBiomeUtils.searchForBiome(reqWeightSum, vanillaArrayWeight, moddedWeightTotal, moddedBiomes)).getRawId());
		}
	}

	@Inject(at = @At("RETURN"), method = "sample", cancellable = true)
	private void transformVariants(LayerRandomnessSource random, int value, CallbackInfoReturnable<Integer> info) {
		Biome biome = Registry.BIOME.get(value);
		Map<Biome, VariantTransformer> overworldVariantTransformers = InternalBiomeData.getOverworldVariantTransformers();
		if (overworldVariantTransformers.containsKey(biome)) {
			info.setReturnValue(Registry.BIOME.getRawId(overworldVariantTransformers.get(biome).transformBiome(biome, random)));
		}
	}

}
