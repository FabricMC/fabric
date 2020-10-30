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

package net.fabricmc.fabric.test.networking.channels;

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

import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

// TODO: WIP
public final class ChannelRegistrationTest implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(
					literal("fabric_channel_tests")
							.then(literal("register").then(argument("channel", identifier()).executes(this::executeRegisterCommand)))
							.then(literal("unregister").then(argument("channel", identifier()).suggests(this::suggestReceiverChannels).executes(this::executeUnregisterCommand)))
			);
		});
	}

	private CompletableFuture<Suggestions> suggestReceiverChannels(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();
		final ServerPlayerEntity player = source.getPlayer();

		return CommandSource.suggestIdentifiers(ServerPlayNetworking.getReceivers(player), builder);
	}

	private int executeRegisterCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();
		final ServerPlayerEntity player = source.getPlayer();

		final Identifier channel = getIdentifier(context, "channel");

		if (ServerPlayNetworking.canReceive(player, channel)) {
			throw new SimpleCommandExceptionType(new LiteralText(String.format("Player can already receive packets on channel %s", channel.toString())).styled(style -> style.withColor(Formatting.YELLOW))).create();
		}

		ServerPlayNetworking.register(channel, (handler, sender, server, buf) -> {});

		source.sendFeedback(new LiteralText(String.format("Registered channel %s", channel.toString())), false);

		return 1;
	}

	private int executeUnregisterCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();
		source.getPlayer(); // require player

		final Identifier channel = getIdentifier(context, "channel");

		ServerPlayNetworking.unregister(channel);

		source.sendFeedback(new LiteralText(String.format("Unregistered channel %s", channel.toString())).styled(style -> style.withColor(Formatting.YELLOW)), false);

		return 1;
	}
}
