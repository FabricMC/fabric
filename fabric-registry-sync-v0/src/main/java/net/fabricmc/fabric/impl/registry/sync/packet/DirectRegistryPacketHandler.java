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

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

/**
 * A more optimized method to sync registry ids to client.
 * Produce smaller packet than old {@link NbtRegistryPacketHandler nbt-based} method.
 *
 * <p>This method optimize the packet in multiple way:
 * <ul>
 *     <li>Directly write into the buffer instead of using an nbt;</li>
 *     <li>Group all {@link Identifier} with same namespace together and only send those unique namespaces once for each group;</li>
 *     <li>Group consecutive rawIds together and only send the difference of the first rawId and the last rawId of the bulk before.
 *     This is based on the assumption that mods generally register all of their object at once,
 *     therefore making the rawIds somewhat densely packed.</li>
 * </ul>
 *
 * <p>This method also split into multiple packets if it exceeds the limit, defaults to 1 MB.
 */
public class DirectRegistryPacketHandler extends RegistryPacketHandler {
	/**
	 * @see net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket#MAX_PAYLOAD_SIZE
	 */
	@SuppressWarnings("JavadocReference")
	private static final int MAX_PAYLOAD_SIZE = Integer.getInteger("fabric.registry.direct.maxPayloadSize", 0x100000);
	private static final Identifier ID = new Identifier("fabric", "registry/sync/direct");

	@Nullable
	private PacketByteBuf combinedBuf;

	@Nullable
	private Map<Identifier, Object2IntMap<Identifier>> syncedRegistryMap;

	private boolean isPacketFinished = false;
	private int totalPacketReceived = 0;

	@Override
	public Identifier getPacketId() {
		return ID;
	}

	@Override
	public void sendPacket(ServerPlayerEntity player, Map<Identifier, Object2IntMap<Identifier>> registryMap) {
		PacketByteBuf buf = PacketByteBufs.create();

		// Group registry ids with same namespace.
		Map<String, List<Identifier>> regNamespaceGroups = registryMap.keySet().stream()
				.collect(Collectors.groupingBy(Identifier::getNamespace));

		buf.writeVarInt(regNamespaceGroups.size());

		regNamespaceGroups.forEach((regNamespace, regIds) -> {
			buf.writeString(optimizeNamespace(regNamespace));
			buf.writeVarInt(regIds.size());

			for (Identifier regId : regIds) {
				buf.writeString(regId.getPath());

				Object2IntMap<Identifier> idMap = registryMap.get(regId);

				// Sort object ids by its namespace. We use linked map here to keep the original namespace ordering.
				Map<String, List<Object2IntMap.Entry<Identifier>>> idNamespaceGroups = idMap.object2IntEntrySet().stream()
						.collect(Collectors.groupingBy(e -> e.getKey().getNamespace(), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));

				buf.writeVarInt(idNamespaceGroups.size());

				int lastBulkLastRawId = 0;

				for (Map.Entry<String, List<Object2IntMap.Entry<Identifier>>> idNamespaceEntry : idNamespaceGroups.entrySet()) {
					// Make sure the ids are sorted by its raw id.
					List<Object2IntMap.Entry<Identifier>> idPairs = idNamespaceEntry.getValue();
					idPairs.sort(Comparator.comparingInt(Object2IntMap.Entry::getIntValue));

					// Group consecutive raw ids together.
					List<List<Object2IntMap.Entry<Identifier>>> bulks = new ArrayList<>();

					Iterator<Object2IntMap.Entry<Identifier>> idPairIter = idPairs.iterator();
					List<Object2IntMap.Entry<Identifier>> currentBulk = new ArrayList<>();
					Object2IntMap.Entry<Identifier> currentPair = idPairIter.next();
					currentBulk.add(currentPair);

					while (idPairIter.hasNext()) {
						currentPair = idPairIter.next();

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

		// Split the packet to multiple MAX_PAYLOAD_SIZEd buffers.
		int readableBytes = buf.readableBytes();
		int sliceIndex = 0;

		while (sliceIndex < readableBytes) {
			int sliceSize = Math.min(readableBytes - sliceIndex, MAX_PAYLOAD_SIZE);
			PacketByteBuf slicedBuf = PacketByteBufs.slice(buf, sliceIndex, sliceSize);
			sendPacket(player, slicedBuf);
			sliceIndex += sliceSize;
		}

		// Send an empty buffer to mark the end of the split.
		sendPacket(player, PacketByteBufs.empty());
	}

	@Override
	public void receivePacket(PacketByteBuf slicedBuf) {
		Preconditions.checkState(!isPacketFinished);
		totalPacketReceived++;

		if (combinedBuf == null) {
			combinedBuf = PacketByteBufs.create();
		}

		if (slicedBuf.readableBytes() != 0) {
			combinedBuf.writeBytes(slicedBuf);
			return;
		}

		isPacketFinished = true;

		computeBufSize(combinedBuf);
		syncedRegistryMap = new LinkedHashMap<>();
		int regNamespaceGroupAmount = combinedBuf.readVarInt();

		for (int i = 0; i < regNamespaceGroupAmount; i++) {
			String regNamespace = combinedBuf.readString();
			int regNamespaceGroupLength = combinedBuf.readVarInt();

			for (int j = 0; j < regNamespaceGroupLength; j++) {
				String regPath = combinedBuf.readString();
				Object2IntMap<Identifier> idMap = new Object2IntLinkedOpenHashMap<>();
				int idNamespaceGroupAmount = combinedBuf.readVarInt();

				int lastBulkLastRawId = 0;

				for (int k = 0; k < idNamespaceGroupAmount; k++) {
					String idNamespace = combinedBuf.readString();
					int rawIdBulkAmount = combinedBuf.readVarInt();

					for (int l = 0; l < rawIdBulkAmount; l++) {
						int bulkRawIdStartDiff = combinedBuf.readVarInt();
						int bulkSize = combinedBuf.readVarInt();

						int currentRawId = (lastBulkLastRawId + bulkRawIdStartDiff) - 1;

						for (int m = 0; m < bulkSize; m++) {
							currentRawId++;
							String idPath = combinedBuf.readString();
							idMap.put(new Identifier(idNamespace, idPath), currentRawId);
						}

						lastBulkLastRawId = currentRawId;
					}
				}

				syncedRegistryMap.put(new Identifier(regNamespace, regPath), idMap);
			}
		}

		combinedBuf.release();
		combinedBuf = null;
	}

	@Override
	public boolean isPacketFinished() {
		return isPacketFinished;
	}

	@Override
	public int getTotalPacketReceived() {
		Preconditions.checkState(isPacketFinished);
		return totalPacketReceived;
	}

	@Override
	@Nullable
	public Map<Identifier, Object2IntMap<Identifier>> getSyncedRegistryMap() {
		Preconditions.checkState(isPacketFinished);
		Map<Identifier, Object2IntMap<Identifier>> map = syncedRegistryMap;
		isPacketFinished = false;
		totalPacketReceived = 0;
		syncedRegistryMap = null;
		return map;
	}

	private String optimizeNamespace(String namespace) {
		return namespace.equals(Identifier.DEFAULT_NAMESPACE) ? "" : namespace;
	}
}
