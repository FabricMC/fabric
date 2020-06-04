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

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;

public class CommandTest implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(CommandTest.class);
	private static final SimpleCommandExceptionType WRONG_SIDE_SHOULD_BE_INTEGRATED = new SimpleCommandExceptionType(new LiteralText("This command was registered incorrectly. Should only be present on an integrated server but was ran on a dedicated server!"));
	private static final SimpleCommandExceptionType WRONG_SIDE_SHOULD_BE_DEDICATED = new SimpleCommandExceptionType(new LiteralText("This command was registered incorrectly. Should only be present on an dedicated server but was ran on an integrated server!"));

	private boolean hasTested = false;

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			// A command that exists on both types of servers
			dispatcher.register(literal("fabric_common_test_command").executes(this::executeCommonCommand));

			if (dedicated) {
				// The command here should only be present on a dedicated server
				dispatcher.register(literal("fabric_dedicated_test_command").executes(this::executeDedicatedCommand));
			} else {
				// The command here should only be present on a integrated server
				dispatcher.register(literal("fabric_integrated_test_command").executes(this::executeIntegratedCommand));
			}
		});

		// Use the ServerTickCallback to verify the commands actually exist in the command dispatcher.
		ServerTickCallback.EVENT.register(server -> {
			// Don't run the test more than once
			if (this.hasTested) {
				return;
			}

			final boolean dedicated = server.isDedicated();
			final RootCommandNode<ServerCommandSource> rootNode = server.getCommandManager().getDispatcher().getRoot();

			// Now we climb the tree
			final CommandNode<ServerCommandSource> fabric_common_test_command = rootNode.getChild("fabric_common_test_command");
			final CommandNode<ServerCommandSource> fabric_dedicated_test_command = rootNode.getChild("fabric_dedicated_test_command");
			final CommandNode<ServerCommandSource> fabric_integrated_test_command = rootNode.getChild("fabric_integrated_test_command");

			// Verify the common command exists
			if (fabric_common_test_command == null) {
				throw new AssertionError("Expected to find 'fabric_common_test_command' on the server's command dispatcher. But it was not found.");
			}

			if (dedicated) {
				// Verify we don't have the integrated command
				if (fabric_integrated_test_command != null) {
					throw new AssertionError("Found 'fabric_integrated_test_command' on the dedicated server's command dispatcher. This should not happen!");
				}

				// Verify we have the dedicated command
				if (fabric_dedicated_test_command == null) {
					throw new AssertionError("Expected to find 'fabric_dedicated_test_command' on the dedicated server's command dispatcher. But it was not found.");
				}
			} else {
				// Verify we don't have the dedicated command
				if (fabric_dedicated_test_command != null) {
					throw new AssertionError("Found 'fabric_dedicated_test_command' on the integrated server's command dispatcher. This should not happen!");
				}

				// Verify we have the integrated command
				if (fabric_integrated_test_command == null) {
					throw new AssertionError("Expected to find 'fabric_integrated_test_command' on the integrated server's command dispatcher. But it was not found.");
				}
			}

			// Success!
			this.hasTested = true;
			CommandTest.LOGGER.info("The command tests have passed! Please make sure you execute the three commands for extra safety.");
		});
	}

	private int executeCommonCommand(CommandContext<ServerCommandSource> context) {
		final ServerCommandSource source = context.getSource();
		source.sendFeedback(new LiteralText("Common test command is working."), false);
		source.sendFeedback(new LiteralText("Server Is Dedicated: " + source.getMinecraftServer().isDedicated()), false);

		return 1;
	}

	private int executeDedicatedCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();

		if (!source.getMinecraftServer().isDedicated()) {
			throw WRONG_SIDE_SHOULD_BE_DEDICATED.create();
		}

		source.sendFeedback(new LiteralText("Dedicated test command is working."), false);
		source.sendFeedback(new LiteralText("Server Is Dedicated: " + source.getMinecraftServer().isDedicated()), false);

		return 1;
	}

	private int executeIntegratedCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();

		if (source.getMinecraftServer().isDedicated()) {
			throw WRONG_SIDE_SHOULD_BE_INTEGRATED.create();
		}

		source.sendFeedback(new LiteralText("Integrated test command is working."), false);
		source.sendFeedback(new LiteralText("Server Is Integrated: " + !source.getMinecraftServer().isDedicated()), false);

		return 1;
	}
}
