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

package net.fabricmc.fabric.mixin.registry.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.impl.registry.sync.trackers.vanilla.BlockInitTracker;
import net.fabricmc.loader.api.FabricLoader;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Unique
	private static final Logger FABRIC_LOGGER = LoggerFactory.getLogger(MinecraftServerMixin.class);

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"), method = "runServer")
	private void beforeSetupServer(CallbackInfo info) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			// Freeze the registries on the server
			FABRIC_LOGGER.debug("Freezing registries");
			BuiltinRegistries.REGISTRIES.freeze();
			for (Registry<?> registry : BuiltinRegistries.REGISTRIES) {
				((SimpleRegistry<?>) registry).freeze();
			}

			Registry.freezeRegistries();
			BlockInitTracker.postFreeze();
		}
	}
}
