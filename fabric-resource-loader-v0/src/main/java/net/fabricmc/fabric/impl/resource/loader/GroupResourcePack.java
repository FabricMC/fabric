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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.ModResourcePack;

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
	public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
		List<ModResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

		if (packs != null) {
			for (int i = packs.size() - 1; i >= 0; i--) {
				ResourcePack pack = packs.get(i);
				InputSupplier<InputStream> supplier = pack.open(type, id);

				if (supplier != null) {
					return supplier;
				}
			}
		}

		return null;
	}

	@Override
	public void findResources(ResourceType type, String namespace, String prefix, class_7664 arg) {
		List<ModResourcePack> packs = this.namespacedPacks.get(namespace);

		for (int i = packs.size() - 1; i >= 0; i--) {
			ResourcePack pack = packs.get(i);

			pack.findResources(type, namespace, prefix, arg);
		}
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return this.namespacedPacks.keySet();
	}

	public void appendResources(ResourceType type, Identifier id, List<Resource> resources) {
		List<ModResourcePack> packs = this.namespacedPacks.get(id.getNamespace());

		if (packs == null) {
			return;
		}

		Identifier metadataId = NamespaceResourceManager.getMetadataPath(id);

		for (ModResourcePack pack : packs) {
			InputSupplier<InputStream> supplier = pack.open(type, id);

			if (supplier != null) {
				InputSupplier<ResourceMetadata> metadataSupplier = () -> {
					InputSupplier<InputStream> rawMetadataSupplier = pack.open(this.type, metadataId);
					return rawMetadataSupplier != null ? NamespaceResourceManager.method_45297(rawMetadataSupplier) : ResourceMetadata.NONE;
				};

				resources.add(new Resource(pack, supplier, metadataSupplier));
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
