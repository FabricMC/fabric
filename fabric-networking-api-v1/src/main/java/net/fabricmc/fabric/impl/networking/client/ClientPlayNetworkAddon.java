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

package net.fabricmc.fabric.impl.networking.client;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ClientChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ClientConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ClientNetworking;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;

public final class ClientPlayNetworkAddon extends AbstractChanneledNetworkAddon<ClientNetworking.PlayChannelHandler> {
	private final ClientPlayNetworkHandler handler;
	private final MinecraftClient client;

	public ClientPlayNetworkAddon(ClientPlayNetworkHandler handler, MinecraftClient client) {
		super(ClientNetworkingDetails.PLAY, handler.getConnection());
		this.handler = handler;
		this.client = client;

		// Must register pending channels via lateinit
		this.registerPendingChannels((ChannelInfoHolder) this.connection);
	}

	// also expose sendRegistration

	public void onServerReady() {
		this.sendChannelRegistrationPacket();
		ClientConnectionEvents.PLAY_INITIALIZED.invoker().onPlayInitialized(this.handler, this.client, this);
	}

	/**
	 * Handles an incoming packet.
	 *
	 * @param packet the packet to handle
	 * @return true if the packet has been handled
	 */
	public boolean handle(CustomPayloadS2CPacket packet) {
		PacketByteBuf buf = packet.getData();

		try {
			return handle(packet.getChannel(), buf);
		} finally {
			buf.release();
		}
	}

	@Override
	protected void receive(ClientNetworking.PlayChannelHandler handler, PacketByteBuf buf) {
		handler.receive(this.handler, this.client, this, buf);
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		MinecraftClient.getInstance().execute(task);
	}

	@Override
	public Packet<?> makePacket(Identifier channel, PacketByteBuf buf) {
		return new CustomPayloadC2SPacket(channel, buf);
	}

	@Override
	protected void postRegisterEvent(List<Identifier> ids) {
		ClientChannelEvents.REGISTERED.invoker().onChannelRegistered(this.handler, this.client, this, ids);
	}

	@Override
	protected void postUnregisterEvent(List<Identifier> ids) {
		ClientChannelEvents.UNREGISTERED.invoker().onChannelUnregistered(this.handler, this.client, this, ids);
	}
}
