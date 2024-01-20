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

import java.util.UUID;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.test.networking.NetworkingTestmods;

public final class NetworkingPlayPacketTest implements ModInitializer {
	public static final Identifier TEST_CHANNEL = NetworkingTestmods.id("test_channel");
	private static final Identifier UNKNOWN_TEST_CHANNEL = NetworkingTestmods.id("unknown_test_channel");
	private static boolean spamUnknownPackets = false;

	public static void sendToTestChannel(ServerPlayerEntity player, String stuff) {
		ServerPlayNetworking.getSender(player).sendPacket(new OverlayPacket(Text.literal(stuff)), future -> {
			NetworkingTestmods.LOGGER.info("Sent custom payload packet in {}", TEST_CHANNEL);
		});
	}

	private static void sendToUnknownChannel(ServerPlayerEntity player) {
		PacketByteBuf buf = PacketByteBufs.create();

		for (int i = 0; i < 20; i++) {
			buf.writeUuid(UUID.randomUUID());
		}
		// TODO 1.20.5
//		ServerPlayNetworking.getSender(player).sendPacket(UNKNOWN_TEST_CHANNEL, buf);
	}

	public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
		NetworkingTestmods.LOGGER.info("Registering test command");

		dispatcher.register(literal("networktestcommand")
				.then(argument("stuff", string()).executes(ctx -> {
					String stuff = StringArgumentType.getString(ctx, "stuff");
					sendToTestChannel(ctx.getSource().getPlayer(), stuff);
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("unknown").executes(ctx -> {
					sendToUnknownChannel(ctx.getSource().getPlayer());
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("spamUnknown").executes(ctx -> {
					spamUnknownPackets = true;
					ctx.getSource().sendMessage(Text.literal("Spamming unknown packets state:" + spamUnknownPackets));
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("bufctor").executes(ctx -> {
					PacketByteBuf buf = PacketByteBufs.create();
					buf.writeIdentifier(TEST_CHANNEL);
					buf.writeText(Text.literal("bufctor"));
					// TODO 1.20.5
//					ctx.getSource().getPlayer().networkHandler.sendPacket(new CustomPayloadS2CPacket(buf));
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("repeat").executes(ctx -> {
					PacketByteBuf buf = PacketByteBufs.create();
					buf.writeText(Text.literal("repeat"));
					// TODO 1.20.5
//					ServerPlayNetworking.send(ctx.getSource().getPlayer(), TEST_CHANNEL, buf);
//					ServerPlayNetworking.send(ctx.getSource().getPlayer(), TEST_CHANNEL, buf);
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("bundled").executes(ctx -> {
					PacketByteBuf buf1 = PacketByteBufs.create();
					buf1.writeText(Text.literal("bundled #1"));
					PacketByteBuf buf2 = PacketByteBufs.create();
					buf2.writeText(Text.literal("bundled #2"));
					// TODO 1.20.5
//					BundleS2CPacket packet = new BundleS2CPacket((List<Packet<ClientPlayPacketListener>>) (Object) List.of(
//							ServerPlayNetworking.createS2CPacket(TEST_CHANNEL, buf1),
//							ServerPlayNetworking.createS2CPacket(TEST_CHANNEL, buf2)));
//					ctx.getSource().getPlayer().networkHandler.sendPacket(packet);
					return Command.SINGLE_SUCCESS;
				})));
	}

	@Override
	public void onInitialize() {
		NetworkingTestmods.LOGGER.info("Hello from networking user!");

		PayloadTypeRegistry.play(NetworkSide.SERVERBOUND).register(OverlayPacket.ID, OverlayPacket.CODEC);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			NetworkingPlayPacketTest.registerCommand(dispatcher);
		});

		ServerTickEvents.START_SERVER_TICK.register(server -> {
			if (!spamUnknownPackets) {
				return;
			}

			// Send many unknown packets, used to debug https://github.com/FabricMC/fabric/issues/3505
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				for (int i = 0; i < 50; i++) {
					sendToUnknownChannel(player);
				}
			}
		});
	}

	public record OverlayPacket(Text message) implements CustomPayload {
		public static final CustomPayload.Id<OverlayPacket> ID = new Id<>(TEST_CHANNEL);
		public static final PacketCodec<RegistryByteBuf, OverlayPacket> CODEC = CustomPayload.codecOf(OverlayPacket::write, OverlayPacket::new);

		public OverlayPacket(PacketByteBuf buf) {
			this(buf.readText());
		}

		public void write(PacketByteBuf buf) {
			buf.writeText(this.message);
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
