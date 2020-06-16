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
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;

@Mixin(MultiNoiseBiomeSource.class)
public class MixinTheNetherDimension {
	@Inject(method = "method_28467", at = @At("RETURN"), cancellable = true)
	private static void method_28467(long l, CallbackInfoReturnable<MultiNoiseBiomeSource> info) {
		List<Biome> newList = new ArrayList<>(info.getReturnValue().method_28443());
		newList.addAll(InternalBiomeData.getNetherBiomes());
		MultiNoiseBiomeSource multiNoiseBiomeSource = new MultiNoiseBiomeSource(l, newList.stream().flatMap((biome) -> biome.streamNoises().map((point) -> Pair.of(point, biome))).collect(ImmutableList.toImmutableList()), Optional.of(MultiNoiseBiomeSource.Preset.NETHER));
		info.setReturnValue(multiNoiseBiomeSource);
	}
}
