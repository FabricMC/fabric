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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesLoader;

import net.fabricmc.fabric.impl.biome.InternalBiomeUtils;

@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin {
	@Shadow
	@Final
	private ServerPropertiesLoader propertiesLoader;

	@Inject(method = "setupServer", at = @At("HEAD"))
	public void setupServer(CallbackInfoReturnable<Boolean> cir) {
		InternalBiomeUtils.recreateChunkGenerators(propertiesLoader.getPropertiesHandler().generatorOptions);
	}
}
