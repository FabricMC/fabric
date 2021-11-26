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
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * A more optimized method to sync registry ids to client.
 * Produce smaller packet than old {@link NbtRegistryPacketSerializer nbt-based} method.
 *
 * <p>This method optimize the packet in multiple way:
 * <ul>
 *     <li>Directly write into the buffer instead of using an nbt;</li>
 *     <li>Group all {@link Identifier} with same namespace together and only send those unique namespaces once for each group;</li>
 *     <li>Group adjacent rawIds together and only send the difference of the first rawId and the last rawId of the bulk before.
 *     This is based on the assumption that mods generally register all of their object at once,
 *     therefore making the rawIds somewhat densely packed.</li>
 * </ul>
 */
public class DirectRegistryPacketSerializer implements RegistryPacketSerializer {
	public static final Identifier ID = new Identifier("fabric", "registry/sync/direct");

	DirectRegistryPacketSerializer() {
	}

	@Override
	public Identifier getPacketId() {
		return ID;
	}

	@Override
	public void writeBuffer(PacketByteBuf buf, Map<Identifier, Object2IntMap<Identifier>> map) {
		// Group registry ids with same namespace
		Map<String, List<Identifier>> regNamespaceGroups = map.keySet().stream()
				.collect(Collectors.groupingBy(Identifier::getNamespace));

		buf.writeVarInt(regNamespaceGroups.size());

		regNamespaceGroups.forEach((regNamespace, regIds) -> {
			buf.writeString(optimizeNamespace(regNamespace));
			buf.writeVarInt(regIds.size());

			for (Identifier regId : regIds) {
				buf.writeString(regId.getPath());

				Object2IntMap<Identifier> idMap = map.get(regId);

				// Sort object ids by raw id and group them by namespace
				Map<String, List<Object2IntMap.Entry<Identifier>>> idNamespaceGroups = idMap.object2IntEntrySet().stream()
						.sorted(Comparator.comparingInt(Object2IntMap.Entry::getIntValue))
						.collect(Collectors.groupingBy(e -> e.getKey().getNamespace(), LinkedHashMap::new, Collectors.toList()));

				buf.writeVarInt(idNamespaceGroups.size());

				int lastBulkLastRawId = 0;

				for (Map.Entry<String, List<Object2IntMap.Entry<Identifier>>> idNamespaceEntry : idNamespaceGroups.entrySet()) {
					Iterator<Object2IntMap.Entry<Identifier>> idPairs = idNamespaceEntry.getValue().iterator();

					// Group consecutive raw ids together
					List<List<Object2IntMap.Entry<Identifier>>> bulks = new ArrayList<>();

					List<Object2IntMap.Entry<Identifier>> currentBulk = new ArrayList<>();
					Object2IntMap.Entry<Identifier> currentPair = idPairs.next();
					currentBulk.add(currentPair);

					while (idPairs.hasNext()) {
						currentPair = idPairs.next();

						if (currentBulk.get(currentBulk.size() - 1).getIntValue() + 1 != currentPair.getIntValue()) {
							bulks.add(currentBulk);
							currentBulk = new ArrayList<>();
						}

						currentBulk.add(currentPair);
					}

					bulks.add(currentBulk);

					buf.writeString(optimizeNamespace(idNamespaceEntry.getKey()));
					buf.writeVarInt(bulks.size());

					for (List<Object2IntMap.Entry<Identifier>> bulk : bulks) {
						int firstRawId = bulk.get(0).getIntValue();
						int bulkRawIdStartDiff = firstRawId - lastBulkLastRawId;

						buf.writeVarInt(bulkRawIdStartDiff);
						buf.writeVarInt(bulk.size());

						for (Object2IntMap.Entry<Identifier> idPair : bulk) {
							buf.writeString(idPair.getKey().getPath());

							lastBulkLastRawId = idPair.getIntValue();
						}
					}
				}
			}
		});
	}

	@Override
	@Nullable
	public Map<Identifier, Object2IntMap<Identifier>> readBuffer(PacketByteBuf buf) {
		Map<Identifier, Object2IntMap<Identifier>> map = new LinkedHashMap<>();
		int regNamespaceGroupAmount = buf.readVarInt();

		for (int i = 0; i < regNamespaceGroupAmount; i++) {
			String regNamespace = buf.readString();
			int regNamespaceGroupLength = buf.readVarInt();

			for (int j = 0; j < regNamespaceGroupLength; j++) {
				String regPath = buf.readString();
				Object2IntMap<Identifier> idMap = new Object2IntLinkedOpenHashMap<>();
				int idNamespaceGroupAmount = buf.readVarInt();

				int lastBulkLastRawId = 0;

				for (int k = 0; k < idNamespaceGroupAmount; k++) {
					String idNamespace = buf.readString();
					int rawIdBulkAmount = buf.readVarInt();

					for (int l = 0; l < rawIdBulkAmount; l++) {
						int bulkRawIdStartDiff = buf.readVarInt();
						int bulkSize = buf.readVarInt();

						int currentRawId = (lastBulkLastRawId + bulkRawIdStartDiff) - 1;

						for (int m = 0; m < bulkSize; m++) {
							currentRawId++;
							String idPath = buf.readString();
							idMap.put(new Identifier(idNamespace, idPath), currentRawId);
						}

						lastBulkLastRawId = currentRawId;
					}
				}

				map.put(new Identifier(regNamespace, regPath), idMap);
			}
		}

		return map;
	}

	private String optimizeNamespace(String namespace) {
		return namespace.equals("minecraft") ? "" : namespace;
	}
}
