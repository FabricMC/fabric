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

package net.fabricmc.fabric.test.command;

import java.util.Arrays;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.ClientCommandManager;
import static net.fabricmc.fabric.api.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.command.v1.ClientCommandManager.literal;

public class ClientCommandTest implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(ClientCommandTest.class);

	@Override
	public void onInitialize() {
		ClientCommandManager.INSTANCE.getDispatcher().register(
				literal("command_a")
						.then(literal("command_b")
								.then(argument("arg1", IntegerArgumentType.integer())
										.executes(context -> {
											sendMessage(String.valueOf(context.getArgument("arg1", Integer.class)));
											return 0;
										})))
						.then(literal("command_c")
								.requires(source -> true))
						.then(literal("command_d")
								.requires(source -> false))
						.then(literal("command_e")
								.executes(context -> {
									throw new RuntimeException("command_e: RuntimeException");
								}))
						.then(literal("command_f")
								.executes(context -> {
									throw new CommandException(new LiteralText("command_f: CommandException"));
								}))
						.then(literal("command_g")
								.executes(context -> {
									throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();
								})));

		sendMessage("/command_a command_b 1234");

		sendMessage("----------------------------");

		sendMessage("/command_a command_e");

		sendMessage("----------------------------");

		sendMessage("/command_a command_f");

		sendMessage("----------------------------");

		sendMessage("/command_a command_g");

		sendMessage("----------------------------");

		CommandNode<CommandSource> command_b_arg1 = MinecraftClient.getInstance().player.networkHandler
				.getCommandDispatcher().findNode(Arrays.asList("command_a", "command_b", "arg1"));

		if (command_b_arg1 == null) {
			throw new AssertionError("Expected to find 'arg1' on the networkHandler's command dispatcher. But it was not found.");
		}

		CommandNode<CommandSource> command_c = MinecraftClient.getInstance().player.networkHandler
				.getCommandDispatcher().findNode(Arrays.asList("command_a", "command_c"));

		if (command_c == null) {
			throw new AssertionError("Expected to find 'command_c' on the networkHandler's command dispatcher. But it was not found.");
		}

		CommandNode<CommandSource> command_d = MinecraftClient.getInstance().player.networkHandler
				.getCommandDispatcher().findNode(Arrays.asList("command_a", "command_d"));

		if (command_d != null) {
			throw new AssertionError("Found 'command_d' on the networkHandler's command dispatcher. This should not happen!");
		}

		CommandNode<CommandSource> command_e = MinecraftClient.getInstance().player.networkHandler
				.getCommandDispatcher().findNode(Arrays.asList("command_a", "command_e"));

		if (command_e == null) {
			throw new AssertionError("Expected to find 'command_e' on the networkHandler's command dispatcher. But it was not found.");
		}

		CommandNode<CommandSource> command_f = MinecraftClient.getInstance().player.networkHandler
				.getCommandDispatcher().findNode(Arrays.asList("command_a", "command_f"));

		if (command_f == null) {
			throw new AssertionError("Expected to find 'command_f' on the networkHandler's command dispatcher. But it was not found.");
		}

		CommandNode<CommandSource> command_g = MinecraftClient.getInstance().player.networkHandler
				.getCommandDispatcher().findNode(Arrays.asList("command_a", "command_g"));

		if (command_g == null) {
			throw new AssertionError("Expected to find 'command_g' on the networkHandler's command dispatcher. But it was not found.");
		}

		LOGGER.info("The command tests have passed!");
	}

	private void sendMessage(String message) {
		MinecraftClient.getInstance().player.sendChatMessage(message);
	}
}
