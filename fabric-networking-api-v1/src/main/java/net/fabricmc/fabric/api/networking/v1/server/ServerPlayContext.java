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

package net.fabricmc.fabric.api.networking.v1.server;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.networking.v1.PlayContext;

/**
 * Represents the context for {@link ServerNetworking#getPlayReceiver()}, in which a
 * {@link net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket client to server
 * custom payload packet} is received.
 *
 * @see ServerNetworking#getPlayReceiver()
 */
public interface ServerPlayContext extends ServerContext, PlayContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In the server play context, the player is always a server player
	 * associated with the {@link #getListener() network handler}.</p>
	 *
	 * @return a server player
	 */
	@Override
	ServerPlayerEntity getPlayer();

	/**
	 * {@inheritDoc}
	 *
	 * <p>In the client play context, the packet listener is always a {@link
	 * ServerPlayNetworkHandler}.</p>
	 */
	@Override
	ServerPlayNetworkHandler getListener();
}
