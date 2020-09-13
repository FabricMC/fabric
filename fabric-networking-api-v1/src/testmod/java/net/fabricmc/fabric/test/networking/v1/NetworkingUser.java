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

package net.fabricmc.fabric.test.networking.v1;

import java.util.concurrent.FutureTask;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerNetworking;
import net.fabricmc.fabric.impl.networking.NetworkingDetails;

public final class NetworkingUser implements ModInitializer {
	public static final String ID = "fabric-networking-api-v1-testmod";
	public static final Identifier TEST_CHANNEL = id("test_channel");
	private static final Logger LOGGER = LogManager.getLogger(ID);

	public static Identifier id(String name) {
		return new Identifier(ID, name);
	}

	public static void sendToTestChannel(ServerPlayerEntity player, String stuff) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeText(new LiteralText(stuff));
		ServerNetworking.getPlaySender(player).sendPacket(TEST_CHANNEL, buf);
		NetworkingDetails.LOGGER.info("Sent custom payload packet in {}", TEST_CHANNEL);
	}

	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		LOGGER.info("Registering test command");
		dispatcher.register(
				CommandManager.literal("networktestcommand")
						.then(CommandManager.argument("stuff", StringArgumentType.string())
								.executes(ctx -> {
									String stuff = StringArgumentType.getString(ctx, "stuff");
									sendToTestChannel(ctx.getSource().getPlayer(), stuff);
									return Command.SINGLE_SUCCESS;
								})
						)
		);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Hello from networking user!");
		ServerConnectionEvents.LOGIN_QUERY_START.register(this::onLoginStart);
		// login delaying example
		ServerNetworking.getLoginReceiver().register(TEST_CHANNEL, (handler, server, sender, buf, understood, synchronizer) -> {
			if (understood) {
				FutureTask<?> future = new FutureTask<>(() -> {
					for (int i = 0; i <= 10; i++) {
						Thread.sleep(300);
						LOGGER.info("Delayed login for number {} 300 milliseconds", i);
					}

					return null;
				});

				Util.getMainWorkerExecutor().execute(future);
				synchronizer.waitFor(future);
			}
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			NetworkingUser.registerCommand(dispatcher);
		});
	}

	private void onLoginStart(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, ServerNetworking.LoginSynchronizer synchronizer) {
		sender.sendPacket(TEST_CHANNEL, PacketByteBufs.empty()); // dummy packet
	}
}
