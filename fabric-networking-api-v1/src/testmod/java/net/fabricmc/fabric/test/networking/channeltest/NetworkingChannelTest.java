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

package net.fabricmc.fabric.test.networking.channeltest;

import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.command.argument.IdentifierArgumentType.getIdentifier;
import static net.minecraft.command.argument.IdentifierArgumentType.identifier;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class NetworkingChannelTest implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			final LiteralCommandNode<ServerCommandSource> channelTestCommand = literal("network_channel_test").build();

			// Info
			{
				final LiteralCommandNode<ServerCommandSource> info = literal("info")
						.executes(context -> infoCommand(context, context.getSource().getPlayer()))
						.build();

				final ArgumentCommandNode<ServerCommandSource, EntitySelector> player = argument("player", player())
						.executes(context -> infoCommand(context, getPlayer(context, "player")))
						.build();

				info.addChild(player);
				channelTestCommand.addChild(info);
			}

			// Register
			{
				final LiteralCommandNode<ServerCommandSource> register = literal("register")
						.then(argument("channel", identifier())
								.executes(context -> registerChannel(context, context.getSource().getPlayer())))
						.build();

				channelTestCommand.addChild(register);
			}

			// Unregister
			{
				final LiteralCommandNode<ServerCommandSource> unregister = literal("unregister")
						.then(argument("channel", identifier()).suggests(NetworkingChannelTest::suggestReceivableChannels)
								.executes(context -> unregisterChannel(context, context.getSource().getPlayer())))
						.build();

				channelTestCommand.addChild(unregister);
			}

			dispatcher.getRoot().addChild(channelTestCommand);
		});
	}

	private static CompletableFuture<Suggestions> suggestReceivableChannels(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		final ServerPlayerEntity player = context.getSource().getPlayer();

		return CommandSource.suggestIdentifiers(ServerPlayNetworking.getReceived(player), builder);
	}

	private static int registerChannel(CommandContext<ServerCommandSource> context, ServerPlayerEntity executor) throws CommandSyntaxException {
		final Identifier channel = getIdentifier(context, "channel");

		if (ServerPlayNetworking.getReceived(executor).contains(channel)) {
			throw new SimpleCommandExceptionType(new LiteralText(String.format("Cannot register channel %s twice for server player", channel))).create();
		}

		ServerPlayNetworking.registerReceiver(executor.networkHandler, channel, (server, player, handler, buf, sender) -> {
			System.out.printf("Received packet on channel %s%n", channel);
		});

		context.getSource().sendFeedback(new LiteralText(String.format("Registered channel %s for %s", channel, executor.getEntityName())), false);

		return 1;
	}

	private static int unregisterChannel(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
		final Identifier channel = getIdentifier(context, "channel");

		if (!ServerPlayNetworking.getReceived(player).contains(channel)) {
			throw new SimpleCommandExceptionType(new LiteralText("Cannot unregister channel the server player entity cannot recieve packets on")).create();
		}

		ServerPlayNetworking.unregisterReceiver(player.networkHandler, channel);
		context.getSource().sendFeedback(new LiteralText(String.format("Unregistered channel %s for %s", getIdentifier(context, "channel"), player.getEntityName())), false);

		return 1;
	}

	private static int infoCommand(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
		// TODO:

		return 1;
	}
}
