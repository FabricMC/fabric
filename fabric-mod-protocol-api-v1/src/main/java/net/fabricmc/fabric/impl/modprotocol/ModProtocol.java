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

package net.fabricmc.fabric.impl.modprotocol;

import java.util.List;
import java.util.stream.IntStream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ModProtocol(String id, String displayName, String displayVersion, IntList protocols, boolean requiredClient, boolean requiredServer) {
	public static final int UNSUPPORTED = -1;
	public static final Codec<ModProtocol> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("id").forGetter(ModProtocol::id),
		Codec.STRING.fieldOf("name").forGetter(ModProtocol::displayName),
		Codec.STRING.fieldOf("version").forGetter(ModProtocol::displayVersion),
		Codec.INT_STREAM.xmap(x -> IntList.of(x.toArray()), x -> IntStream.of(x.toIntArray())).fieldOf("protocol").forGetter(ModProtocol::protocols),
		Codec.BOOL.fieldOf("require_client").forGetter(ModProtocol::requiredClient),
		Codec.BOOL.fieldOf("require_server").forGetter(ModProtocol::requiredServer)
	).apply(instance, ModProtocol::new));
	public static final Codec<List<ModProtocol>> LIST_CODEC = CODEC.listOf();
	public int getHighestVersion(IntList list) {
		int value = UNSUPPORTED;
		for (int i = 0; i < list.size(); i++) {
			var proto = list.getInt(i);
			if (this.protocols.contains(proto) && value < proto) {
				value = proto;
			}
		}

		return value;
	}

	public static final PacketCodec<PacketByteBuf, ModProtocol> PACKET_CODEC = PacketCodec.ofStatic(ModProtocol::encode, ModProtocol::decode);

	private static ModProtocol decode(PacketByteBuf buf) {
		var id = buf.readString();
		var name = buf.readString();
		var version = buf.readString();
		var protocols = IntList.of(buf.readIntArray());
		var b = buf.readByte();
		var requireClient = (b & 0b10) != 0;
		var requireServer = (b & 0b01) != 0;
		return new ModProtocol(id, name, version, protocols, requireClient, requireServer);
	}

	private static void encode(PacketByteBuf buf, ModProtocol protocol) {
		buf.writeString(protocol.id);
		buf.writeString(protocol.displayName);
		buf.writeString(protocol.displayVersion);
		buf.writeIntArray(protocol.protocols.toIntArray());
		buf.writeByte((protocol.requiredClient ? 0b10 : 0) | (protocol.requiredServer ? 0b01 : 0));
	}
}
