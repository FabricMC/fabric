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

package net.fabricmc.fabric.impl.networking.handler;

import java.util.Collection;

import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.event.ServerPlayNetworkHandlerCallback;
import net.fabricmc.fabric.api.networking.v1.event.ServerPacketChannelCallback;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPlayPacketContext;
import net.fabricmc.fabric.impl.networking.receiver.SimplePacketReceiverRegistry;
import net.fabricmc.fabric.impl.networking.receiver.ServerPacketReceivers;

public final class ServerPlayPacketHandler extends AbstractPlayPacketHandler<ServerPlayPacketContext> {
	private final ServerPlayNetworkHandler handler;

	public ServerPlayPacketHandler(ServerPlayNetworkHandler handler) {
		super(handler.getConnection());
		this.handler = handler;
	}

	@Override
	SimplePacketReceiverRegistry<ServerPlayPacketContext> getPacketReceiverRegistry() {
		return ServerPacketReceivers.PLAY;
	}

	@Override
	Packet<?> createActualPacket(Identifier channel, PacketByteBuf buffer) {
		return new CustomPayloadS2CPacket(channel, buffer);
	}

	@Override
	void onAdd(Collection<Identifier> ids) {
		ServerPacketChannelCallback.CLIENT_REGISTERED.invoker().accept(handler, this, ids);
	}

	@Override
	void onRemove(Collection<Identifier> ids) {
		ServerPacketChannelCallback.CLIENT_UNREGISTERED.invoker().accept(handler, this, ids);
	}

	@Override
	void onReady() {
		ServerPlayNetworkHandlerCallback.INITIALIZED.invoker().handle(handler);
	}
}
