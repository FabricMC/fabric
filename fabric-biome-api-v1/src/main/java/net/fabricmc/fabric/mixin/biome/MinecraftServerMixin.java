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

import java.net.Proxy;

import com.mojang.datafixers.DataFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.fabric.impl.biome.NetherBiomeData;

// Priority set just below biome modification mixin's
@Mixin(value = MinecraftServer.class, priority = 990)
public class MinecraftServerMixin {
	@Shadow
	private DynamicRegistryManager.Immutable getRegistryManager() {
		throw new AssertionError();
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void addNetherBiomes(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
		Registry<DimensionOptions> registry = getRegistryManager().get(RegistryKeys.DIMENSION);

		registry.stream().forEach(dimensionOptions -> NetherBiomeData.modifyBiomeSource(getRegistryManager().getWrapperOrThrow(RegistryKeys.BIOME_WORLDGEN), dimensionOptions.chunkGenerator().getBiomeSource()));
	}
}
