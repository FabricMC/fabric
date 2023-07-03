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

package net.fabricmc.fabric.impl.client.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;

/**
 * Represents a vanilla built-in resource pack with support for modded content.
 *
 * <p>Vanilla resources are provided as usual through the original resource pack (if not overridden),
 * all other resources will be searched for in the provided modded resource packs.</p>
 */
public class FabricWrappedVanillaResourcePack extends GroupResourcePack {
	private final AbstractFileResourcePack originalResourcePack;

	public FabricWrappedVanillaResourcePack(AbstractFileResourcePack originalResourcePack, List<ModResourcePack> modResourcePacks) {
		// Mod resource packs have higher priority, add them last (so vanilla assets can be overridden)
		super(ResourceType.CLIENT_RESOURCES, Stream.concat(Stream.of(originalResourcePack), modResourcePacks.stream()).toList());
		this.originalResourcePack = originalResourcePack;
	}

	@Override
	public InputSupplier<InputStream> openRoot(String... pathSegments) {
		return this.originalResourcePack.openRoot(pathSegments);
	}

	@Override
	public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return this.originalResourcePack.parseMetadata(metaReader);
	}

	@Override
	public String getName() {
		return this.originalResourcePack.getName();
	}
}
