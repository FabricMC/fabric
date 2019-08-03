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

package net.fabricmc.fabric.impl.network.login;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.mixin.network.LoginQueryRequestS2CPacketAccessor;
import net.fabricmc.fabric.mixin.network.LoginQueryResponseC2SPacketAccessor;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.LinkedList;
import java.util.Queue;

public class S2CLoginQueryQueueImpl implements S2CLoginQueryQueue {
	private static final class Entry {
		private final Identifier id;
		private final PacketByteBuf buf;
		private final Receiver receiver;

		Entry(Identifier id, PacketByteBuf buf, Receiver receiver) {
			this.id = id;
			this.buf = buf;
			this.receiver = receiver;
		}
	}

	private final ServerLoginNetworkHandler handler;
	private final Int2ObjectMap<Entry> awaitees = new Int2ObjectOpenHashMap<>();
	private final Queue<Entry> queue = new LinkedList<>();
	private boolean finished = false;
	private int nextQueryId = 1048576 + Math.round((float) Math.random() * 1048576);

	public S2CLoginQueryQueueImpl(ServerLoginNetworkHandler handler) {
		this.handler = handler;

		S2CLoginHandshakeCallback.EVENT.invoker().accept(this);
	}

	public boolean receiveResponse(LoginQueryResponseC2SPacket packet) {
		LoginQueryResponseC2SPacketAccessor packetAccessor = (LoginQueryResponseC2SPacketAccessor) packet;
		Entry entry = awaitees.remove(packetAccessor.getQueryId());
		if (entry != null) {
			entry.receiver.onResponse(handler, handler.client, entry.id, (PacketByteBuf) packetAccessor.getResponse());
			return true;
		} else {
			return false;
		}
	}

	public boolean tick() {
		if (finished) {
			return false;
		}

		if (!awaitees.isEmpty()) {
			return true; // waiting for packet
		}

		if (!queue.isEmpty()) {
			Entry entry = queue.remove();

			LoginQueryRequestS2CPacket packet = new LoginQueryRequestS2CPacket();
			//noinspection ConstantConditions
			LoginQueryRequestS2CPacketAccessor packetAccessor = (LoginQueryRequestS2CPacketAccessor) packet;

			packetAccessor.setQueryId(nextQueryId++);
			packetAccessor.setChannel(entry.id);
			packetAccessor.setPayload(entry.buf);

			awaitees.put(packetAccessor.getQueryId(), entry);
			handler.client.send(packet);

			return true; // waiting for new packet
		} else {
			finished = true;
			return false; // we're done!
		}
	}

	@Override
	public void sendPacket(Identifier id, PacketByteBuf buf, Receiver receiver) {
		queue.add(new Entry(id, buf, receiver));
	}
}
