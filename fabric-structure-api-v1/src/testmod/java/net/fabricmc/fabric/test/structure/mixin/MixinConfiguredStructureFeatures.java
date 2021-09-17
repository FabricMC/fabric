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

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;

import net.fabricmc.fabric.test.structure.StructureTest;

@Mixin(ConfiguredStructureFeatures.class)
public class MixinConfiguredStructureFeatures {
	@Inject(method = "method_38571", at = @At("TAIL"))
	private static void addStructuresToBiomes(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> consumer, CallbackInfo ci) {
		consumer.accept(StructureTest.CONFIGURED_STRUCTURE, BiomeKeys.PLAINS);
	}
}
