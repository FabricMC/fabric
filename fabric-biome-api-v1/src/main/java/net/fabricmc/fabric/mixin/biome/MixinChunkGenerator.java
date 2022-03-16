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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.class_7138;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import net.fabricmc.fabric.impl.biome.TheEndBiomeData;

@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator {
	@Inject(method = "method_38267", at = @At("HEAD"))
	private void populateBiomes_lambda_head(Chunk chunk, class_7138 rng, CallbackInfoReturnable<Chunk> ci) {
		// capture seed so TheEndBiomeData.Overrides has it if it needs it
		TheEndBiomeData.Overrides.setSeed(rng.legacyLevelSeed());
	}
}
