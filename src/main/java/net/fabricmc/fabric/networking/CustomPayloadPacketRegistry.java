/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.networking;

import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Registry for CustomPayload-based packet handling. You can use this
 * to register your own CustomPayload packet handlers.
 */
public class CustomPayloadPacketRegistry {
	public static final CustomPayloadPacketRegistry CLIENT = new CustomPayloadPacketRegistry();
	public static final CustomPayloadPacketRegistry SERVER = new CustomPayloadPacketRegistry();

	protected final Map<Identifier, BiConsumer<PacketContext, PacketByteBuf>> consumerMap;

	protected CustomPayloadPacketRegistry() {
		consumerMap = new HashMap<>();
	}

	/**
	 * Register a packet.
	 *
	 * @param id The packet Identifier.
	 * @param consumer The method used for handling the packet.
	 */
	public void register(Identifier id, BiConsumer<PacketContext, PacketByteBuf> consumer) {
		if (consumerMap.containsKey(id)) {
			// TODO: log warning
		}

		consumerMap.put(id, consumer);
	}

	/**
	 * Hook for accepting packets used in Fabric mixins.
	 *
	 * @param id The packet Identifier received.
	 * @param context The packet context provided.
	 * @param buf The packet data buffer received.
	 * @return Whether or not the packet was handled by this packet registry.
	 */
	public boolean accept(Identifier id, PacketContext context, PacketByteBuf buf) {
		BiConsumer<PacketContext, PacketByteBuf> consumer = consumerMap.get(id);
		if (consumer != null) {
			try {
				consumer.accept(context, buf);
			} catch (Throwable t) {
				// TODO: handle better
				t.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}
}
