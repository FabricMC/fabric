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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Charsets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.SharedConstants;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.BlockEntry;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceFilter;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.mixin.resource.loader.ResourceFilterAccessor;
import net.fabricmc.loader.api.FabricLoader;

/**
 * The Fabric mods resource pack, holds all the mod resource packs as one pack.
 */
public class FabricModResourcePack extends GroupResourcePack {
	private static final Logger LOGGER = LoggerFactory.getLogger(FabricModResourcePack.class);

	public FabricModResourcePack(ResourceType type, List<ModResourcePack> packs) {
		super(type, packs);
	}

	@Override
	public InputSupplier<InputStream> openRoot(String... pathSegments) {
		String fileName = String.join("/", pathSegments);

		if ("pack.mcmeta".equals(fileName)) {
			return () -> IOUtils.toInputStream(generateMetadataJson(), Charsets.UTF_8);
		} else if ("pack.png".equals(fileName)) {
			return FabricLoader.getInstance().getModContainer("fabric-resource-loader-v0")
					.flatMap(container -> container.getMetadata().getIconPath(512).flatMap(container::findPath))
					.map(path -> (InputSupplier<InputStream>) (() -> Files.newInputStream(path)))
					.orElse(null);
		}

		return null;
	}

	private String generateMetadataJson() {
		record PackMetadata(PackResourceMetadata pack, Optional<ResourceFilter> filter) {
			static final Codec<PackMetadata> CODEC = RecordCodecBuilder.create(instance ->
					instance.group(
							PackResourceMetadata.CODEC.fieldOf("pack").forGetter(PackMetadata::pack),
							ResourceFilterAccessor.getCodec().optionalFieldOf("filter").forGetter(PackMetadata::filter)
					).apply(instance, PackMetadata::new));
		}

		final var resourceMetadata = new PackResourceMetadata(
						Text.translatableWithFallback("pack.description.modResources", "Mod resources."),
						SharedConstants.getGameVersion().getResourceVersion(type),
				Optional.empty()
		);

		final List<BlockEntry> blockEntries = collectBlockEntries();
		final Optional<ResourceFilter> filter = blockEntries.isEmpty() ? Optional.empty() : Optional.of(new ResourceFilter(blockEntries));
		final var metadata = new PackMetadata(resourceMetadata, filter);
		return Util.getResult(PackMetadata.CODEC.encodeStart(JsonOps.INSTANCE, metadata), IllegalArgumentException::new).toString();
	}

	// Reads all the resource filters from the sub packs
	private List<BlockEntry> collectBlockEntries() {
		var filterBlocks = new ArrayList<BlockEntry>();

		for (ResourcePack pack : packs) {
			try {
				ResourceFilter resourceFilter = pack.parseMetadata(ResourceFilter.SERIALIZER);

				if (resourceFilter == null) {
					continue;
				}

				filterBlocks.addAll(((ResourceFilterAccessor) resourceFilter).getBlocks());
			} catch (IOException e) {
				LOGGER.error("Failed to get filter section from pack {}", pack.getName());
			}
		}

		return filterBlocks;
	}

	@Override
	public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		InputSupplier<InputStream> inputSupplier = this.openRoot("pack.mcmeta");

		if (inputSupplier != null) {
			try (InputStream input = inputSupplier.get()) {
				return AbstractFileResourcePack.parseMetadata(metaReader, input);
			}
		} else {
			return null;
		}
	}

	@Override
	public String getName() {
		return "fabric";
	}

	@Override
	public boolean isAlwaysStable() {
		return true;
	}
}
