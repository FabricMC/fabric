package net.fabricmc.fabric.api.network;

import net.minecraft.util.PacketByteBuf;

/**
 * Interface for receiving CustomPayload-based packets.
 */
@FunctionalInterface
public interface PacketConsumer {
	/**
	 * Receive a CustomPayload-based packet.
	 *
	 * Please keep in mind that this CAN be called OUTSIDE of the main thread!
	 * Most game operations are not thread-safe, so you should look into using
	 * the thread task queue ({@link PacketContext#getTaskQueue()}) to split
	 * the "reading" (which should, but does not have to, happen off-thread)
	 * and "applying" (which, unless you know what you're doing, should happen
	 * on the main thread).
	 *
	 * @param context The context (receiving player, side, etc.)
	 * @param buffer The byte buffer containing the received packet data.
	 */
	void accept(PacketContext context, PacketByteBuf buffer);
}
