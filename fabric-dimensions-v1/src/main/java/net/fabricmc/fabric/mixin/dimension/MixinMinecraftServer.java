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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	// Replace the ChunkGenerator with the Current Seed and Skip Vanilla Dimensions
	@Redirect(
			method = "createWorlds",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/gen/GeneratorOptions;getDimensionMap()Lnet/minecraft/util/registry/SimpleRegistry;"
			)
	)
	private SimpleRegistry<DimensionOptions> onGetDimensionMap(GeneratorOptions generatorOptions) {
		SimpleRegistry<DimensionOptions> dimensionMap = generatorOptions.getDimensionMap();

		dimensionMap.getIds().forEach(dimensionId -> {
			if (!isVanillaDimension(dimensionId)) {
				DimensionOptions dimensionOptions = dimensionMap.get(dimensionId);
				dimensionOptions.chunkGenerator =
						dimensionOptions.chunkGenerator.withSeed(generatorOptions.getSeed());
			}
		});

		return dimensionMap;
	}

	@Unique
	private static boolean isVanillaDimension(Identifier identifier) {
		return identifier.equals(World.OVERWORLD.getValue())
				|| identifier.equals(World.NETHER.getValue())
				|| identifier.equals(World.END.getValue());
	}
}
