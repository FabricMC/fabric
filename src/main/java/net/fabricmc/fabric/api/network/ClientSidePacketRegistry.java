/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

/**
 * The client->server packet registry.
 */
public interface ClientSidePacketRegistry extends PacketRegistry {
	static final ClientSidePacketRegistry INSTANCE = new ClientSidePacketRegistryImpl();

	boolean canServerReceive(Identifier id);

	void sendToServer(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener);

	default void sendToServer(Identifier id, PacketByteBuf buf, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		sendToServer(toPacket(id, buf), completionListener);
	}

	default void sendToServer(Packet<?> packet) {
		sendToServer(packet, null);
	}

	default void sendToServer(Identifier id, PacketByteBuf buf) {
		sendToServer(id, buf, null);
	}
}
