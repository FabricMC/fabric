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

package net.fabricmc.fabric.test.networking.play;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.test.networking.NetworkingTestmods;

public final class NetworkingPlayPacketTest implements ModInitializer {
	public static final Identifier TEST_CHANNEL = NetworkingTestmods.id("test_channel");

	public static void sendToTestChannel(ServerPlayerEntity player, String stuff) {
		ServerPlayNetworking.send(player, new OverlayPacket(Text.literal(stuff)));
		NetworkingTestmods.LOGGER.info("Sent custom payload packet in {}", TEST_CHANNEL);
	}

	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		NetworkingTestmods.LOGGER.info("Registering test command");

		dispatcher.register(literal("networktestcommand")
				.then(argument("stuff", string()).executes(ctx -> {
					String stuff = StringArgumentType.getString(ctx, "stuff");
					sendToTestChannel(ctx.getSource().getPlayer(), stuff);
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("bundled").executes(ctx -> {
					PacketByteBuf buf1 = PacketByteBufs.create();
					buf1.writeText(Text.literal("bundled #1"));
					PacketByteBuf buf2 = PacketByteBufs.create();
					buf2.writeText(Text.literal("bundled #2"));

					BundleS2CPacket packet = new BundleS2CPacket(List.of(
							ServerPlayNetworking.createS2CPacket(TEST_CHANNEL, buf1),
							ServerPlayNetworking.createS2CPacket(TEST_CHANNEL, buf2)));
					ctx.getSource().getPlayer().networkHandler.sendPacket(packet);
					return Command.SINGLE_SUCCESS;
				})));
	}

	@Override
	public void onInitialize() {
		NetworkingTestmods.LOGGER.info("Hello from networking user!");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			NetworkingPlayPacketTest.registerCommand(dispatcher);
		});
	}

	public record OverlayPacket(Text message) implements FabricPacket {
		public static final PacketType<OverlayPacket> PACKET_TYPE = PacketType.create(TEST_CHANNEL, OverlayPacket::new);

		public OverlayPacket(PacketByteBuf buf) {
			this(buf.readText());
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeText(this.message);
		}

		@Override
		public PacketType<?> getType() {
			return PACKET_TYPE;
		}
	}
}
