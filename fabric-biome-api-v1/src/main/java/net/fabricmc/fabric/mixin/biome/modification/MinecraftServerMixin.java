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

package net.fabricmc.fabric.mixin.biome.modification;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.LevelProperties;

import net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
	@Final
	@Shadow
	protected SaveProperties saveProperties;

	@Shadow
	public abstract DynamicRegistryManager.Immutable getRegistryManager();

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void finalizeWorldGen(CallbackInfo ci) {
		if (!(saveProperties instanceof LevelProperties levelProperties)) {
			throw new RuntimeException("Incompatible SaveProperties passed to MinecraftServer: " + saveProperties);
		}

		BiomeModificationImpl.INSTANCE.finalizeWorldGen(getRegistryManager());
	}
}
