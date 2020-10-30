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

package net.fabricmc.fabric.test.command.client;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.text.LiteralText;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientArgumentBuilders;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;

@Environment(EnvType.CLIENT)
public final class ClientCommandTest implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DynamicCommandExceptionType IS_NULL = new DynamicCommandExceptionType(x -> new LiteralText("The " + x + " is null"));

	private boolean hasTested = false;

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register(dispatcher -> {
			dispatcher.register(ClientArgumentBuilders.literal("test_client_command").executes(context -> {
				context.getSource().sendFeedback(new LiteralText("This is a client command!"));

				if (context.getSource().getClient() == null) {
					throw IS_NULL.create("client");
				}

				if (context.getSource().getWorld() == null) {
					throw IS_NULL.create("world");
				}

				if (context.getSource().getPlayer() == null) {
					throw IS_NULL.create("player");
				}

				return 0;
			}));

			// Command with argument
			dispatcher.register(ClientArgumentBuilders.literal("test_client_command_with_arg").then(
					ClientArgumentBuilders.argument("number", DoubleArgumentType.doubleArg()).executes(context -> {
						double number = DoubleArgumentType.getDouble(context, "number");

						// Test error formatting
						context.getSource().sendError(new LiteralText("Your number is " + number));

						return 0;
					})
			));
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (hasTested) {
				return;
			}

			RootCommandNode<FabricClientCommandSource> rootNode = ClientCommandInternals.getDispatcher().getRoot();

			// We climb the tree again
			CommandNode<FabricClientCommandSource> test_client_command = rootNode.getChild("test_client_command");
			CommandNode<FabricClientCommandSource> test_client_command_with_arg = rootNode.getChild("test_client_command_with_arg");

			if (test_client_command == null) {
				throw new AssertionError("Expected to find 'test_client_command' on the client command dispatcher. But it was not found.");
			}

			if (test_client_command_with_arg == null) {
				throw new AssertionError("Expected to find 'test_client_command_with_arg' on the client command dispatcher. But it was not found.");
			}

			CommandNode<FabricClientCommandSource> number_arg = test_client_command_with_arg.getChild("number");

			if (number_arg == null) {
				throw new AssertionError("Expected to find 'number' as a child of 'test_client_command_with_arg' on the client command dispatcher. But it was not found.");
			}

			hasTested = true;
			LOGGER.info("The client command tests have passed! Please make sure you execute the two commands for extra safety.");
		});
	}
}
