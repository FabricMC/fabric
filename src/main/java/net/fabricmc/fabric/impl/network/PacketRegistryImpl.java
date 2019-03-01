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

import com.google.common.base.Charsets;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class PacketRegistryImpl implements PacketRegistry {
	protected static final Logger LOGGER = LogManager.getLogger();
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
		boolean first = true;
		for (Identifier a : ids) {
			if (!first) {
				buf.writeByte(0);
			} else {
				first = false;
			}
			buf.writeBytes(a.toString().getBytes(StandardCharsets.US_ASCII));
		}
		return toPacket(id, buf);
	}

	private boolean acceptRegisterType(Identifier id, PacketContext context, PacketByteBuf buf) {
		Collection<Identifier> ids = new HashSet<>();

		{
			StringBuilder sb = new StringBuilder();
			char c;

			while (buf.readerIndex() < buf.writerIndex()) {
				c = (char) buf.readByte();
				if (c == 0) {
					String s = sb.toString();
					if (!s.isEmpty()) {
						ids.add(new Identifier(s));
					}
					sb = new StringBuilder();
				} else {
					sb.append(c);
				}
			}

			String s = sb.toString();
			if (!s.isEmpty()) {
				ids.add(new Identifier(s));
			}
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
			return acceptRegisterType(id, context, buf);
		}

		PacketConsumer consumer = consumerMap.get(id);
		if (consumer != null) {
			try {
				consumer.accept(context, buf);
			} catch (Throwable t) {
				LOGGER.warn("Failed to handle packet " + id + "!", t);
			}
			return true;
		} else {
			return false;
		}
	}
}
