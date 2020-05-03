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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PlayPacketSender;
import net.fabricmc.fabric.api.networking.v1.server.ServerNetworking;
import net.fabricmc.fabric.api.networking.v1.server.ServerPlayContext;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.NetworkingDetails;
import net.fabricmc.fabric.mixin.networking.access.CustomPayloadC2SPacketAccess;

public final class ServerPlayNetworkAddon extends AbstractChanneledNetworkAddon<ServerPlayContext> implements ServerPlayContext {
	private final ServerPlayNetworkHandler handler;

	public ServerPlayNetworkAddon(ServerPlayNetworkHandler handler) {
		super(ServerNetworkingDetails.PLAY, handler.getConnection());
		this.handler = handler;
	}

	public void onClientReady() {
		ServerNetworking.PLAY_INITIALIZED.invoker().handle(handler);
		sendRegistration();
	}

	public boolean handle(CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccess access = (CustomPayloadC2SPacketAccess) packet;
		return handle(access.getChannel(), access.getData(), this);
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		this.handler.player.server.execute(task);
	}

	@Override
	protected Packet<?> makeUncheckedPacket(Identifier channel, PacketByteBuf buf) {
		return new CustomPayloadS2CPacket(channel, buf);
	}

	@Override
	protected void postRegisterEvent(List<Identifier> ids) {
		ServerNetworking.CHANNEL_REGISTERED.invoker().handle(handler, ids);
	}

	@Override
	protected void postUnregisterEvent(List<Identifier> ids) {
		ServerNetworking.CHANNEL_UNREGISTERED.invoker().handle(handler, ids);
	}

	// context

	@Override
	public ServerPlayerEntity getPlayer() {
		NetworkingDetails.OFF_THREAD_GAME_ACCESS_POLICY.check(this, "a server player");
		return this.handler.player;
	}

	@Override
	public ServerPlayNetworkHandler getListener() {
		return this.handler;
	}

	@Override
	public PlayPacketSender getPacketSender() {
		return this;
	}

	@Override
	public MinecraftServer getEngine() {
		return this.handler.player.server;
	}
}
