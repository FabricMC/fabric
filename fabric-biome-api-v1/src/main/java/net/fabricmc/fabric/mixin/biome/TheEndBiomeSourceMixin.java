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

import java.util.Set;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.class_7871;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import net.fabricmc.fabric.impl.biome.TheEndBiomeData;

@Mixin(TheEndBiomeSource.class)
public class TheEndBiomeSourceMixin extends BiomeSourceMixin {
	@Unique
	private Supplier<TheEndBiomeData.Overrides> overrides;

	@Unique
	private boolean biomeSetModified = false;

	@Inject(method = "method_46680", at = @At("HEAD"))
	private static void rememberLookup(class_7871<Biome> biomes, CallbackInfoReturnable<?> ci) {
		TheEndBiomeData.biomeRegistry.set(biomes);
	}

	@Inject(method = "method_46680", at = @At("TAIL"))
	private static void clearLookup(class_7871<Biome> biomes, CallbackInfoReturnable<?> ci) {
		TheEndBiomeData.biomeRegistry.remove();
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(RegistryEntry<Biome> centerBiome, RegistryEntry<Biome> highlandsBiome, RegistryEntry<Biome> midlandsBiome, RegistryEntry<Biome> smallIslandsBiome, RegistryEntry<Biome> barrensBiome, CallbackInfo ci) {
		overrides = Suppliers.memoize(() -> {
			var biomes = TheEndBiomeData.biomeRegistry.get();
			if (biomes == null) {
				throw new IllegalStateException("Biome registry not set by Mixin");
			}
			return TheEndBiomeData.createOverrides(biomes);
		});
	}

	@Inject(method = "getBiome", at = @At("RETURN"), cancellable = true)
	private void getWeightedEndBiome(int biomeX, int biomeY, int biomeZ, MultiNoiseUtil.MultiNoiseSampler noise, CallbackInfoReturnable<RegistryEntry<Biome>> cir) {
		cir.setReturnValue(overrides.get().pick(biomeX, biomeY, biomeZ, noise, cir.getReturnValue()));
	}

	@Override
	protected void fabric_modifyBiomeSet(Set<RegistryEntry<Biome>> biomes) {
		if (!biomeSetModified) {
			biomeSetModified = true;
			biomes.addAll(overrides.get().customBiomes);
		}
	}
}
