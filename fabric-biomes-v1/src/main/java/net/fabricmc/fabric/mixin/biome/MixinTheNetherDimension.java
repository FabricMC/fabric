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

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.TheNetherDimension;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;

@Mixin(TheNetherDimension.class)
public class MixinTheNetherDimension {
	@ModifyArg(method = "createChunkGenerator", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/MultiNoiseBiomeSourceConfig;withBiomes(Ljava/util/List;)Lnet/minecraft/world/biome/source/MultiNoiseBiomeSourceConfig;"))
	protected List<Biome> modifyNetherBiomes(List<Biome> list) {
		// the provided set is immutable, so we construct our own
		List<Biome> newList = new ArrayList<>(list);
		newList.addAll(InternalBiomeData.getNetherBiomes());
		return newList;
	}
}
