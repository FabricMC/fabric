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

package net.fabricmc.fabric.api.network;

import net.minecraft.network.PacketByteBuf;

/**
 * Interface for receiving CustomPayload-based packets.
 */
@FunctionalInterface
public interface PacketConsumer {
	/**
	 * Receive a CustomPayload-based packet.
	 *
	 * <p>The PacketByteBuf received will be released as soon as the method exits,
	 * meaning that you have to call .retain()/.release() on it if you want to
	 * keep it around after that.
	 *
	 * <p>Please keep in mind that this CAN be called OUTSIDE of the main thread!
	 * Most game operations are not thread-safe, so you should look into using
	 * the thread task queue ({@link PacketContext#getTaskQueue()}) to split
	 * the "reading" (which should happen within this method's execution)
	 * and "applying" (which, unless you know what you're doing, should happen
	 * on the main thread, after this method exits).
	 *
	 * @param context The context (receiving player, side, etc.)
	 * @param buffer  The byte buffer containing the received packet data.
	 */
	void accept(PacketContext context, PacketByteBuf buffer);
}
