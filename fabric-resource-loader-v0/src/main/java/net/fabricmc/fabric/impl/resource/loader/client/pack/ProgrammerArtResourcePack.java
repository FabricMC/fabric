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

package net.fabricmc.fabric.impl.resource.loader.client.pack;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;

/**
 * Represents the Programmer Art resource pack with support for modded content.
 *
 * <p>Any vanilla resources are provided like in Vanilla through the original programmer art, any missing resources
 * will be searched in the provided modded resource packs.
 */
@Environment(EnvType.CLIENT)
public class ProgrammerArtResourcePack extends GroupResourcePack {
	private final AbstractFileResourcePack originalResourcePack;

	public ProgrammerArtResourcePack(AbstractFileResourcePack originalResourcePack, List<ModResourcePack> modResourcePacks) {
		super(ResourceType.CLIENT_RESOURCES, modResourcePacks);
		this.originalResourcePack = originalResourcePack;
	}

	@Override
	public InputStream openRoot(String fileName) throws IOException {
		if (!fileName.contains("/") && !fileName.contains("\\")) {
			// There should be nothing to read at the root of mod's Programmer Art extensions.
			return this.originalResourcePack.openRoot(fileName);
		} else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		if (this.originalResourcePack.contains(type, id)) {
			return this.originalResourcePack.open(type, id);
		}

		return super.open(type, id);
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		Set<Identifier> resources = new HashSet<>(this.originalResourcePack.findResources(type, namespace, prefix, maxDepth, pathFilter));

		resources.addAll(super.findResources(type, namespace, prefix, maxDepth, pathFilter));

		return resources;
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		return this.originalResourcePack.contains(type, id) || super.contains(type, id);
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		Set<String> namespaces = this.originalResourcePack.getNamespaces(type);

		namespaces.addAll(super.getNamespaces(type));

		return namespaces;
	}

	@Override
	public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return this.originalResourcePack.parseMetadata(metaReader);
	}

	@Override
	public String getName() {
		return "Programmer Art";
	}

	@Override
	public void close() {
		this.originalResourcePack.close();
		super.close();
	}
}
