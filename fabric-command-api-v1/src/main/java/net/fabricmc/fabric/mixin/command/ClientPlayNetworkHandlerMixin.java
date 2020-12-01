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
import com.mojang.brigadier.tree.CommandNode;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;

import net.fabricmc.fabric.api.command.v1.ClientCommandManager;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
	@Shadow
	@Final
	private ClientCommandSource commandSource;
	@Shadow
	@Final
	private CommandDispatcher<CommandSource> commandDispatcher;

	@Inject(method = "onCommandTree", at = @At("RETURN"))
	private void onOnCommandTree(CallbackInfo callbackInfo) {
		ClientCommandManager.INSTANCE.getDispatcher().getRoot().getChildren().forEach(node -> {
			if (node.canUse(commandSource)) {
				commandDispatcher.getRoot().addChild(nonCanNotUse(node, commandSource));
			}
		});
	}

	private static <T> CommandNode<T> nonCanNotUse(CommandNode<T> root, T source) {
		CommandNode<T> newRoot = root.createBuilder().build();

		for (CommandNode<T> node : root.getChildren()) {
			if (node.canUse(source)) {
				newRoot.addChild(nonCanNotUse(node, source));
			}
		}

		return newRoot;
	}
}
