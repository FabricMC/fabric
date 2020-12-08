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

package net.fabricmc.fabric.mixin.command.client;

import com.mojang.brigadier.CommandDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;

@Mixin(ClientPlayNetworkHandler.class)
abstract class ClientPlayNetworkHandlerMixin {
	@Shadow
	private CommandDispatcher<CommandSource> commandDispatcher;

	@Shadow
	@Final
	private ClientCommandSource commandSource;

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Inject(method = "onCommandTree", at = @At("RETURN"))
	private void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo info) {
		// Add the commands to the vanilla dispatcher for completion.
		// It's done here because both the server and the client commands have
		// to be in the same dispatcher and completion results.
		ClientCommandInternals.addCommands((CommandDispatcher) commandDispatcher, (FabricClientCommandSource) commandSource);
	}
}
