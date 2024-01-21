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

package net.fabricmc.fabric.test.networking.configuration;

import java.util.function.Consumer;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerConfigurationTask;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.test.networking.NetworkingTestmods;

/**
 * Also see NetworkingConfigurationClientTest.
 */
public class NetworkingConfigurationTest implements ModInitializer {
	@Override
	public void onInitialize() {
		PayloadTypeRegistry.configuration(NetworkSide.CLIENTBOUND).register(ConfigurationPacket.ID, ConfigurationPacket.CODEC);
		PayloadTypeRegistry.configuration(NetworkSide.SERVERBOUND).register(ConfigurationCompletePacket.ID, ConfigurationCompletePacket.CODEC);

		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
			// You must check to see if the client can handle your config task
			if (ServerConfigurationNetworking.canSend(handler, ConfigurationPacket.ID)) {
				handler.addTask(new TestConfigurationTask("Example data"));
			} else {
				// You can opt to disconnect the client if it cannot handle the configuration task
				handler.disconnect(Text.literal("Network test configuration not supported by client"));
			}
		});

		ServerConfigurationNetworking.registerGlobalReceiver(ConfigurationCompletePacket.ID, (packet, networkHandler, responseSender) -> {
			networkHandler.completeTask(TestConfigurationTask.KEY);
		});
	}

	public record TestConfigurationTask(String data) implements ServerPlayerConfigurationTask {
		public static final Key KEY = new Key(new Identifier(NetworkingTestmods.ID, "configure").toString());

		@Override
		public void sendPacket(Consumer<Packet<?>> sender) {
			var packet = new ConfigurationPacket(data);
			sender.accept(ServerConfigurationNetworking.createS2CPacket(packet));
		}

		@Override
		public Key getKey() {
			return KEY;
		}
	}

	public record ConfigurationPacket(String data) implements CustomPayload {
		public static final CustomPayload.Id<ConfigurationPacket> ID = new Id<>(new Identifier(NetworkingTestmods.ID, "configure"));
		public static final PacketCodec<PacketByteBuf, ConfigurationPacket> CODEC = CustomPayload.codecOf(ConfigurationPacket::write, ConfigurationPacket::new);

		public ConfigurationPacket(PacketByteBuf buf) {
			this(buf.readString());
		}

		public void write(PacketByteBuf buf) {
			buf.writeString(data);
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	public record ConfigurationCompletePacket() implements CustomPayload {
		public static final CustomPayload.Id<ConfigurationCompletePacket> ID = new Id<>(new Identifier(NetworkingTestmods.ID, "configure_complete"));
		public static final PacketCodec<PacketByteBuf, ConfigurationCompletePacket> CODEC = CustomPayload.codecOf(ConfigurationCompletePacket::write, ConfigurationCompletePacket::new);

		public ConfigurationCompletePacket(PacketByteBuf buf) {
			this();
		}

		public void write(PacketByteBuf buf) {
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
