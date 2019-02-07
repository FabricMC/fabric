package net.fabricmc.fabric.api.network;

import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public interface PacketRegistry {
	Packet<?> toPacket(Identifier id, PacketByteBuf buf);

	/**
	 * Register a packet.
	 *
	 * @param id The packet Identifier.
	 * @param consumer The method used for handling the packet.
	 */
	void register(Identifier id, PacketConsumer consumer);

	/**
	 * Unregister a packet.
	 *
	 * @param id The packet Identifier.
	 */
	void unregister(Identifier id);
}
