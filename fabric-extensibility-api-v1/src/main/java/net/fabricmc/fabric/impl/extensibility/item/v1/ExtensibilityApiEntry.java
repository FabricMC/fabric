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

package net.fabricmc.fabric.impl.extensibility.item.v1;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public class ExtensibilityApiEntry implements ClientModInitializer {
	public static final Queue<Item> tridentItems = new LinkedList<>();
	public static final Set<UUID> KNOWN_TRIDENT_UUIDS = new HashSet<>();

	private static World currentWorld = null;

	public void onInitializeClient() {
		ClientSidePacketRegistry.INSTANCE.register(new Identifier("fabric-extensibility-api-v1", "custom-trident-info-packet"), (packetContext, attachedData) -> {
			int itemId = attachedData.readInt();
			UUID uuid = attachedData.readUuid();

			System.out.println(itemId + " " + uuid);
			System.out.println(Registry.ITEM.get(itemId));

			if (!KNOWN_TRIDENT_UUIDS.contains(uuid)) {
				tridentItems.add(Registry.ITEM.get(itemId));
				KNOWN_TRIDENT_UUIDS.add(uuid);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if (client.world == null || client.world != currentWorld) {
				KNOWN_TRIDENT_UUIDS.clear();
				currentWorld = client.world;
			}
		});
	}
}
