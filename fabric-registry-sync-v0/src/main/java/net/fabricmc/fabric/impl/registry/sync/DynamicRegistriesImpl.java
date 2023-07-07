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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Unmodifiable;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SerializableRegistries;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.impl.base.toposort.NodeSorting;
import net.fabricmc.fabric.impl.base.toposort.SortableNode;

public final class DynamicRegistriesImpl {
	private static final Set<RegistryKey<? extends Registry<?>>> DYNAMIC_REGISTRY_KEYS = new HashSet<>();
	private static final Map<RegistryKey<? extends Registry<?>>, SettingsImpl<?>> SETTINGS = new HashMap<>();
	private static volatile List<RegistryLoader.Entry<?>> sortedRegistries = null;

	static {
		for (RegistryLoader.Entry<?> vanillaEntry : RegistryLoader.DYNAMIC_REGISTRIES) {
			DYNAMIC_REGISTRY_KEYS.add(vanillaEntry.key());
		}
	}

	private DynamicRegistriesImpl() {
	}

	public static @Unmodifiable List<RegistryLoader.Entry<?>> getDynamicRegistries() {
		List<RegistryLoader.Entry<?>> ret = sortedRegistries;

		if (ret == null) {
			sortedRegistries = ret = sort();
		}

		return ret;
	}

	private static List<RegistryLoader.Entry<?>> sort() {
		Map<RegistryKey<? extends Registry<?>>, RegistryNode> nodes = new HashMap<>(RegistryLoader.DYNAMIC_REGISTRIES.size() + SETTINGS.size());

		// Add vanilla nodes with their ordering
		int vanillaIndex = 0;
		RegistryNode previousVanillaNode = null;

		for (RegistryLoader.Entry<?> vanillaEntry : RegistryLoader.DYNAMIC_REGISTRIES) {
			RegistryNode node = new RegistryNode(vanillaEntry, vanillaIndex++);
			nodes.put(vanillaEntry.key(), node);

			if (previousVanillaNode != null) {
				link(previousVanillaNode, node);
			}

			previousVanillaNode = node;
		}

		// Add modded nodes
		for (SettingsImpl<?> settings : SETTINGS.values()) {
			RegistryNode node = nodes.computeIfAbsent(settings.owner.key(), k -> new RegistryNode(settings.owner, RegistryNode.MODDED_INDEX));
			nodes.put(settings.owner.key(), node);
		}

		// Add modded ordering
		for (SettingsImpl<?> settings : SETTINGS.values()) {
			RegistryNode node = nodes.get(settings.owner.key());

			for (RegistryKey<? extends Registry<?>> before : settings.before) {
				RegistryNode other = nodes.get(before);

				if (other == null) {
					throw new IllegalStateException("Registry " + settings.owner.key() + " has a dependency on " + before + ", which does not exist!");
				}

				link(node, other);
			}

			for (RegistryKey<? extends Registry<?>> after : settings.after) {
				RegistryNode other = nodes.get(after);

				if (other == null) {
					throw new IllegalStateException("Registry " + settings.owner.key() + " has a dependency on " + after + ", which does not exist!");
				}

				link(other, node);
			}
		}

		// Sort everything
		List<RegistryNode> nodesToSort = new ArrayList<>(nodes.values());
		NodeSorting.sort(nodesToSort, "dynamic registries", RegistryNode.COMPARATOR);

		for (RegistryNode node : nodesToSort) {
			System.out.println("Sorted node: " + node.entry.key());
		}

		return nodesToSort.stream().map(node -> node.entry).collect(Collectors.toUnmodifiableList());
	}

	private static void link(RegistryNode node1, RegistryNode node2) {
		node1.subsequentNodes.add(node2);
		node2.previousNodes.add(node1);
	}

	public static <T> DynamicRegistries.Settings<T> register(RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
		Objects.requireNonNull(key, "Registry key cannot be null");
		Objects.requireNonNull(codec, "Codec cannot be null");

		if (!DYNAMIC_REGISTRY_KEYS.add(key)) {
			throw new IllegalArgumentException("Dynamic registry " + key + " has already been registered!");
		}

		var entry = new RegistryLoader.Entry<>(key, codec);
		// TODO: may not be thread-safe
		sortedRegistries = null;
		var settings = new SettingsImpl<>(entry);
		SETTINGS.put(key, settings);
		return settings;
	}

	private static <T> void addSyncedRegistry(RegistryKey<? extends Registry<T>> registryKey, Codec<T> networkCodec) {
		if (!(SerializableRegistries.REGISTRIES instanceof HashMap<?, ?>)) {
			SerializableRegistries.REGISTRIES = new HashMap<>(SerializableRegistries.REGISTRIES);
		}

		SerializableRegistries.REGISTRIES.put(registryKey, new SerializableRegistries.Info<>(registryKey, networkCodec));
	}

	private static final class SettingsImpl<T> extends DynamicRegistries.Settings<T> {
		private final RegistryLoader.Entry<T> owner;
		private final Set<RegistryKey<? extends Registry<?>>> before = new HashSet<>();
		private final Set<RegistryKey<? extends Registry<?>>> after = new HashSet<>();
		private boolean synced = false;

		private SettingsImpl(RegistryLoader.Entry<T> owner) {
			this.owner = owner;
		}

		@Override
		public DynamicRegistries.Settings<T> synced() {
			return synced(owner.elementCodec());
		}

		@Override
		public DynamicRegistries.Settings<T> synced(Codec<T> networkCodec) {
			Objects.requireNonNull(networkCodec, "Network codec cannot be null");

			if (synced) {
				throw new IllegalStateException("Registry " + owner.key() + " has already been marked as synced!");
			}

			this.synced = true;
			addSyncedRegistry(owner.key(), networkCodec);
			return this;
		}

		@Override
		public DynamicRegistries.Settings<T> sortBefore(RegistryKey<? extends Registry<?>> before) {
			Objects.requireNonNull(before, "Registry key to sort before");
			this.before.add(before);
			sortedRegistries = null;
			return this;
		}

		@Override
		public DynamicRegistries.Settings<T> sortAfter(RegistryKey<? extends Registry<?>> after) {
			Objects.requireNonNull(after, "Registry key to sort after");
			this.after.add(after);
			sortedRegistries = null;
			return this;
		}
	}

	private static final class RegistryNode extends SortableNode<RegistryNode> {
		private static final Comparator<RegistryNode> COMPARATOR = Comparator.<RegistryNode>comparingInt(node -> node.vanillaIndex)
				.thenComparing(node -> node.entry.key().getValue());
		private static final int MODDED_INDEX = 1000; // modded registries go after vanilla by default

		private final RegistryLoader.Entry<?> entry;
		private final int vanillaIndex;

		private RegistryNode(RegistryLoader.Entry<?> entry, int vanillaIndex) {
			this.entry = entry;
			this.vanillaIndex = vanillaIndex;
		}

		@Override
		protected String getDescription() {
			return entry.key().toString();
		}
	}
}
