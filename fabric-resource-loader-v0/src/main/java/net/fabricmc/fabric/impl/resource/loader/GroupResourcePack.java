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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.mixin.resource.loader.NamespaceResourceManagerAccessor;

public abstract class GroupResourcePack implements ResourcePack {
	protected List<ModResourcePack> packs;

	public GroupResourcePack(List<ModResourcePack> packs) {
		this.packs = packs;
	}

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		for (int i = this.packs.size() - 1; i >= 0; i--) {
			ResourcePack pack = this.packs.get(i);

			if (pack.contains(type, id)) {
				return pack.open(type, id);
			}
		}

		throw new ResourceNotFoundException(null,
				String.format("%s/%s/%s", type.getDirectory(), id.getNamespace(), id.getPath()));
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		Set<Identifier> resources = new HashSet<>();

		for (int i = this.packs.size() - 1; i >= 0; i--) {
			ResourcePack pack = this.packs.get(i);
			Collection<Identifier> modResources = pack.findResources(type, namespace, prefix, maxDepth, pathFilter);

			resources.addAll(modResources);
		}

		return resources;
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		for (int i = this.packs.size() - 1; i >= 0; i--) {
			ResourcePack pack = this.packs.get(i);

			if (pack.contains(type, id)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		Set<String> namespaces = new HashSet<>();

		for (int i = this.packs.size() - 1; i >= 0; i--) {
			ResourcePack pack = this.packs.get(i);
			namespaces.addAll(pack.getNamespaces(type));
		}

		return namespaces;
	}

	public void appendResources(NamespaceResourceManagerAccessor manager, Identifier id, List<Resource> resources) throws IOException {
		for (ModResourcePack pack : this.packs) {
			if (pack.contains(manager.getType(), id)) {
				InputStream inputStream = pack.contains(manager.getType(), id) ? manager.fabric$open(id, pack) : null;
				resources.add(new ResourceImpl(pack.getName(), id, manager.fabric$open(id, pack), inputStream));
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
