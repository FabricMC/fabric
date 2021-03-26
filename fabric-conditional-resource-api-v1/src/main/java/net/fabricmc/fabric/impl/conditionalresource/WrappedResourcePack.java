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

package net.fabricmc.fabric.impl.conditionalresource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.conditionalresource.v1.ResourceConditions;

public class WrappedResourcePack implements ResourcePack {
	private static final String CONDITIONS_EXTENSION = ".fabricmeta";
	private static final Logger LOGGER = LogManager.getLogger();

	private final ResourcePack parent;
	private Set<String> hidden;

	public WrappedResourcePack(ResourcePack parent) {
		this.parent = parent;
	}

	public void fabric_indexFabricMeta(ResourceType type, String namespace) {
		hidden = new HashSet<>();

		for (Identifier fabricMeta : parent.findResources(type, namespace, ".", 2147483647, s -> s.endsWith(CONDITIONS_EXTENSION))) {
			try (InputStream stream = parent.open(type, fabricMeta)) {
				JsonElement element = Streams.parse(new JsonReader(new InputStreamReader(stream)));

				try {
					if (!ResourceConditions.evaluate(fabricMeta, element)) {
						String s = fabricMeta.toString();
						hidden.add(s.substring(0, s.length() - CONDITIONS_EXTENSION.length()));
					}
				} catch (Exception e) {
					LOGGER.error("[Fabric] Failed to evaluate conditions for {}: {}", fabricMeta, element);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public InputStream openRoot(String fileName) throws IOException {
		return parent.openRoot(fileName);
	}

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		return parent.open(type, id);
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		Collection<Identifier> resources = parent.findResources(type, namespace, prefix, maxDepth, pathFilter);
		resources.removeIf(this::isHidden);
		return resources;
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		return !isHidden(id) && parent.contains(type, id);
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return parent.getNamespaces(type);
	}

	@Override
	public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return parent.parseMetadata(metaReader);
	}

	@Override
	public String getName() {
		return parent.getName();
	}

	@Override
	public void close() {
		parent.close();
		hidden.clear();
	}

	private boolean isHidden(Identifier resourceId) {
		if (resourceId.getPath().endsWith(CONDITIONS_EXTENSION)) {
			return true;
		}

		String s = resourceId.toString();
		return hidden != null && (hidden.contains(s.substring(0, s.lastIndexOf('/') + 1)) || hidden.contains(s));
	}
}
