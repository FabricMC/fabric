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

package net.fabricmc.fabric.impl.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.mixin.resource.loader.NamespaceResourceManagerAccessor;

/**
 * Represents a group resource pack, holds multiple resource packs as one.
 */
public abstract class GroupResourcePack implements ResourcePack {
	protected final ResourceType type;
	protected final List<ModResourcePack> packs;
	protected final Map<String, List<ModResourcePack>> namespacedPacks = new Object2ObjectOpenHashMap<>();

	public GroupResourcePack(ResourceType type, List<ModResourcePack> packs) {
		this.type = type;
		this.packs = packs;
		this.packs.forEach(pack -> pack.getNamespaces(this.type)
				.forEach(namespace -> this.namespacedPacks.computeIfAbsent(namespace, value -> new ArrayList<>())
						.add(pack)));
	}

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		List<ModResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

		if (packs != null) {
			for (int i = packs.size() - 1; i >= 0; i--) {
				ResourcePack pack = packs.get(i);

				if (pack.contains(type, id)) {
					return pack.open(type, id);
				}
			}
		}

		throw new ResourceNotFoundException(null,
				String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath()));
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		List<ModResourcePack> packs = this.namespacedPacks.get(namespace);

		if (packs == null) {
			return Collections.emptyList();
		}

		Set<Identifier> resources = new HashSet<>();

		for (int i = packs.size() - 1; i >= 0; i--) {
			ResourcePack pack = packs.get(i);
			Collection<Identifier> modResources = pack.findResources(type, namespace, prefix, maxDepth, pathFilter);

			resources.addAll(modResources);
		}

		return resources;
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		List<ModResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

		if (packs == null) {
			return false;
		}

		for (int i = packs.size() - 1; i >= 0; i--) {
			ResourcePack pack = packs.get(i);

			if (pack.contains(type, id)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return this.namespacedPacks.keySet();
	}

	public void appendResources(NamespaceResourceManagerAccessor manager, Identifier id, List<Resource> resources) throws IOException {
		List<ModResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

		if (packs == null) {
			return;
		}

		Identifier metadataId = NamespaceResourceManagerAccessor.fabric$accessor_getMetadataPath(id);

		for (ModResourcePack pack : packs) {
			if (pack.contains(manager.getType(), id)) {
				InputStream metadataInputStream = pack.contains(manager.getType(), metadataId) ? manager.fabric$accessor_open(metadataId, pack) : null;
				resources.add(new ResourceImpl(pack.getName(), id, manager.fabric$accessor_open(id, pack), metadataInputStream));
			}
		}
	}

	public String getFullName() {
		return this.getName() + " (" + this.packs.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")) + ")";
	}

	@Override
	public void close() {
		this.packs.forEach(ResourcePack::close);
	}
}
