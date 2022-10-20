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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionOptions;

import net.fabricmc.fabric.impl.biome.NetherBiomeData;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Shadow
	private DynamicRegistryManager.Immutable getRegistryManager() {
		throw new AssertionError();
	}

	@Inject(method = "createWorlds", at = @At("HEAD"))
	private void addNetherBiomes(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
		// This is the last point where we can safely modify worldgen related things
		// plus, this is server-side only, and DRM is easily accessible
		// please blame Mojang for using dynamic registry
		Registry<DimensionOptions> registry = getRegistryManager().get(Registry.DIMENSION_KEY);

		registry.stream().forEach(dimensionOptions -> NetherBiomeData.modifyBiomeSource(getRegistryManager().get(Registry.BIOME_KEY), dimensionOptions.chunkGenerator().getBiomeSource()));
	}
}
