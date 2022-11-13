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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.SaveProperties;

import net.fabricmc.fabric.impl.biome.NetherBiomeData;

// Priority set just below biome modification mixin's
@Mixin(value = MinecraftServer.class, priority = 990)
public class MinecraftServerMixin {
	@Shadow
	@Final
	protected SaveProperties saveProperties;

	@Shadow
	@Final
	private DynamicRegistryManager.Immutable registryManager;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void addNetherBiomes(CallbackInfo ci) {
		this.saveProperties.getGeneratorOptions().getDimensions().stream().forEach(dimensionOptions -> NetherBiomeData.modifyBiomeSource(this.registryManager.get(Registry.BIOME_KEY), dimensionOptions.getChunkGenerator().getBiomeSource()));
	}
}
