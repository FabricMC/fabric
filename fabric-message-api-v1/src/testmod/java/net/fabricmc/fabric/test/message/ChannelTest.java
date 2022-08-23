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

package net.fabricmc.fabric.test.message;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.MessageChannels;

public class ChannelTest {
	private ChannelTest() {
	}

	public static void test() {
		CommandRegistrationCallback.EVENT.register(ChannelTest::registerCommands);
	}

	private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
		LiteralCommandNode<ServerCommandSource> root = CommandManager
				.literal("fabric-message-api-v1-test")
				.build();

		dispatcher.getRoot().addChild(root);

		root.addChild(
				CommandManager
						.literal("set")
						.then(
								CommandManager.argument("identifier", IdentifierArgumentType.identifier()).executes(ChannelTest::run)
						)
						.build()
		);
	}

	private static int run(CommandContext<ServerCommandSource> context) {
		Identifier identifier = IdentifierArgumentType.getIdentifier(context, "identifier");

		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();

		if (player == null) {
			source.sendError(Text.of("You must be a player in order to run this command"));
			return 0;
		}

		MessageChannels.setChannel(player.getUuid(), identifier);

		source.sendFeedback(Text.of("Successfully set channel"), false);

		return 1;
	}
}
