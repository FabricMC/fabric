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

import java.util.Collections;
import java.util.List;

import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.S2CConfigurationChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload;
import net.fabricmc.fabric.impl.networking.payload.ResolvedPayload;
import net.fabricmc.fabric.mixin.networking.accessor.ServerCommonNetworkHandlerAccessor;

public final class ServerConfigurationNetworkAddon extends AbstractChanneledNetworkAddon<ServerConfigurationNetworkAddon.Handler> {
	private final ServerConfigurationNetworkHandler handler;
	private final MinecraftServer server;
	private RegisterState registerState = RegisterState.NOT_SENT;

	public ServerConfigurationNetworkAddon(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
		super(ServerNetworkingImpl.CONFIGURATION, ((ServerCommonNetworkHandlerAccessor) handler).getConnection(), "ServerConfigurationNetworkAddon for " + handler.getDebugProfile().getName());
		this.handler = handler;
		this.server = server;

		// Must register pending channels via lateinit
		this.registerPendingChannels((ChannelInfoHolder) this.connection, NetworkState.CONFIGURATION);
	}

	@Override
	protected void invokeInitEvent() {
	}

	public void preConfiguration() {
		ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.invoker().onSendConfiguration(handler, server);
	}

	public void configuration() {
		ServerConfigurationConnectionEvents.CONFIGURE.invoker().onSendConfiguration(handler, server);
	}

	public boolean startConfiguration() {
		if (this.registerState == RegisterState.NOT_SENT) {
			// Send the registration packet, followed by a ping
			this.sendInitialChannelRegistrationPacket();
			this.sendPacket(new CommonPingS2CPacket(0xFAB71C));

			this.registerState = RegisterState.SENT;

			// Cancel the configuration for now, the response from the ping or registration packet will continue.
			return true;
		}

		// We should have received a response
		assert registerState == RegisterState.RECEIVED || registerState == RegisterState.NOT_RECEIVED;
		return false;
	}

	@Override
	protected void receiveRegistration(boolean register, ResolvablePayload resolvable) {
		super.receiveRegistration(register, resolvable);

		if (register && registerState == RegisterState.SENT) {
			// We received the registration packet, thus we know this is a modded client, continue with configuration.
			registerState = RegisterState.RECEIVED;
			handler.sendConfigurations();
		}
	}

	public void onPong(int parameter) {
		if (registerState == RegisterState.SENT) {
			// We did not receive the registration packet, thus we think this is a vanilla client, continue with configuration.
			registerState = RegisterState.NOT_RECEIVED;
			handler.sendConfigurations();
		}
	}

	@Override
	protected void receive(Handler handler, ResolvedPayload payload) {
		handler.receive(this.server, this.handler, payload, this);
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		this.server.execute(task);
	}

	@Override
	public Packet<?> createPacket(Identifier channelName, PacketByteBuf buf) {
		return ServerPlayNetworking.createS2CPacket(channelName, buf);
	}

	@Override
	public Packet<?> createPacket(FabricPacket packet) {
		return ServerPlayNetworking.createS2CPacket(packet);
	}

	@Override
	protected void invokeRegisterEvent(List<Identifier> ids) {
		S2CConfigurationChannelEvents.REGISTER.invoker().onChannelRegister(this.handler, this, this.server, ids);
	}

	@Override
	protected void invokeUnregisterEvent(List<Identifier> ids) {
		S2CConfigurationChannelEvents.UNREGISTER.invoker().onChannelUnregister(this.handler, this, this.server, ids);
	}

	@Override
	protected void handleRegistration(Identifier channelName) {
		// If we can already send packets, immediately send the register packet for this channel
		if (this.registerState != RegisterState.NOT_SENT) {
			final PacketByteBuf buf = this.createRegistrationPacket(Collections.singleton(channelName));

			if (buf != null) {
				this.sendPacket(NetworkingImpl.REGISTER_CHANNEL, buf);
			}
		}
	}

	@Override
	protected void handleUnregistration(Identifier channelName) {
		// If we can already send packets, immediately send the unregister packet for this channel
		if (this.registerState != RegisterState.NOT_SENT) {
			final PacketByteBuf buf = this.createRegistrationPacket(Collections.singleton(channelName));

			if (buf != null) {
				this.sendPacket(NetworkingImpl.UNREGISTER_CHANNEL, buf);
			}
		}
	}

	@Override
	protected void invokeDisconnectEvent() {
		ServerConfigurationConnectionEvents.DISCONNECT.invoker().onConfigureDisconnect(handler, server);
	}

	@Override
	protected boolean isReservedChannel(Identifier channelName) {
		return NetworkingImpl.isReservedCommonChannel(channelName);
	}

	@Override
	public void sendPacket(Packet<?> packet, PacketCallbacks callback) {
		handler.send(packet, callback);
	}

	private enum RegisterState {
		NOT_SENT,
		SENT,
		RECEIVED,
		NOT_RECEIVED
	}

	public ChannelInfoHolder getChannelInfoHolder() {
		return (ChannelInfoHolder) ((ServerCommonNetworkHandlerAccessor) handler).getConnection();
	}

	public interface Handler {
		void receive(MinecraftServer server, ServerConfigurationNetworkHandler handler, ResolvedPayload payload, PacketSender responseSender);
	}
}
