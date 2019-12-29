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

package net.fabricmc.fabric.mixin.levelgenerator;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

import net.fabricmc.fabric.impl.levelgenerator.FabricLevelGeneratorType;

@Mixin(OverworldDimension.class)
public abstract class MixinOverworldDimension extends Dimension {
	public MixinOverworldDimension(World world, DimensionType type, float f) {
		super(world, type, f);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Inject(method = "createChunkGenerator", at = @At("RETURN"), cancellable = true)
	private void createChunkGenerator(CallbackInfoReturnable<ChunkGenerator<? extends ChunkGeneratorConfig>> cir) {
		Pair<ChunkGeneratorType<?, ? extends ChunkGenerator<?>>, Function<World, BiomeSource>> supplier = FabricLevelGeneratorType.suppliers.get(world.getLevelProperties().getGeneratorType());

		// Skip if levelGenerator doesn't provide supplier
		if (supplier == null) return;

		ChunkGeneratorType chunkGeneratorType = supplier.getLeft();
		cir.setReturnValue(chunkGeneratorType.create(world, supplier.getRight().apply(world), chunkGeneratorType.createSettings()));
	}
}
