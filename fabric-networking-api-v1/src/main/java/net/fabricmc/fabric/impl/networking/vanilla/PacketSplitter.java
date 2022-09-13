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

package net.fabricmc.fabric.impl.networking.vanilla;

import java.util.List;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;

public class PacketSplitter extends MessageToMessageEncoder<Packet<?>> {
	public static final String ID = "fabric:packet_splitter";

	private static final Logger LOGGER = LoggerFactory.getLogger(PacketSplitter.class);

	private static final int MAX_S2C_CUSTOM_PACKET_SIZE = 0x100000;
	private static final int MAX_C2S_CUSTOM_PACKET_SIZE = Short.MAX_VALUE;

	private final ChannelHandler delegate;
	private final ClientConnection connection;

	public PacketSplitter(ChannelHandler delegate, ClientConnection connection) {
		this.delegate = delegate;
		this.connection = connection;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet<?> packet, List<Object> out) throws Exception {
		NetworkState state = ctx.channel().attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get();

		// Only try to split non-custom play packets if the encoder is present.
		// Encoder only present when connected to non-local receiver.
		if (state != NetworkState.PLAY
				|| !(delegate instanceof PacketEncoderExtensions encoder)
				|| packet instanceof CustomPayloadS2CPacket
				|| packet instanceof CustomPayloadC2SPacket
		) {
			out.add(packet);
			return;
		}

		// Only try to split when receiver accepts the split packet.
		if (connection.getPacketListener() instanceof NetworkHandlerExtensions ext
				&& ext.getAddon() instanceof AbstractChanneledNetworkAddon<?> addon
				&& !addon.getSendableChannels().contains(NetworkingImpl.SPLIT_CHANNEL)
		) {
			out.add(packet);
			return;
		}

		PacketByteBuf buf = PacketByteBufs.create();

		// First run vanilla encoder with size error messages suppressed, return the buf as-is if it succeeded.
		// Vanilla encoder output format: varint(packet id), byte[](message).
		if (encoder.fabric_tryEncode(ctx, packet, buf)) {
			out.add(buf);
			return;
		}

		int maxPartSize = connection.getOppositeSide() == NetworkSide.CLIENTBOUND ? MAX_S2C_CUSTOM_PACKET_SIZE : MAX_C2S_CUSTOM_PACKET_SIZE;
		int packetId = buf.readVarInt();
		int packetSize = buf.readableBytes();
		int partCount = 0;

		// Part format: varint(packet id), bool(end mark), byte[](part).
		while (buf.isReadable()) {
			PacketByteBuf partBuf = new PacketByteBuf(Unpooled.buffer(maxPartSize));
			partBuf.writeVarInt(packetId);
			int partSize = Math.min(buf.readableBytes(), maxPartSize - partBuf.writerIndex() - 1);
			partBuf.writeBoolean(partSize == buf.readableBytes());
			partBuf.writeBytes(buf, partSize);

			switch (connection.getOppositeSide()) {
			case CLIENTBOUND -> out.add(new CustomPayloadS2CPacket(NetworkingImpl.SPLIT_CHANNEL, partBuf));
			case SERVERBOUND -> out.add(new CustomPayloadC2SPacket(NetworkingImpl.SPLIT_CHANNEL, partBuf));
			}

			partCount++;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Split {} packet sized {} into {} with {} each max", packet.getClass().getSimpleName(), packetSize, partCount, maxPartSize);
		}

		buf.release();
	}
}
