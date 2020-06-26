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

package net.fabricmc.fabric.mixin.dimension;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.world.SaveProperties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Shadow
	@Final
	protected SaveProperties saveProperties;

	// Replace the ChunkGenerator with the current seed
	@Inject(method = "createWorlds", at = @At("HEAD"))
	private void onCreateWorlds(
			WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci
	) {
		GeneratorOptions generatorOptions = saveProperties.getGeneratorOptions();

		SimpleRegistry<DimensionOptions> dimensionMap = generatorOptions.getDimensionMap();

		dimensionMap.getIds().forEach(dimensionId -> {
			if (isVanillaDimension(dimensionId)) {
				return;
			}

			DimensionOptions dimensionOptions = dimensionMap.get(dimensionId);

			// Vanilla datapacks can only use vanilla chunk generators
			if (isVanillaChunkGenerator(dimensionOptions.chunkGenerator)) {
				return;
			}

			dimensionOptions.chunkGenerator = dimensionOptions.chunkGenerator.withSeed(generatorOptions.getSeed());
		});
	}

	@Unique
	private static boolean isVanillaDimension(Identifier identifier) {
		return identifier.equals(World.OVERWORLD.getValue())
				|| identifier.equals(World.NETHER.getValue())
				|| identifier.equals(World.END.getValue());
	}

	@Unique
	private static boolean isVanillaChunkGenerator(ChunkGenerator chunkGenerator) {
		Class<? extends ChunkGenerator> type = chunkGenerator.getClass();
		return type == SurfaceChunkGenerator.class
				|| type == FlatChunkGenerator.class
				|| type == DebugChunkGenerator.class;
	}
}
