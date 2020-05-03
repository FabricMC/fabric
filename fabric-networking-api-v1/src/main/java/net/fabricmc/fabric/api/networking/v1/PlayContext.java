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

package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Represents a context for {@linkplain PacketReceiver packet reception}
 * in {@linkplain net.minecraft.network.NetworkState#PLAY play stage} of
 * the game in a channel.
 *
 * <p>Compared to the basic listener context, the play context offers
 * more access to the game.</p>
 */
public interface PlayContext extends ListenerContext {
	/**
	 * Returns the packet sender corresponding this context.
	 *
	 * <p>This packet sender may be useful for responding after receiving the packet.</p>
	 *
	 * @return the packet sender
	 */
	PlayPacketSender getPacketSender();

	/**
	 * Returns a player associated with the current packet.
	 *
	 * <p>For security concerns, this method should be called on game engine threads
	 * in order to prevent inadvertent asynchronous modifications to the game.</p>
	 *
	 * <p>{@code fabric-networking-api-v1.offThreadGameAccess} system property
	 * can be set to {@code PERMIT} for disabling checks, {@code WARN} for emitting an
	 * error message, and {@code THROW} to throw an exception. The values are case
	 * insensitive.</p>
	 *
	 * @return the player associated with the current packet
	 * @throws IllegalArgumentException if this method is called outside of
	 *                                  the corresponding engine threads
	 */
	PlayerEntity getPlayer();
}
