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

package net.fabricmc.fabric.impl.networking.receiver;

import java.util.Collections;

import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPlayPacketContext;
import net.fabricmc.fabric.impl.networking.PacketHelper;

final class ServerPlayPacketReceiverRegistry extends NotifyingPacketReceiverRegistry<ServerPlayPacketContext> {
	private MinecraftServer server;

	ServerPlayPacketReceiverRegistry() {
		ServerStartCallback.EVENT.register(this::onServerStart);
		ServerStopCallback.EVENT.register(this::onServerStop);
	}

	private void onServerStart(MinecraftServer server) {
		this.server = server;
	}

	private void onServerStop(MinecraftServer server) {
		this.server = null;
	}

	@Override
	protected void notify(boolean register, Identifier channel) {
		MinecraftServer server = this.server;
		if (server == null) return;

		PacketByteBuf buf = PacketHelper.createRegisterChannelBuf(Collections.singleton(channel));
		Packet<?> packet = new CustomPayloadS2CPacket(register ? PacketHelper.REGISTER : PacketHelper.UNREGISTER, buf);
		server.getPlayerManager().sendToAll(packet);
	}
}
