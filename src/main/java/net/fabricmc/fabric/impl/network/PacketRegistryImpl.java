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

package net.fabricmc.fabric.impl.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.*;

public abstract class PacketRegistryImpl implements PacketRegistry {
	protected final Map<Identifier, PacketConsumer> consumerMap;

	PacketRegistryImpl() {
		consumerMap = new LinkedHashMap<>();
	}

	public static Packet<?> createInitialRegisterPacket(PacketRegistry registry) {
		PacketRegistryImpl impl = (PacketRegistryImpl) registry;
		return impl.createRegisterTypePacket(PacketTypes.REGISTER, impl.consumerMap.keySet());
	}

	@Override
	public void register(Identifier id, PacketConsumer consumer) {
		boolean isNew = true;
		if (consumerMap.containsKey(id)) {
			// TODO: log warning
			isNew = false;
		}

		consumerMap.put(id, consumer);
		if (isNew) {
			onRegister(id);
		}
	}

	@Override
	public void unregister(Identifier id) {
		consumerMap.remove(id);
		onUnregister(id);
	}

	protected abstract void onRegister(Identifier id);
	protected abstract void onUnregister(Identifier id);
	protected abstract Collection<Identifier> getIdCollectionFor(PacketContext context);
	protected abstract void onReceivedRegisterPacket(PacketContext context, Collection<Identifier> ids);
	protected abstract void onReceivedUnregisterPacket(PacketContext context, Collection<Identifier> ids);

	protected Packet<?> createRegisterTypePacket(Identifier id, Collection<Identifier> ids) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		for (Identifier a : ids) {
			buf.writeString(a.toString());
		}
		return toPacket(id, buf);
	}

	/**
	 * Hook for accepting packets used in Fabric mixins.
	 *
	 * @param id The packet Identifier received.
	 * @param context The packet context provided.
	 * @param buf The packet data buffer received.
	 * @return Whether or not the packet was handled by this packet registry.
	 */
	public boolean accept(Identifier id, PacketContext context, PacketByteBuf buf) {
		if (id.equals(PacketTypes.REGISTER) || id.equals(PacketTypes.UNREGISTER)) {
			Collection<Identifier> ids = new HashSet<>();
			while (buf.readerIndex() < buf.writerIndex() /* TODO: check correctness */) {
				Identifier newId = new Identifier(buf.readString(32767));
				ids.add(newId);
			}

			Collection<Identifier> target = getIdCollectionFor(context);
			if (id.equals(PacketTypes.UNREGISTER)) {
				target.removeAll(ids);
				onReceivedUnregisterPacket(context, ids);
			} else {
				target.addAll(ids);
				onReceivedRegisterPacket(context, ids);
			}
			return false; // continue execution for other mods
		}

		PacketConsumer consumer = consumerMap.get(id);
		if (consumer != null) {
			try {
				consumer.accept(context, buf);
			} catch (Throwable t) {
				// TODO: handle better
				t.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}
}
