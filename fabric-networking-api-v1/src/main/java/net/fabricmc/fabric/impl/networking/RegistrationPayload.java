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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.netty.util.AsciiString;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public record RegistrationPayload(Id<RegistrationPayload> id, List<Identifier> channels) implements CustomPayload {
	public static final CustomPayload.Id<RegistrationPayload> REGISTER = new CustomPayload.Id<>(NetworkingImpl.REGISTER_CHANNEL);
	public static final CustomPayload.Id<RegistrationPayload> UNREGISTER = new CustomPayload.Id<>(NetworkingImpl.UNREGISTER_CHANNEL);
	public static final PacketCodec<PacketByteBuf, RegistrationPayload> REGISTER_CODEC = codec(REGISTER);
	public static final PacketCodec<PacketByteBuf, RegistrationPayload> UNREGISTER_CODEC = codec(UNREGISTER);

	private RegistrationPayload(Id<RegistrationPayload> id, PacketByteBuf buf) {
		this(id, read(buf));
	}

	private void write(PacketByteBuf buf) {
		boolean first = true;

		for (Identifier channel : channels) {
			if (first) {
				first = false;
			} else {
				buf.writeByte(0);
			}

			buf.writeBytes(channel.toString().getBytes(StandardCharsets.US_ASCII));
		}
	}

	private static List<Identifier> read(PacketByteBuf buf) {
		List<Identifier> ids = new ArrayList<>();
		StringBuilder active = new StringBuilder();

		while (buf.isReadable()) {
			byte b = buf.readByte();

			if (b != 0) {
				active.append(AsciiString.b2c(b));
			} else {
				addId(ids, active);
				active = new StringBuilder();
			}
		}

		addId(ids, active);

		return Collections.unmodifiableList(ids);
	}

	private static void addId(List<Identifier> ids, StringBuilder sb) {
		String literal = sb.toString();

		try {
			ids.add(new Identifier(literal));
		} catch (InvalidIdentifierException ex) {
			// TODO 1.20.5 maybe mode these back to the network addon, so we can log the connection as we did before?
			NetworkingImpl.LOGGER.warn("Received invalid channel identifier \"{}\"", literal);
		}
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return id;
	}

	private static PacketCodec<PacketByteBuf, RegistrationPayload> codec(Id<RegistrationPayload> id) {
		return CustomPayload.codecOf(RegistrationPayload::write, buf -> new RegistrationPayload(id, buf));
	}
}
