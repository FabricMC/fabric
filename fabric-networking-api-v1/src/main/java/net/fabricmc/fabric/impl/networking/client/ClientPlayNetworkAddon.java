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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PlayPacketSender;
import net.fabricmc.fabric.api.networking.v1.client.ClientNetworking;
import net.fabricmc.fabric.api.networking.v1.client.ClientPlayContext;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.NetworkingDetails;

public final class ClientPlayNetworkAddon extends AbstractChanneledNetworkAddon<ClientPlayContext> implements ClientPlayContext {
	private final ClientPlayNetworkHandler handler;

	public ClientPlayNetworkAddon(ClientPlayNetworkHandler handler) {
		super(ClientNetworkingDetails.PLAY, handler.getConnection());
		this.handler = handler;
	}

	// also expose sendRegistration

	public void onServerReady() {
		sendRegistration();
		ClientNetworking.PLAY_INITIALIZED.invoker().handle(this.handler);
	}

	public boolean handle(CustomPayloadS2CPacket packet) {
		PacketByteBuf buf = packet.getData();

		try {
			return handle(packet.getChannel(), buf, this);
		} finally {
			buf.release();
		}
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		MinecraftClient.getInstance().execute(task);
	}

	@Override
	protected Packet<?> makeUncheckedPacket(Identifier channel, PacketByteBuf buf) {
		return new CustomPayloadC2SPacket(channel, buf);
	}

	@Override
	protected void postRegisterEvent(List<Identifier> ids) {
		ClientNetworking.CHANNEL_REGISTERED.invoker().handle(handler, ids);
	}

	@Override
	protected void postUnregisterEvent(List<Identifier> ids) {
		ClientNetworking.CHANNEL_UNREGISTERED.invoker().handle(handler, ids);
	}

	// context stuff

	@Override
	public ClientPlayerEntity getPlayer() {
		NetworkingDetails.OFF_THREAD_GAME_ACCESS_POLICY.check(this, "the client player");
		return MinecraftClient.getInstance().player;
	}

	@Override
	public ClientPlayNetworkHandler getListener() {
		return this.handler;
	}

	@Override
	public PlayPacketSender getPacketSender() {
		return this;
	}

	@Override
	public MinecraftClient getEngine() {
		return MinecraftClient.getInstance();
	}
}
