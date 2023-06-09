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

package net.fabricmc.fabric.impl.resource.loader.reloader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderKeys;
import net.fabricmc.fabric.impl.base.event.NodeSorting;

public abstract class ReloaderSorting {
	private final Map<Identifier, ReloaderNode> nodes = new HashMap<>();

	public void addVanillaReloaders(List<ResourceReloader> vanillaReloaders) {
		// Add vanilla reloaders to the graph
		Identifier previousId = ResourceReloaderKeys.BEFORE_MINECRAFT;
		addVanillaNode(ReloaderNode.BEFORE_MINECRAFT, previousId, null);

		for (int i = 0; i < vanillaReloaders.size(); i++) {
			ResourceReloader reloader = vanillaReloaders.get(i);
			Identifier id = getVanillaReloaderId(reloader);

			addVanillaNode(i, id, reloader);
			addReloaderOrdering(previousId, id);

			previousId = id;
		}

		addVanillaNode(ReloaderNode.AFTER_MINECRAFT, ResourceReloaderKeys.AFTER_MINECRAFT, null);
		addReloaderOrdering(previousId, ResourceReloaderKeys.AFTER_MINECRAFT);
	}

	public List<ResourceReloader> getSortedReloaders() {
		List<ReloaderNode> nodeList = new ArrayList<>(nodes.values());
		NodeSorting.sort(nodeList, "resource reloaders");

		// We remove nodes that do not have a reloader (but might have been used to add ordering)
		return nodeList.stream().map(n -> n.reloader).filter(Objects::nonNull).toList();
	}

	/**
	 * Construct map of all (modded only!) reloaders by id.
	 */
	public void addReloadersToMap(Map<Identifier, ResourceReloader> map) {
		for (ReloaderNode node : nodes.values()) {
			if (node.vanillaIndex == ReloaderNode.MODDED && node.reloader != null) {
				map.put(node.identifier, node.reloader);
			}
		}
	}

	private void addVanillaNode(int vanillaIndex, Identifier identifier, @Nullable ResourceReloader reloader) {
		if (nodes.containsKey(identifier)) {
			throw new IllegalArgumentException("Duplicate reloader identifier: " + identifier);
		}

		ReloaderNode node = new ReloaderNode(vanillaIndex, identifier);
		node.reloader = reloader;
		nodes.put(identifier, node);
	}

	private ReloaderNode getOrCreateNode(Identifier identifier) {
		return nodes.computeIfAbsent(identifier, id -> new ReloaderNode(ReloaderNode.MODDED, id));
	}

	protected abstract Identifier getVanillaReloaderId(ResourceReloader reloader);

	public void addReloader(Identifier identifier, ResourceReloader reloader) {
		Objects.requireNonNull(identifier);
		Objects.requireNonNull(reloader);

		ReloaderNode node = getOrCreateNode(identifier);

		if (node.vanillaIndex != ReloaderNode.MODDED) {
			throw new IllegalArgumentException("Cannot register a reloader using a vanilla identifier");
		}

		if (node.identifier.equals(identifier) && node.reloader != null) {
			throw new IllegalArgumentException("Duplicate reloader identifier: " + identifier);
		}

		node.reloader = reloader;
	}

	public void addReloaderOrdering(Identifier firstReloader, Identifier secondReloader) {
		Objects.requireNonNull(firstReloader);
		Objects.requireNonNull(secondReloader);

		ReloaderNode node1 = getOrCreateNode(firstReloader);
		ReloaderNode node2 = getOrCreateNode(secondReloader);

		node1.subsequentNodes.add(node2);
		node2.previousNodes.add(node1);
	}

	public void addLegacyReloaders(List<LegacyReloaderHolder.Definition> definitions) {
		for (LegacyReloaderHolder.Definition definition : definitions) {
			Identifier id = definition.idSupplier().get();
			Collection<Identifier> dependencies = definition.dependencySupplier().get();

			addReloader(id, definition.reloader());

			for (Identifier dependency : dependencies) {
				addReloaderOrdering(dependency, id);
			}
		}
	}
}
