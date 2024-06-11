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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.EntitySelectorOptionRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class CommandTest implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandTest.class);
	static final Identifier SELECTOR_ID = Identifier.of("fabric-command-api-v2-testmod", "min_health");
	private static final SimpleCommandExceptionType WRONG_SIDE_SHOULD_BE_INTEGRATED = new SimpleCommandExceptionType(Text.literal("This command was registered incorrectly. Should only be present on an integrated server but was ran on a dedicated server!"));
	private static final SimpleCommandExceptionType WRONG_SIDE_SHOULD_BE_DEDICATED = new SimpleCommandExceptionType(Text.literal("This command was registered incorrectly. Should only be present on an dedicated server but was ran on an integrated server!"));

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			// A command that exists on both types of servers
			dispatcher.register(literal("fabric_common_test_command").executes(this::executeCommonCommand));

			if (environment.dedicated) {
				// The command here should only be present on a dedicated server
				dispatcher.register(literal("fabric_dedicated_test_command").executes(this::executeDedicatedCommand));
			}

			if (environment.integrated) {
				// The command here should only be present on an integrated server
				dispatcher.register(literal("fabric_integrated_test_command").executes(this::executeIntegratedCommand));
			}
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			// Verify the commands actually exist in the command dispatcher.
			final boolean dedicated = server.isDedicated();
			final RootCommandNode<ServerCommandSource> rootNode = server.getCommandManager().getDispatcher().getRoot();

			// Now we climb the tree
			final CommandNode<ServerCommandSource> fabricCommonTestCommand = rootNode.getChild("fabric_common_test_command");
			final CommandNode<ServerCommandSource> fabricDedicatedTestCommand = rootNode.getChild("fabric_dedicated_test_command");
			final CommandNode<ServerCommandSource> fabricIntegratedTestCommand = rootNode.getChild("fabric_integrated_test_command");

			// Verify the common command exists
			if (fabricCommonTestCommand == null) {
				throw new AssertionError("Expected to find 'fabric_common_test_command' on the server's command dispatcher. But it was not found.");
			}

			if (dedicated) {
				// Verify we don't have the integrated command
				if (fabricIntegratedTestCommand != null) {
					throw new AssertionError("Found 'fabric_integrated_test_command' on the dedicated server's command dispatcher. This should not happen!");
				}

				// Verify we have the dedicated command
				if (fabricDedicatedTestCommand == null) {
					throw new AssertionError("Expected to find 'fabric_dedicated_test_command' on the dedicated server's command dispatcher. But it was not found.");
				}
			} else {
				// Verify we don't have the dedicated command
				if (fabricDedicatedTestCommand != null) {
					throw new AssertionError("Found 'fabric_dedicated_test_command' on the integrated server's command dispatcher. This should not happen!");
				}

				// Verify we have the integrated command
				if (fabricIntegratedTestCommand == null) {
					throw new AssertionError("Expected to find 'fabric_integrated_test_command' on the integrated server's command dispatcher. But it was not found.");
				}
			}

			// Success!
			CommandTest.LOGGER.info("The command tests have passed! Please make sure you execute the three commands for extra safety.");
		});

		EntitySelectorOptionRegistry.registerNonRepeatable(
				SELECTOR_ID,
				Text.literal("Minimum entity health"),
				(reader) -> {
					final float minHealth = reader.getReader().readFloat();

					if (minHealth > 0) {
						reader.addPredicate((entity) -> entity instanceof LivingEntity livingEntity && livingEntity.getHealth() >= minHealth);
					}
				}
		);
	}

	private int executeCommonCommand(CommandContext<ServerCommandSource> context) {
		final ServerCommandSource source = context.getSource();
		source.sendFeedback(() -> Text.literal("Common test command is working."), false);
		source.sendFeedback(() -> Text.literal("Server Is Dedicated: " + source.getServer().isDedicated()), false);

		return 1;
	}

	private int executeDedicatedCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();

		if (!source.getServer().isDedicated()) {
			throw WRONG_SIDE_SHOULD_BE_DEDICATED.create();
		}

		source.sendFeedback(() -> Text.literal("Dedicated test command is working."), false);
		source.sendFeedback(() -> Text.literal("Server Is Dedicated: " + source.getServer().isDedicated()), false);

		return 1;
	}

	private int executeIntegratedCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();

		if (source.getServer().isDedicated()) {
			throw WRONG_SIDE_SHOULD_BE_INTEGRATED.create();
		}

		source.sendFeedback(() -> Text.literal("Integrated test command is working."), false);
		source.sendFeedback(() -> Text.literal("Server Is Integrated: " + !source.getServer().isDedicated()), false);

		return 1;
	}
}
