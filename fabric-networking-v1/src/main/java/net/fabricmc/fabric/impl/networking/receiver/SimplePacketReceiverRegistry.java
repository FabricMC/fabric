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

package net.fabricmc.fabric.impl.networking.receiver;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.receiver.PacketContext;
import net.fabricmc.fabric.api.networking.v1.receiver.PacketReceiver;
import net.fabricmc.fabric.api.networking.v1.receiver.PacketReceiverRegistry;
import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.PacketHelper;

public class SimplePacketReceiverRegistry<T extends PacketContext> implements PacketReceiverRegistry<T> {
	public static final Logger LOGGER = LogManager.getLogger();
	private final Map<Identifier, PacketReceiver<? super T>> receivers;

	SimplePacketReceiverRegistry() {
		this.receivers = new LinkedHashMap<>();
	}

	@Override
	public boolean register(Identifier channel, PacketReceiver<? super T> receiver) {
		if (receivers.putIfAbsent(channel, receiver) == null) {
			return true;
		}

		LOGGER.warn("Registered duplicate channel " + channel + "!");
		LOGGER.trace((Supplier<?>) Throwable::new);
		return false;
	}

	@Override
	public boolean unregister(Identifier channel) {
		if (receivers.remove(channel) != null) {
			return true;
		}

		LOGGER.warn("Tried to unregister non-registered channel " + channel + "!");
		LOGGER.trace((Supplier<?>) Throwable::new);
		return false;
	}

	// note: the buf from the buf supplier is released if the method returns true
	public boolean receive(Identifier channel, T context, PacketByteBuf buf) {
		PacketReceiver<? super T> consumer = receivers.get(channel);

		if (consumer == null) {
			return false;
		}

		try {
			consumer.accept(context, PacketByteBufs.duplicate(buf));
		} catch (Throwable t) {
			LOGGER.error("Failed to handle packet in channel " + channel + "!", t);
		}

		PacketHelper.releaseBuffer(buf); // todo release depend on consumer decision?
		return true;
	}

	public Collection<Identifier> getAcceptedChannels() {
		return receivers.keySet();
	}
}
