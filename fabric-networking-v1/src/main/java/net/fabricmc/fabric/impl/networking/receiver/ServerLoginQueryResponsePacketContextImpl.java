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

import java.util.concurrent.Future;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;

import net.fabricmc.fabric.api.networking.v1.receiver.ServerLoginQueryResponsePacketContext;
import net.fabricmc.fabric.api.networking.v1.sender.PacketSender;
import net.fabricmc.fabric.impl.networking.handler.ServerLoginPacketHandler;

public final class ServerLoginQueryResponsePacketContextImpl implements ServerLoginQueryResponsePacketContext {
	private final MinecraftServer server;
	private final ServerLoginNetworkHandler networkListener;
	private final ServerLoginPacketHandler packetSender;
	private final boolean understood;

	public ServerLoginQueryResponsePacketContextImpl(MinecraftServer server, ServerLoginNetworkHandler networkListener, ServerLoginPacketHandler packetSender, boolean understood) {
		this.server = server;
		this.networkListener = networkListener;
		this.packetSender = packetSender;
		this.understood = understood;
	}

	@Override
	public MinecraftServer getEngine() {
		return server;
	}

	@Override
	public ServerLoginNetworkHandler getNetworkHandler() {
		return networkListener;
	}

	@Override
	public boolean isUnderstood() {
		return understood;
	}

	@Override
	public PacketSender getPacketSender() {
		return packetSender;
	}

	@Override
	public void addLoginHold(Future<?> future) {
		packetSender.addHold(future);
	}
}
