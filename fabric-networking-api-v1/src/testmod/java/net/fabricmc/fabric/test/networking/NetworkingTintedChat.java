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

package net.fabricmc.fabric.test.networking;

import static net.fabricmc.fabric.test.networking.NetworkingV1Test.id;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.client.ClientNetworking;
import net.fabricmc.fabric.api.networking.v1.server.ServerNetworking;
import net.fabricmc.fabric.api.networking.v1.util.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.NetworkingDetails;

public final class NetworkingTintedChat {
	public static final Identifier CHANNEL = id("tinted_chat/v1");

	public static void sendToTestChannel(ServerPlayerEntity player, String stuff) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeText(new LiteralText(stuff));
		ServerNetworking.getPlaySender(player).sendPacket(CHANNEL, buf);
		NetworkingDetails.LOGGER.info("Sent custom payload packet in {}", CHANNEL);
	}

	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		NetworkingV1Test.LOGGER.info("Registering test command");
		dispatcher.register(
				CommandManager.literal("networktintedchat")
						.then(CommandManager.argument("stuff", StringArgumentType.string())
								.executes(ctx -> {
									String stuff = StringArgumentType.getString(ctx, "stuff");
									sendToTestChannel(ctx.getSource().getPlayer(), stuff);
									return Command.SINGLE_SUCCESS;
								})
						)
		);
	}

	@SuppressWarnings("unused") // entrypoint
	public static void init() {
		CommandRegistrationCallback.EVENT.register(NetworkingTintedChat::registerCommand);
	}

	@Environment(EnvType.CLIENT)
	@SuppressWarnings("unused") // entrypoint
	public static void clientInit() {
		ClientNetworking.getPlayReceiver().register(CHANNEL, (context, buf) -> {
			Text text = buf.readText();
			context.getEngine().send(() -> context.getEngine().inGameHud.setOverlayMessage(text, true));
		});
	}

	private NetworkingTintedChat() {
	}
}
