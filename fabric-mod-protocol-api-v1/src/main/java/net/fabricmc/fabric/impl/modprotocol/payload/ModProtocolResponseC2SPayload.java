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

package net.fabricmc.fabric.impl.modprotocol.payload;



import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;



public record ModProtocolResponseC2SPayload(Object2IntMap<Identifier> supported) implements CustomPayload {
	public static final Id<ModProtocolResponseC2SPayload> ID = new Id<>(Identifier.of("fabric", "mod_protocol/response"));
	public static final PacketCodec<PacketByteBuf, ModProtocolResponseC2SPayload> PACKET_CODEC =
			PacketCodecs.map(ModProtocolResponseC2SPayload::createMap, Identifier.PACKET_CODEC, PacketCodecs.INTEGER)
			.xmap(ModProtocolResponseC2SPayload::new, ModProtocolResponseC2SPayload::supported).cast();

	private static Object2IntMap<Identifier> createMap(int i) {
		return new Object2IntOpenHashMap<>(i);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
