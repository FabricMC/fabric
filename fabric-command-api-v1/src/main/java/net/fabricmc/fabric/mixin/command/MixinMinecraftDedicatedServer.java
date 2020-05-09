/*
 * Copyright (c) 2016, 2017, 2018, 2019, 2020 FabricMC
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

import java.net.Proxy;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.UserCache;
import net.minecraft.class_5219;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MixinMinecraftDedicatedServer extends MinecraftServer {
	public MixinMinecraftDedicatedServer(LevelStorage.Session session, class_5219 arg, Proxy proxy, DataFixer dataFixer, CommandManager commandManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
		super(session, arg, proxy, dataFixer, commandManager, minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory);
	}

	@Inject(method = "setupServer", at = @At("HEAD"))
	private void setupServer(CallbackInfoReturnable<Boolean> info) {
		CommandRegistrationCallback.EVENT.invoker().register(getCommandManager().getDispatcher(), true);

		//Possibly call findAmbiguities here
	}
}
