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

package net.fabricmc.fabric.mixin.command;

import com.mojang.brigadier.CommandDispatcher;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MixinMinecraftDedicatedServer {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Inject(method = "setupServer", at = @At("HEAD"))
	private void setupServer(CallbackInfoReturnable<Boolean> info) {
		MinecraftDedicatedServer server = ((MinecraftDedicatedServer) (Object) this);
		CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();

		CommandRegistrationCallback.EVENT.invoker().register(dispatcher, true);

		// Now find ambiguities after commands have loaded.
		server.getCommandManager().getDispatcher().findAmbiguities((parent, child, sibling, collection) -> {
			LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", dispatcher.getPath(child), dispatcher.getPath(sibling), collection);
		});
	}
}
