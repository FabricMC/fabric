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

package net.fabricmc.fabric.api.networking.v1.event;

import java.util.Collection;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.sender.PlayPacketSender;

/**
 * The callback for listening to play-stage custom payload packet channel registrations and
 * unregistrations from "minecraft:register" and "minecraft:unregister" channels.
 *
 * <p>This is fired from the network thread.
 *
 * @param <T> the network handler type
 */
@FunctionalInterface
public interface PacketChannelCallback<T extends PacketListener> {
	/**
	 * Called when the connection the network handler is bound to declares its ability or
	 * inability to receive custom payload packets on certain channels.
	 *
	 * @param handler the network handler
	 * @param sender the sender for sending response packets
	 * @param channels the declared channels
	 */
	void accept(T handler, PlayPacketSender sender, Collection<Identifier> channels);
}
