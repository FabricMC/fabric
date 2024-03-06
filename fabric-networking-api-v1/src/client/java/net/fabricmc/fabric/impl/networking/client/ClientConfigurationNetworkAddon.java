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

import java.util.Collections;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.networking.v1.C2SConfigurationChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload;
import net.fabricmc.fabric.impl.networking.payload.ResolvedPayload;
import net.fabricmc.fabric.mixin.networking.client.accessor.ClientCommonNetworkHandlerAccessor;
import net.fabricmc.fabric.mixin.networking.client.accessor.ClientConfigurationNetworkHandlerAccessor;

public final class ClientConfigurationNetworkAddon extends AbstractChanneledNetworkAddon<ClientConfigurationNetworkAddon.Handler> {
	private final ClientConfigurationNetworkHandler handler;
	private final MinecraftClient client;
	private boolean sentInitialRegisterPacket;

	public ClientConfigurationNetworkAddon(ClientConfigurationNetworkHandler handler, MinecraftClient client) {
		super(ClientNetworkingImpl.CONFIGURATION, ((ClientCommonNetworkHandlerAccessor) handler).getConnection(), "ClientPlayNetworkAddon for " + ((ClientConfigurationNetworkHandlerAccessor) handler).getProfile().getName());
		this.handler = handler;
		this.client = client;

		// Must register pending channels via lateinit
		this.registerPendingChannels((ChannelInfoHolder) this.connection, NetworkState.CONFIGURATION);
	}

	@Override
	protected void invokeInitEvent() {
		ClientConfigurationConnectionEvents.INIT.invoker().onConfigurationInit(this.handler, this.client);
	}

	public void onServerReady() {
		// Do nothing for now
	}

	@Override
	protected void receiveRegistration(boolean register, ResolvablePayload resolvable) {
		super.receiveRegistration(register, resolvable);

		if (register && !this.sentInitialRegisterPacket) {
			this.sendInitialChannelRegistrationPacket();
			this.sentInitialRegisterPacket = true;
		}
	}

	@Override
	protected void receive(Handler handler, ResolvedPayload payload) {
		handler.receive(this.client, this.handler, payload, this);
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		MinecraftClient.getInstance().execute(task);
	}

	@Override
	public Packet<?> createPacket(Identifier channelName, PacketByteBuf buf) {
		return ClientPlayNetworking.createC2SPacket(channelName, buf);
	}

	@Override
	public Packet<?> createPacket(FabricPacket packet) {
		return ClientPlayNetworking.createC2SPacket(packet);
	}

	@Override
	protected void invokeRegisterEvent(List<Identifier> ids) {
		C2SConfigurationChannelEvents.REGISTER.invoker().onChannelRegister(this.handler, this, this.client, ids);
	}

	@Override
	protected void invokeUnregisterEvent(List<Identifier> ids) {
		C2SConfigurationChannelEvents.UNREGISTER.invoker().onChannelUnregister(this.handler, this, this.client, ids);
	}

	@Override
	protected void handleRegistration(Identifier channelName) {
		// If we can already send packets, immediately send the register packet for this channel
		if (this.sentInitialRegisterPacket) {
			final PacketByteBuf buf = this.createRegistrationPacket(Collections.singleton(channelName));

			if (buf != null) {
				this.sendPacket(NetworkingImpl.REGISTER_CHANNEL, buf);
			}
		}
	}

	@Override
	protected void handleUnregistration(Identifier channelName) {
		// If we can already send packets, immediately send the unregister packet for this channel
		if (this.sentInitialRegisterPacket) {
			final PacketByteBuf buf = this.createRegistrationPacket(Collections.singleton(channelName));

			if (buf != null) {
				this.sendPacket(NetworkingImpl.UNREGISTER_CHANNEL, buf);
			}
		}
	}

	public void handleReady() {
		ClientConfigurationConnectionEvents.READY.invoker().onConfigurationReady(this.handler, this.client);
		ClientNetworkingImpl.setClientConfigurationAddon(null);
	}

	@Override
	protected void invokeDisconnectEvent() {
		ClientConfigurationConnectionEvents.DISCONNECT.invoker().onConfigurationDisconnect(this.handler, this.client);
	}

	@Override
	protected boolean isReservedChannel(Identifier channelName) {
		return NetworkingImpl.isReservedCommonChannel(channelName);
	}

	public ChannelInfoHolder getChannelInfoHolder() {
		return (ChannelInfoHolder) ((ClientCommonNetworkHandlerAccessor) handler).getConnection();
	}

	public interface Handler {
		void receive(MinecraftClient client, ClientConfigurationNetworkHandler handler, ResolvedPayload payload, PacketSender responseSender);
	}
}
