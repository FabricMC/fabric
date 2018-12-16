/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.bugfix;

import net.minecraft.client.render.block.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiomeColors.class)
public class MixinBiomeColors {
	// Apparently, ExtendedBlockView.getBiome() is null sometimes. Huh?
	@Redirect(method = "colorAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ExtendedBlockView;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome;"))
	private static Biome getBiome(ExtendedBlockView view, BlockPos pos) {
		Biome biome = view.getBiome(pos);
		if (biome == null) {
			return Biomes.DEFAULT;
		} else {
			return biome;
		}
	}
}
