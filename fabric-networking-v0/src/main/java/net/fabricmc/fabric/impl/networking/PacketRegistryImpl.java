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

package net.fabricmc.fabric.impl.networking;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.PacketRegistry;

public abstract class PacketRegistryImpl implements PacketRegistry {
	protected static final Logger LOGGER = LogManager.getLogger();
	protected final Map<Identifier, PacketConsumer> consumerMap;

	PacketRegistryImpl() {
		consumerMap = new LinkedHashMap<>();
	}

	public static Optional<Packet<?>> createInitialRegisterPacket(PacketRegistry registry) {
		PacketRegistryImpl impl = (PacketRegistryImpl) registry;
		return impl.createRegisterTypePacket(PacketTypes.REGISTER, impl.consumerMap.keySet());
	}

	@Override
	public void register(Identifier id, PacketConsumer consumer) {
		boolean isNew = true;

		if (consumerMap.containsKey(id)) {
			LOGGER.warn("Registered duplicate packet " + id + "!");
			LOGGER.trace(new Throwable());
			isNew = false;
		}

		consumerMap.put(id, consumer);

		if (isNew) {
			onRegister(id);
		}
	}

	@Override
	public void unregister(Identifier id) {
		if (consumerMap.remove(id) != null) {
			onUnregister(id);
		} else {
			LOGGER.warn("Tried to unregister non-registered packet " + id + "!");
			LOGGER.trace(new Throwable());
		}
	}

	protected abstract void onRegister(Identifier id);

	protected abstract void onUnregister(Identifier id);

	protected abstract Collection<Identifier> getIdCollectionFor(PacketContext context);

	protected abstract void onReceivedRegisterPacket(PacketContext context, Collection<Identifier> ids);

	protected abstract void onReceivedUnregisterPacket(PacketContext context, Collection<Identifier> ids);

	protected Optional<Packet<?>> createRegisterTypePacket(Identifier id, Collection<Identifier> ids) {
		if (ids.isEmpty()) {
			return Optional.empty();
		}

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

		return Optional.of(toPacket(id, buf));
	}

	private boolean acceptRegisterType(Identifier id, PacketContext context, Supplier<PacketByteBuf> bufSupplier) {
		Collection<Identifier> ids = new HashSet<>();

		{
			StringBuilder sb = new StringBuilder();
			char c;
			PacketByteBuf buf = bufSupplier.get();

			try {
				while (buf.readerIndex() < buf.writerIndex()) {
					c = (char) buf.readByte();

					if (c == 0) {
						String s = sb.toString();

						if (!s.isEmpty()) {
							try {
								ids.add(new Identifier(s));
							} catch (InvalidIdentifierException e) {
								LOGGER.warn("Received invalid identifier in " + id + ": " + s + " (" + e.getLocalizedMessage() + ")");
								LOGGER.trace(e);
							}
						}

						sb = new StringBuilder();
					} else {
						sb.append(c);
					}
				}
			} finally {
				buf.release();
			}

			String s = sb.toString();

			if (!s.isEmpty()) {
				try {
					ids.add(new Identifier(s));
				} catch (InvalidIdentifierException e) {
					LOGGER.warn("Received invalid identifier in " + id + ": " + s + " (" + e.getLocalizedMessage() + ")");
					LOGGER.trace(e);
				}
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
	 * <p>As PacketByteBuf getters in vanilla create a copy (to allow releasing the original packet buffer without
	 * breaking other, potentially delayed accesses), we use a Supplier to generate those copies and release them
	 * when needed.
	 *
	 * @param id      The packet Identifier received.
	 * @param context The packet context provided.
	 * @param bufSupplier A supplier creating a new PacketByteBuf.
	 * @return Whether or not the packet was handled by this packet registry.
	 */
	public boolean accept(Identifier id, PacketContext context, Supplier<PacketByteBuf> bufSupplier) {
		if (id.equals(PacketTypes.REGISTER) || id.equals(PacketTypes.UNREGISTER)) {
			return acceptRegisterType(id, context, bufSupplier);
		}

		PacketConsumer consumer = consumerMap.get(id);

		if (consumer != null) {
			PacketByteBuf buf = bufSupplier.get();

			try {
				consumer.accept(context, buf);
			} catch (Throwable t) {
				LOGGER.warn("Failed to handle packet " + id + "!", t);
			} finally {
				if (buf.refCnt() > 0 && !PacketDebugOptions.DISABLE_BUFFER_RELEASES) {
					buf.release();
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
