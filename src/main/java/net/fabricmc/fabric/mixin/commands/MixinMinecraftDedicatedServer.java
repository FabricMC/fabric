/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.commands;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.DataFixer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.UserCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.net.Proxy;
import java.util.function.Consumer;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MixinMinecraftDedicatedServer extends MinecraftServer {

	public MixinMinecraftDedicatedServer(File file_1, Proxy proxy_1, DataFixer dataFixer_1, ServerCommandManager serverCommandManager_1, YggdrasilAuthenticationService yggdrasilAuthenticationService_1, MinecraftSessionService minecraftSessionService_1, GameProfileRepository gameProfileRepository_1, UserCache userCache_1, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory_1, String string_1) {
		super(file_1, proxy_1, dataFixer_1, serverCommandManager_1, yggdrasilAuthenticationService_1, minecraftSessionService_1, gameProfileRepository_1, userCache_1, worldGenerationProgressListenerFactory_1, string_1);
	}

	@Inject(method = "setupServer", at = @At("HEAD"))
	private void setupServer(CallbackInfoReturnable<Boolean> info){
		//Load none dedicated only commands and dedicated only commands
		for(Consumer<CommandDispatcher<ServerCommandSource>> entry : CommandRegistry.INSTANCE.entries(false)){
			entry.accept(getCommandManager().getDispatcher());
		}
		for(Consumer<CommandDispatcher<ServerCommandSource>> entry : CommandRegistry.INSTANCE.entries(true)){
			entry.accept(getCommandManager().getDispatcher());
		}
	}
}
