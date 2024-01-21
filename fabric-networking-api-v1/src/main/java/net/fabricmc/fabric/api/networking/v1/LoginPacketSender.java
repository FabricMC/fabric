package net.fabricmc.fabric.api.networking.v1;

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

/**
 * Represents something that supports sending packets to login channels.
 * @see PacketSender
 */
@ApiStatus.NonExtendable
public interface LoginPacketSender extends PacketSender {
	Packet<?> createPacket(Identifier channelName, PacketByteBuf buf);

	/**
	 * Sends a packet to a channel.
	 *
	 * @param channel the id of the channel
	 * @param buf the content of the packet
	 */
	default void sendPacket(Identifier channel, PacketByteBuf buf) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(buf, "Payload cannot be null");

		this.sendPacket(this.createPacket(channel, buf));
	}

	/**
	 * Sends a packet to a channel.
	 *
	 * @param channel  the id of the channel
	 * @param buf the content of the packet
	 * @param callback an optional callback to execute after the packet is sent, may be {@code null}
	 */
	default void sendPacket(Identifier channel, PacketByteBuf buf, @Nullable PacketCallbacks callback) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(buf, "Payload cannot be null");

		this.sendPacket(this.createPacket(channel, buf), callback);
	}
}
