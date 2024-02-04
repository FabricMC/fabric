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
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.SharedConstants;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record PlaceholderResourcePack(ResourceType type) implements ResourcePack {
	private static final Text DESCRIPTION_TEXT = Text.translatable("pack.description.modResources");

	public PackResourceMetadata getMetadata() {
		return ModResourcePackUtil.getMetadataPack(
				SharedConstants.getGameVersion().getResourceVersion(type),
				DESCRIPTION_TEXT
		);
	}

	@Nullable
	@Override
	public InputSupplier<InputStream> openRoot(String... segments) {
		if (segments.length > 0) {
			switch (segments[0]) {
			case "pack.mcmeta":
				return () -> {
					String metadata = ModResourcePackUtil.GSON.toJson(PackResourceMetadata.SERIALIZER.toJson(getMetadata()));
					return IOUtils.toInputStream(metadata, StandardCharsets.UTF_8);
				};
			case "pack.png":
				return ModResourcePackUtil::getDefaultIcon;
			}
		}

		return null;
	}

	/**
	 * This pack has no actual contents.
	 */
	@Nullable
	@Override
	public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
		return null;
	}

	@Override
	public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return Collections.emptySet();
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
		return ResourceMetadataMap.of(PackResourceMetadata.SERIALIZER, getMetadata()).get(metaReader);
	}

	@Override
	public String getName() {
		return ModResourcePackCreator.FABRIC;
	}

	@Override
	public void close() {
	}

	public record Factory(ResourceType type) implements ResourcePackProfile.PackFactory {
		@Override
		public ResourcePack open(String name) {
			return new PlaceholderResourcePack(this.type);
		}

		@Override
		public ResourcePack openWithOverlays(String name, ResourcePackProfile.Metadata metadata) {
			return open(name);
		}
	}
}
