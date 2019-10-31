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

package net.fabricmc.fabric.impl.registry.sync;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public class RegistrySerialization {
	private static final Set<Identifier> REGISTRY_BLACKLIST = ImmutableSet.of();
	private static final Set<Identifier> REGISTRY_BLACKLIST_NETWORK = ImmutableSet.of();

	private static List<Identifier> getSerializableRegistries(boolean isClientSync) {
		return Registry.REGISTRIES.stream()
			.filter(registry -> registry instanceof RemappableRegistry)
			.map(Registry.REGISTRIES::getId)
			.filter(registryId -> !REGISTRY_BLACKLIST.contains(registryId))
			.filter(registryId -> !isClientSync || !REGISTRY_BLACKLIST_NETWORK.contains(registryId))
			.collect(Collectors.toList());
	}

	private static <T> CompoundTag writeRegistryToTag(Registry<T> registry) {
		CompoundTag registryTag = new CompoundTag();

		for (T o : registry) {
			Identifier id = registry.getId(o);
			int rawId = registry.getRawId(o);

			registryTag.putInt(id.toString(), rawId);
		}

		return registryTag;
	}

	public static CompoundTag toTag(boolean isClientSync) {
		CompoundTag registriesTag = new CompoundTag();

		for (Identifier registryId : getSerializableRegistries(isClientSync)) {
			MutableRegistry<?> registry = Registry.REGISTRIES.get(registryId);
			registriesTag.put(registryId.toString(), writeRegistryToTag(registry));
		}

		CompoundTag tag = new CompoundTag();
		tag.putInt("version", 1);
		tag.put("registries", registriesTag);

		return tag;
	}

	public static Map<Identifier, Object2IntMap<Identifier>> fromTag(CompoundTag tag) {
		Map<Identifier, Object2IntMap<Identifier>> result = new HashMap<>();
		CompoundTag mainTag = tag.getCompound("registries");

		for (String registryKey : mainTag.getKeys()) {
			CompoundTag registryTag = mainTag.getCompound(registryKey);
			Identifier registryId = new Identifier(registryKey);
			MutableRegistry registry = Registry.REGISTRIES.get(registryId);

			if (registry instanceof RemappableRegistry) {
				Object2IntMap<Identifier> idMap = new Object2IntOpenHashMap<>();

				for (String key : registryTag.getKeys()) {
					idMap.put(new Identifier(key), registryTag.getInt(key));
				}

				result.put(registryId, idMap);
			}
		}

		return result;
	}

	private static <T> void writeRegistryToBuf(PacketByteBuf buf, Registry<T> registry) {
		// Registry entries tend to share namespaces. We therefore group the registry entries by namespace.
		// This allows us to write the name of each namespace only once.
		// We can generally assume that the raw IDs of objects within a namespace are dense.
		Map<String, List<T>> namespaces = registry.stream()
				.collect(Collectors.groupingBy(o -> registry.getId(o).getNamespace()));

		buf.writeVarInt(namespaces.size());

		for (Map.Entry<String, List<T>> namespaceEntry : namespaces.entrySet()) {
			String namespace = namespaceEntry.getKey();
			List<T> objects = namespaceEntry.getValue();
			// We are going to use differential encoding of the raw IDS. Sort the objects
			objects.sort(Comparator.comparingInt(registry::getRawId));

			int prevRawId = 0;
			buf.writeString(namespace);
			buf.writeVarInt(objects.size());

			for (T object : objects) {
				Identifier id = registry.getId(object);
				int rawId = registry.getRawId(object);

				buf.writeString(id.getPath());
				buf.writeVarInt(rawId - prevRawId);
				prevRawId = rawId;
			}
		}
	}

	public static void toBuf(PacketByteBuf buf, boolean isClientSync) {
		List<Identifier> registryIds = getSerializableRegistries(isClientSync);

		buf.writeInt(registryIds.size());

		for (Identifier registryId : registryIds) {
			MutableRegistry<?> registry = Registry.REGISTRIES.get(registryId);
			buf.writeIdentifier(registryId);
			writeRegistryToBuf(buf, registry);
		}
	}

	public static Map<Identifier, Object2IntMap<Identifier>> fromBuf(PacketByteBuf buf) {
		Map<Identifier, Object2IntMap<Identifier>> result = new HashMap<>();
		int numRegistries = buf.readVarInt();

		for (int i = 0; i < numRegistries; i++) {
			Identifier registryId = buf.readIdentifier();
			int numNamespaces = buf.readVarInt();
			Object2IntMap<Identifier> idMap = new Object2IntOpenHashMap<>();

			for (int j = 0; j < numNamespaces; j++) {
				String namespace = buf.readString(32767);
				int numObjects = buf.readInt();
				int rawId = 0;

				for (int k = 0; k < numObjects; k++) {
					String path = buf.readString(32767);
					rawId += buf.readVarInt();
					idMap.put(new Identifier(namespace, path), rawId);
				}
			}

			result.put(registryId, idMap);
		}

		return result;
	}
}
