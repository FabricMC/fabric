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

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.CommandDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

@Mixin(CommandManager.class)
public abstract class MixinCommandManager {
	/**
	 * @reason Add commands before ambiguities are calculated.
	 */
	@Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"), method = "<init>")
	private void fabric_addCommands(CommandDispatcher<ServerCommandSource> dispatcher, AmbiguityConsumer<ServerCommandSource> ambiguityConsumer, CommandManager.RegistrationEnvironment registrationEnvironment) {
		CommandRegistrationCallback.EVENT.invoker().register(dispatcher, registrationEnvironment == CommandManager.RegistrationEnvironment.DEDICATED);

		dispatcher.findAmbiguities(ambiguityConsumer);
	}
}
