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

package net.fabricmc.fabric.impl.resources;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FabricCombinedResourcePack implements CustomInjectionResourcePack {

	private final PackResourceMetadata packMeta;
	private final List<? extends ModResourcePack> components;

	public FabricCombinedResourcePack(Text description, List<? extends ModResourcePack> components) {
		this.packMeta = new PackResourceMetadata(description, ModResourcePackUtil.PACK_FORMAT_VERSION);
		this.components = components;
	}

	@Override
	public Stream<? extends ResourcePack> injectPacks() {
		return Stream.concat(Stream.of(this), components.stream());
	}

	@Override
	public InputStream openRoot(String var1) throws IOException {
		return null;
	}

	@Override
	public InputStream open(ResourceType type, Identifier path) throws IOException {
		return null;
	}

	@Override
	public Collection<Identifier> findResources(ResourceType var1, String var2, int var3, Predicate<String> var4) {
		return Collections.emptyList();
	}

	@Override
	public boolean contains(ResourceType var1, Identifier var2) {
		return false;
	}

	@Override
	public Set<String> getNamespaces(ResourceType var1) {
		Set<String> namespaces = new LinkedHashSet<>();
		for (ModResourcePack pack : components) {
			namespaces.addAll(pack.getNamespaces(var1));
		}
		return namespaces;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T parseMetadata(ResourceMetadataReader<T> var1) throws IOException {
		if (var1 == PackResourceMetadata.READER) {
			return (T) packMeta;
		}
		return null;
	}

	@Override
	public String getName() {
		return "Fabric Combined";
	}

	@Override
	public void close() throws IOException {
	}
}
