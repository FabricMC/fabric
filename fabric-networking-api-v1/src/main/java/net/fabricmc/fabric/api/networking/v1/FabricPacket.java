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

package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * A packet to be sent using Networking API. An instance of this class is created
 * each time the packet is sent. This can be used on both the client and the server.
 *
 * <p>Implementations should have fields of values sent over the network.
 * For example, a packet consisting of two integers should have two {@code int}
 * fields with appropriate name. This is written to the buffer in {@link #write}.
 * The packet should have two constructors: one that creates a packet on the sender,
 * which initializes the fields to be written, and one that takes a {@link PacketByteBuf}
 * and reads the packet.
 *
 * <p>For each packet class, a corresponding {@link PacketType} instance should be created.
 * The type should be stored in a {@code static final} field, and {@link #getType} should
 * return that type.
 *
 * <p>Example of a packet:
 * <pre>{@code
 * public record BoomPacket(boolean fire) implements FabricPacket {
 * 	public static final PacketType<BoomPacket> TYPE = PacketType.create(new Identifier("example:boom"), BoomPacket::new);
 *
 * 	public BoomPacket(PacketByteBuf buf) {
 * 		this(buf.readBoolean());
 * 	}
 *
 * 	@Override
 * 	public void write(PacketByteBuf buf) {
 * 		buf.writeBoolean(this.fire);
 * 	}
 *
 * 	@Override
 * 	public PacketType<?> getType() {
 * 		return TYPE;
 * 	}
 * }
 * }</pre>
 *
 * @see ServerPlayNetworking#registerGlobalReceiver(PacketType, ServerPlayNetworking.PlayPacketHandler)
 * @see ServerPlayNetworking#send(ServerPlayerEntity, PacketType, FabricPacket)
 * @see PacketSender#sendPacket(FabricPacket)
 */
public interface FabricPacket {
	/**
	 * Writes the contents of this packet to the buffer.
	 * @param buf the output buffer
	 */
	void write(PacketByteBuf buf);

	/**
	 * Returns the packet type of this packet.
	 *
	 * <p>Implementations should store the packet type instance in a {@code static final}
	 * field and return that here, instead of creating a new instance.
	 *
	 * @return the type of this packet
	 */
	PacketType<?> getType();
}
