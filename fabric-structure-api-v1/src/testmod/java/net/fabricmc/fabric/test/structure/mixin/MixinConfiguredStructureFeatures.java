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

package net.fabricmc.fabric.test.structure.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;

import net.fabricmc.fabric.test.structure.StructureTest;

@Mixin(ConfiguredStructureFeatures.class)
public abstract class MixinConfiguredStructureFeatures {
	@Shadow
	private static void register(ConfiguredStructureFeatures.class_6896 arg, RegistryEntry<? extends ConfiguredStructureFeature<?, ?>> arg2, RegistryKey<Biome> biome) {
		throw new AssertionError();
	}

	@Inject(method = "registerAll", at = @At("TAIL"))
	private static void addStructuresToBiomes(ConfiguredStructureFeatures.class_6896 arg, CallbackInfo ci) {
		register(arg, StructureTest.CONFIGURED_STRUCTURE, BiomeKeys.PLAINS);
	}
}
