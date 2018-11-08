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

public class CustomPayloadHandlerRegistry {
	public static final CustomPayloadHandlerRegistry CLIENT = new CustomPayloadHandlerRegistry();
	public static final CustomPayloadHandlerRegistry SERVER = new CustomPayloadHandlerRegistry();

	protected final Map<Identifier, BiConsumer<PacketContext, PacketByteBuf>> consumerMap;

	protected CustomPayloadHandlerRegistry() {
		consumerMap = new HashMap<>();
	}

	public void register(Identifier id, BiConsumer<PacketContext, PacketByteBuf> consumer) {
		if (consumerMap.containsKey(id)) {
			// TODO: log warning
		}

		consumerMap.put(id, consumer);
	}

	public boolean accept(Identifier identifier, PacketContext context, PacketByteBuf buf) {
		BiConsumer<PacketContext, PacketByteBuf> consumer = consumerMap.get(context);
		if (consumer != null) {
			consumer.accept(context, buf);
			return true;
		} else {
			return false;
		}
	}
}
