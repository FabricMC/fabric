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
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.class_7871;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import net.fabricmc.fabric.impl.biome.TheEndBiomeData;

@Mixin(TheEndBiomeSource.class)
public class TheEndBiomeSourceMixin extends BiomeSourceMixin {
	@Shadow
	@Mutable
	@Final
	static Codec<TheEndBiomeSource> CODEC;

	@Unique
	private Supplier<TheEndBiomeData.Overrides> overrides;

	@Unique
	private boolean biomeSetModified = false;

	/**
	 * Modifies the codec, so it calls the static factory method that gives us access to the
	 * full biome registry instead of just the pre-defined biomes that vanilla uses.
	 */
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void modifyCodec(CallbackInfo ci) {
		CODEC = RecordCodecBuilder.create((instance) -> {
			return instance.group(RegistryOps.method_46636(Registry.BIOME_KEY)).apply(instance, instance.stable(TheEndBiomeSource::method_46680));
		});
	}

	/**
	 * Captures the biome registry at the beginning of the static factory method to allow access to it in the
	 * constructor.
	 */
	@Inject(method = "method_46680", at = @At("HEAD"))
	private static void rememberLookup(class_7871<Biome> biomes, CallbackInfoReturnable<?> ci) {
		TheEndBiomeData.biomeRegistry.set(biomes);
	}

	/**
	 * Frees up the captured biome registry.
	 */
	@Inject(method = "method_46680", at = @At("TAIL"))
	private static void clearLookup(class_7871<Biome> biomes, CallbackInfoReturnable<?> ci) {
		TheEndBiomeData.biomeRegistry.remove();
	}

	/**
	 * Uses the captured biome registry to set up the modded end biomes.
	 */
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(RegistryEntry<Biome> centerBiome, RegistryEntry<Biome> highlandsBiome, RegistryEntry<Biome> midlandsBiome, RegistryEntry<Biome> smallIslandsBiome, RegistryEntry<Biome> barrensBiome, CallbackInfo ci) {
		class_7871<Biome> biomes = TheEndBiomeData.biomeRegistry.get();
		if (biomes == null) {
			throw new IllegalStateException("Biome registry not set by Mixin");
		}
		overrides = Suppliers.memoize(() -> {
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
