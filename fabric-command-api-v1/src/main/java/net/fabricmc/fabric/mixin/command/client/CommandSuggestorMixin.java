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
import com.mojang.brigadier.StringReader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.server.command.CommandSource;

import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;

@Mixin(CommandSuggestor.class)
abstract class CommandSuggestorMixin {
	// Should be slashOptional, see https://github.com/FabricMC/yarn/issues/1744
	@Shadow
	@Final
	private boolean slashRequired;

	/* @Nullable */
	@Unique
	private CommandDispatcher<CommandSource> currentDispatcher = null;

	@Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;peek()C", remap = false))
	private char replacePrefix(StringReader reader) {
		char original = reader.peek();

		if (ClientCommandInternals.isPrefixUsed(original)) {
			// Replace any custom prefixes with / so vanilla's check succeeds
			return '/';
		}

		return original;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Inject(method = "refresh", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;getText()Ljava/lang/String;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void checkPrefix(CallbackInfo info, String message) {
		if (message.isEmpty() || slashRequired) {
			// Prefix changes don't have to be done for empty messages
			// or command blocks (where the slash is optional).
			return;
		}

		char prefix = message.charAt(0);

		// Handle non-slash prefixes
		if (prefix != '/') {
			currentDispatcher = (CommandDispatcher) ClientCommandInternals.getDispatcher(prefix);
		}
	}

	/**
	 * Selects the command dispatcher to use for suggestions.
	 */
	@ModifyVariable(method = "refresh", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getCommandDispatcher()Lcom/mojang/brigadier/CommandDispatcher;"))
	private CommandDispatcher<CommandSource> modifyCommandDispatcher(CommandDispatcher<CommandSource> existing) {
		if (currentDispatcher != null) {
			return currentDispatcher;
		}

		return existing;
	}

	@Inject(method = "refresh", at = @At("RETURN"))
	private void onRefreshReturn(CallbackInfo info) {
		currentDispatcher = null;
	}
}
