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

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.event.ClientPlayNetworkHandlerCallback;
import net.fabricmc.fabric.api.networking.v1.event.ClientPacketChannelCallback;
import net.fabricmc.fabric.api.networking.v1.receiver.ClientPlayPacketContext;
import net.fabricmc.fabric.impl.networking.receiver.ClientPacketReceivers;
import net.fabricmc.fabric.impl.networking.receiver.SimplePacketReceiverRegistry;

public final class ClientPlayPacketHandler extends AbstractPlayPacketHandler<ClientPlayPacketContext> {
	private final ClientPlayNetworkHandler handler;

	public ClientPlayPacketHandler(ClientPlayNetworkHandler handler) {
		super(handler.getConnection());
		this.handler = handler;
	}

	@Override
	SimplePacketReceiverRegistry<ClientPlayPacketContext> getPacketReceiverRegistry() {
		return ClientPacketReceivers.PLAY;
	}

	@Override
	Packet<?> createActualPacket(Identifier channel, PacketByteBuf buffer) {
		return new CustomPayloadC2SPacket(channel, buffer);
	}

	@Override
	void onAdd(Collection<Identifier> ids) {
		ClientPacketChannelCallback.SERVER_REGISTERED.invoker().accept(handler, this, ids);
	}

	@Override
	void onRemove(Collection<Identifier> ids) {
		ClientPacketChannelCallback.SERVER_UNREGISTERED.invoker().accept(handler, this, ids);
	}

	@Override
	void onReady() {
		ClientPlayNetworkHandlerCallback.INITIALIZED.invoker().handle(handler);
	}
}
