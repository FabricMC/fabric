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

package net.fabricmc.fabric.impl.networking.handler;

import static net.fabricmc.fabric.impl.networking.receiver.SimplePacketReceiverRegistry.LOGGER;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.receiver.PlayPacketContext;
import net.fabricmc.fabric.api.networking.v1.sender.PlayPacketSender;
import net.fabricmc.fabric.impl.networking.NetworkingInitializer;
import net.fabricmc.fabric.impl.networking.PacketDebugOptions;
import net.fabricmc.fabric.impl.networking.PacketHelper;

public abstract class AbstractPlayPacketHandler<T extends PlayPacketContext> extends AbstractPacketHandler<T> implements PlayPacketSender {
	private final Set<Identifier> acceptedChannels;
	private boolean sendRegister;

	AbstractPlayPacketHandler(ClientConnection connection) {
		super(connection);
		this.acceptedChannels = Collections.newSetFromMap(new ConcurrentHashMap<>());

		Identifier[] preregisteredChannels = NetworkingInitializer.getInstance().getConnectionSpecificChannels().remove(connection);

		if (preregisteredChannels != null) {
			Collections.addAll(this.acceptedChannels, preregisteredChannels);
			sendRegister = false;
		}
	}

	@Override
	public boolean accept(Identifier channel, T context, PacketByteBuf buf) {
		if (PacketHelper.REGISTER.equals(channel)) {
			handleRegistration(true, buf);
			return false;
		}

		if (PacketHelper.UNREGISTER.equals(channel)) {
			handleRegistration(false, buf);
			return false;
		}

		return super.accept(channel, context, buf);
	}

	@Override
	public void init() {
		if (sendRegister) {
			Collection<Identifier> ids = getPacketReceiverRegistry().getAcceptedChannels();

			if (!ids.isEmpty()) {
				connection.send(createPacket(PacketHelper.REGISTER, PacketHelper.createRegisterChannelBuf(ids)));
			}
		}

		onReady();
	}

	private void handleRegistration(boolean register, PacketByteBuf oldBuf) {
		Collection<Identifier> ids = new HashSet<>();

		{
			StringBuilder sb = new StringBuilder();
			char c;
			ByteBuf buf = oldBuf.slice();

			while (buf.readerIndex() < buf.writerIndex()) {
				c = (char) buf.readByte();

				if (c == 0) {
					addId(register, ids, sb.toString());
					sb = new StringBuilder();
				} else {
					sb.append(c);
				}
			}

			addId(register, ids, sb.toString());
		}

		if (register) {
			acceptedChannels.addAll(ids);
			onAdd(ids);
		} else {
			acceptedChannels.removeAll(ids);
			onRemove(ids);
		}
	}

	abstract void onAdd(Collection<Identifier> ids);

	abstract void onRemove(Collection<Identifier> ids);

	private void addId(boolean register, Collection<Identifier> ids, String s) {
		if (!s.isEmpty()) {
			Identifier id = Identifier.tryParse(s);

			if (id == null) {
				LOGGER.warn("Received invalid identifier in \"minecraft:{}\" channel: \"{}\"", register ? "register" : "unregister", s);
			} else {
				ids.add(id);
			}
		}
	}

	@Override
	public boolean accepts(Identifier channel) {
		return acceptedChannels.contains(channel);
	}

	@Override
	final Packet<?> createPacket(Identifier channel, PacketByteBuf buffer) {
		if (PacketDebugOptions.WARN_UNREGISTERED_PACKETS && !accepts(channel) && !PacketHelper.REGISTER.equals(channel) && !PacketHelper.UNREGISTER.equals(channel)) {
			LOGGER.warn("Packet in channel {} is sent to an unregistered recipient!", channel);
		}

		return createActualPacket(channel, buffer);
	}

	abstract Packet<?> createActualPacket(Identifier channel, PacketByteBuf buffer);

	abstract void onReady();
}
