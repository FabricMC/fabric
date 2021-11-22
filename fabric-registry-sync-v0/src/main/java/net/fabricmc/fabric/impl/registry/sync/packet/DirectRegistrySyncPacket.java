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

package net.fabricmc.fabric.impl.registry.sync.packet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.registry.sync.map.IdMap;
import net.fabricmc.fabric.impl.registry.sync.map.RegistryMap;

/**
 * A more optimized method to sync registry ids to client.
 * Produce smaller packet than old {@link NbtRegistrySyncPacket nbt-based} method.
 *
 * <p>This method optimize the packet in multiple way:
 * <ul>
 *     <li>Directly write into the buffer instead of using an nbt;</li>
 *     <li>Group all {@link Identifier} with same namespace together and only send those unique namespaces once for each group;</li>
 *     <li>Group adjacent rawIds together and only send the last rawId and size of the group.
 *     This is based on the assumption that mods generally register all of their object at once,
 *     therefore making the rawIds somewhat densely packed.</li>
 * </ul>
 */
public class DirectRegistrySyncPacket implements RegistrySyncPacket {
	public static final Identifier ID = new Identifier("fabric", "registry/sync/direct");

	private static final DirectRegistrySyncPacket INSTANCE = new DirectRegistrySyncPacket();

	private DirectRegistrySyncPacket() {
	}

	public static DirectRegistrySyncPacket getInstance() {
		return INSTANCE;
	}

	@Override
	public Identifier getPacketId() {
		return ID;
	}

	@Override
	public void writeBuffer(PacketByteBuf buf, RegistryMap map) {
		// Group registry ids with same namespace
		Map<String, List<Identifier>> regNamespaceGroups = map.keySet().stream()
				.collect(Collectors.groupingBy(Identifier::getNamespace));

		buf.writeVarInt(regNamespaceGroups.size());

		regNamespaceGroups.forEach((regNamespace, regIds) -> {
			buf.writeString(regNamespace);
			buf.writeVarInt(regIds.size());

			for (Identifier regId : regIds) {
				buf.writeString(regId.getPath());

				IdMap idMap = map.get(regId);

				// Group object ids with name namespace
				Map<String, Int2ObjectMap<List<String>>> idNamespaceGroups = new LinkedHashMap<>();

				for (Object2IntMap.Entry<Identifier> idPair : idMap.object2IntEntrySet()) {
					Identifier id = idPair.getKey();
					int rawId = idPair.getIntValue();

					Int2ObjectMap<List<String>> adjacentRawIds = idNamespaceGroups.computeIfAbsent(getNamespace(id), s -> new Int2ObjectLinkedOpenHashMap<>());

					// Group adjacent rawIds together
					List<String> rawIdGroup;

					if (adjacentRawIds.containsKey(rawId - 1)) {
						rawIdGroup = adjacentRawIds.remove(rawId - 1);
					} else {
						rawIdGroup = new ArrayList<>();
					}

					rawIdGroup.add(id.getPath());
					adjacentRawIds.put(rawId, rawIdGroup);
				}

				buf.writeVarInt(idNamespaceGroups.size());

				idNamespaceGroups.forEach((idNamespace, adjacentRawIds) -> {
					buf.writeString(idNamespace);
					buf.writeVarInt(adjacentRawIds.size());

					for (Int2ObjectMap.Entry<List<String>> entry : adjacentRawIds.int2ObjectEntrySet()) {
						int lastRawId = entry.getIntKey();
						List<String> adjacentPaths = entry.getValue();
						buf.writeVarInt(lastRawId);
						buf.writeVarInt(adjacentPaths.size());

						for (String path : adjacentPaths) {
							buf.writeString(path);
						}
					}
				});
			}
		});
	}

	@Override
	@Nullable
	public RegistryMap readBuffer(PacketByteBuf buf) {
		RegistryMap map = new RegistryMap();
		int regNamespaceGroupAmount = buf.readVarInt();

		for (int i = 0; i < regNamespaceGroupAmount; i++) {
			String regNamespace = buf.readString();
			int regNamespaceGroupLength = buf.readVarInt();

			for (int j = 0; j < regNamespaceGroupLength; j++) {
				String regPath = buf.readString();
				IdMap idMap = new IdMap();
				int idNamespaceGroupAmount = buf.readVarInt();

				for (int k = 0; k < idNamespaceGroupAmount; k++) {
					String idNamespace = buf.readString();
					int idNamespaceGroupLength = buf.readVarInt();

					for (int l = 0; l < idNamespaceGroupLength; l++) {
						int lastRawId = buf.readVarInt();
						int rawIdGroupLength = buf.readVarInt();
						int firstRawId = lastRawId - (rawIdGroupLength - 1);

						for (int m = 0; m < rawIdGroupLength; m++) {
							String idPath = buf.readString();
							idMap.put(new Identifier(idNamespace, idPath), firstRawId + m);
						}
					}
				}

				map.put(new Identifier(regNamespace, regPath), idMap);
			}
		}

		return map;
	}

	private String getNamespace(Identifier id) {
		String namespace = id.getNamespace();

		if (namespace.equals("minecraft")) {
			namespace = "";
		}

		return namespace;
	}
}
