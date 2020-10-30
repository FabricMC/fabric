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

package net.fabricmc.fabric.impl.networking.server;

import java.util.List;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.mixin.networking.accessor.CustomPayloadC2SPacketAccessor;

public final class ServerPlayNetworkAddon extends AbstractChanneledNetworkAddon<ServerPlayNetworking.PlayChannelHandler> {
	private final ServerPlayNetworkHandler handler;
	private final MinecraftServer server;

	public ServerPlayNetworkAddon(ServerPlayNetworkHandler handler, MinecraftServer server) {
		super(ServerNetworkingImpl.PLAY, handler.getConnection());
		this.handler = handler;
		this.server = server;

		// Must register pending channels via lateinit
		this.registerPendingChannels((ChannelInfoHolder) this.connection);
	}

	public void onClientReady() {
		ServerPlayConnectionEvents.PLAY_INITIALIZED.invoker().onPlayInitialized(this.handler, this.server, this);
		this.sendChannelRegistrationPacket();
	}

	/**
	 * Handles an incoming packet.
	 *
	 * @param packet the packet to handle
	 * @return true if the packet has been handled
	 */
	public boolean handle(CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccessor access = (CustomPayloadC2SPacketAccessor) packet;
		return handle(access.getChannel(), access.getData());
	}

	@Override
	protected void receive(ServerPlayNetworking.PlayChannelHandler handler, PacketByteBuf buf) {
		handler.receive(this.handler, this.server, this, buf);
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		this.handler.player.server.execute(task);
	}

	@Override
	public Packet<?> makePacket(Identifier channel, PacketByteBuf buf) {
		return ServerPlayNetworking.createS2CPacket(channel, buf);
	}

	@Override
	protected void postRegisterEvent(List<Identifier> ids) {
		ServerChannelEvents.REGISTERED.invoker().onChannelRegistered(this.handler, this.server, this, ids);
	}

	@Override
	protected void postUnregisterEvent(List<Identifier> ids) {
		ServerChannelEvents.UNREGISTERED.invoker().onChannelUnregistered(this.handler, this.server, this, ids);
	}
}
