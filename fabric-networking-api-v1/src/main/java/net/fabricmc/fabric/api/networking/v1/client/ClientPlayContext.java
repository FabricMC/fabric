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

package net.fabricmc.fabric.api.networking.v1.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayContext;

/**
 * Represents the context for {@link ClientNetworking#getPlayReceiver()}, in which a
 * {@link net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket server to
 * client custom payload packet} is received.
 *
 * @see ClientNetworking#getPlayReceiver()
 */
@Environment(EnvType.CLIENT)
public interface ClientPlayContext extends PlayContext, ClientContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In the client play context, the player is always the client's own player.
	 * It is the same as {@code MinecraftClient.getInstance().player}, but this
	 * player is guaranteed to be non-null.</p>
	 *
	 * @return the client's own player
	 */
	@Override
	ClientPlayerEntity getPlayer();

	/**
	 * {@inheritDoc}
	 *
	 * <p>In the client play context, the packet listener is always a {@link
	 * ClientPlayNetworkHandler}, which exposes a few useful properties, including
	 * the {@linkplain ClientPlayNetworkHandler#getWorld() client world}, the
	 * {@linkplain ClientPlayNetworkHandler#getTagManager() tag manager}, the
	 * {@linkplain ClientPlayNetworkHandler#getAdvancementHandler() advancement
	 * manager}, etc.</p>
	 */
	@Override
	ClientPlayNetworkHandler getListener();
}
