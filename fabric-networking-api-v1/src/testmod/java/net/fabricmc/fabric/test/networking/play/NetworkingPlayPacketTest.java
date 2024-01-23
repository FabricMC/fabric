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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerCookieStore;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerTransferable;
import net.fabricmc.fabric.test.networking.NetworkingTestmods;
import net.fabricmc.loader.api.FabricLoader;

public final class NetworkingPlayPacketTest implements ModInitializer {
	private static boolean spamUnknownPackets = false;

	public static void sendToTestChannel(ServerPlayerEntity player, String stuff) {
		ServerPlayNetworking.getSender(player).sendPacket(new OverlayPacket(Text.literal(stuff)), PacketCallbacks.always(() -> {
			NetworkingTestmods.LOGGER.info("Sent custom payload packet");
		}));
	}

	private static void sendToUnknownChannel(ServerPlayerEntity player) {
		ServerPlayNetworking.getSender(player).sendPacket(new UnknownPayload("Hello"));
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
				.then(literal("simple").executes(ctx -> {
					ServerPlayNetworking.send(ctx.getSource().getPlayer(), new OverlayPacket(Text.literal("simple")));
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("bundled").executes(ctx -> {
					BundleS2CPacket packet = new BundleS2CPacket(List.of(
							ServerPlayNetworking.createS2CPacket(new OverlayPacket(Text.literal("bundled #1"))),
							ServerPlayNetworking.createS2CPacket(new OverlayPacket(Text.literal("bundled #2")))
					));
					ServerPlayNetworking.getSender(ctx.getSource().getPlayer()).sendPacket(packet);
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("transfer").executes(ctx -> {
					((ServerTransferable) ctx.getSource().getPlayer().networkHandler).transferToServer("localhost", 25565);
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("setCookie").executes(ctx -> {
					((ServerCookieStore) ctx.getSource().getPlayer().networkHandler).setCookie(new Identifier("fabric:test"), "123456789".getBytes(StandardCharsets.UTF_8));
					return Command.SINGLE_SUCCESS;
				}))
				.then(literal("getCookie").executes(ctx -> {
					ServerPlayerEntity player = ctx.getSource().getPlayer();
					((ServerCookieStore) player.networkHandler).getCookie(new Identifier("fabric:test")).whenComplete((data, throwable) -> {
						if (data.length == 0) return;
						player.sendMessage(Text.of(new String(data)));
					});
					return Command.SINGLE_SUCCESS;
				})));
	}

	@Override
	public void onInitialize() {
		NetworkingTestmods.LOGGER.info("Hello from networking user!");

		PayloadTypeRegistry.playS2C().register(OverlayPacket.ID, OverlayPacket.CODEC);

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			PayloadTypeRegistry.playS2C().register(UnknownPayload.ID, UnknownPayload.CODEC);
		}

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

		ServerLoginConnectionEvents.INIT.register((handler, server) -> {
			((ServerCookieStore) handler).getCookie(new Identifier("fabric:test")).whenComplete((data, throwable) -> {
				if (data.length == 0) return;
				assert Arrays.equals(data, "123456789".getBytes(StandardCharsets.UTF_8));
			});
		});
	}

	public record OverlayPacket(Text message) implements CustomPayload {
		public static final CustomPayload.Id<OverlayPacket> ID = new Id<>(NetworkingTestmods.id("test_channel"));
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

	private record UnknownPayload(String data) implements CustomPayload {
		private static final CustomPayload.Id<UnknownPayload> ID = new Id<>(NetworkingTestmods.id("unknown_test_channel_s2c"));
		private static final PacketCodec<PacketByteBuf, UnknownPayload> CODEC = PacketCodecs.STRING.xmap(UnknownPayload::new, UnknownPayload::data).cast();

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
