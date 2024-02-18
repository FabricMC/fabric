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
import java.util.Objects;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.RegistrationPayload;

public final class ClientPlayNetworkAddon extends AbstractChanneledNetworkAddon<ClientPlayNetworking.PlayPayloadHandler<?>> {
	private final ClientPlayNetworkHandler handler;
	private final MinecraftClient client;
	private final ClientPlayNetworking.Context context;
	private boolean sentInitialRegisterPacket;

	private static final Logger LOGGER = LogUtils.getLogger();

	public ClientPlayNetworkAddon(ClientPlayNetworkHandler handler, MinecraftClient client) {
		super(ClientNetworkingImpl.PLAY, handler.getConnection(), "ClientPlayNetworkAddon for " + handler.getProfile().getName());
		this.handler = handler;
		this.client = client;
		this.context = new ContextImpl(client, this);

		// Must register pending channels via lateinit
		this.registerPendingChannels((ChannelInfoHolder) this.connection, NetworkPhase.PLAY);
	}

	@Override
	protected void invokeInitEvent() {
		ClientPlayConnectionEvents.INIT.invoker().onPlayInit(this.handler, this.client);
	}

	public void onServerReady() {
		try {
			ClientPlayConnectionEvents.JOIN.invoker().onPlayReady(this.handler, this, this.client);
		} catch (RuntimeException e) {
			LOGGER.error("Exception thrown while invoking ClientPlayConnectionEvents.JOIN", e);
		}

		// The client cannot send any packets, including `minecraft:register` until after GameJoinS2CPacket is received.
		this.sendInitialChannelRegistrationPacket();
		this.sentInitialRegisterPacket = true;
	}

	@Override
	protected void receive(ClientPlayNetworking.PlayPayloadHandler<?> handler, CustomPayload payload) {
		this.client.execute(() -> {
			((ClientPlayNetworking.PlayPayloadHandler) handler).receive(payload, context);
		});
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		MinecraftClient.getInstance().execute(task);
	}

	@Override
	public Packet<?> createPacket(CustomPayload packet) {
		return ClientPlayNetworking.createC2SPacket(packet);
	}

	@Override
	protected void invokeRegisterEvent(List<Identifier> ids) {
		C2SPlayChannelEvents.REGISTER.invoker().onChannelRegister(this.handler, this, this.client, ids);
	}

	@Override
	protected void invokeUnregisterEvent(List<Identifier> ids) {
		C2SPlayChannelEvents.UNREGISTER.invoker().onChannelUnregister(this.handler, this, this.client, ids);
	}

	@Override
	protected void handleRegistration(Identifier channelName) {
		// If we can already send packets, immediately send the register packet for this channel
		if (this.sentInitialRegisterPacket) {
			final RegistrationPayload payload = this.createRegistrationPayload(RegistrationPayload.REGISTER, Collections.singleton(channelName));

			if (payload != null) {
				this.sendPacket(payload);
			}
		}
	}

	@Override
	protected void handleUnregistration(Identifier channelName) {
		// If we can already send packets, immediately send the unregister packet for this channel
		if (this.sentInitialRegisterPacket) {
			final RegistrationPayload payload = this.createRegistrationPayload(RegistrationPayload.UNREGISTER, Collections.singleton(channelName));

			if (payload != null) {
				this.sendPacket(payload);
			}
		}
	}

	@Override
	protected void invokeDisconnectEvent() {
		ClientPlayConnectionEvents.DISCONNECT.invoker().onPlayDisconnect(this.handler, this.client);
	}

	@Override
	protected boolean isReservedChannel(Identifier channelName) {
		return NetworkingImpl.isReservedCommonChannel(channelName);
	}

	private record ContextImpl(MinecraftClient client, PacketSender responseSender) implements ClientPlayNetworking.Context {
		private ContextImpl {
			Objects.requireNonNull(client, "client");
			Objects.requireNonNull(responseSender, "responseSender");
		}

		@Override
		public ClientPlayerEntity player() {
			return Objects.requireNonNull(client.player, "player");
		}
	}
}
