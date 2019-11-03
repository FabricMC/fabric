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

package net.fabricmc.fabric.api.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;

/**
 * The client-side packet registry.
 *
 * <p>It is used for:
 *
 * <ul><li>registering client-side packet receivers (server -&gt; client packets)
 * <li>sending packets to the server (client -&gt; server packets).</ul>
 */
public interface ClientSidePacketRegistry extends PacketRegistry {
	ClientSidePacketRegistry INSTANCE = new ClientSidePacketRegistryImpl();

	/**
	 * Check if the server declared the ability to receive a given packet ID
	 * using the vanilla "register/unregister" protocol.
	 *
	 * @param id The packet identifier.
	 * @return True if the server side declared a given packet identifier.
	 */
	boolean canServerReceive(Identifier id);

	/**
	 * Send a packet to the server.
	 *
	 * @param packet             The packet to be sent.
	 * @param completionListener Completion listener. Can be used to check for
	 *                           the success or failure of sending a given packet, among others.
	 */
	void sendToServer(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener);

	/**
	 * Send an identifier/buffer-based packet to the server.
	 *
	 * @param id                 The packet identifier.
	 * @param buf                The packet byte buffer.
	 * @param completionListener Completion listener. Can be used to check for
	 *                           the success or failure of sending a given packet, among others.
	 */
	default void sendToServer(Identifier id, PacketByteBuf buf, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		sendToServer(toPacket(id, buf), completionListener);
	}

	/**
	 * Send a packet to the server.
	 *
	 * @param packet The packet to be sent.
	 */
	default void sendToServer(Packet<?> packet) {
		sendToServer(packet, null);
	}

	/**
	 * Send an identifier/buffer-based packet to the server.
	 *
	 * @param id  The packet identifier.
	 * @param buf The packet byte buffer.
	 */
	default void sendToServer(Identifier id, PacketByteBuf buf) {
		sendToServer(id, buf, null);
	}
}
