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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.BlockEntry;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackOverlaysMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceFilter;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.mixin.resource.loader.PackFeatureSetMetadataAccessor;
import net.fabricmc.fabric.mixin.resource.loader.PackOverlaysMetadataAccessor;
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
		record PackMetadata(
				PackResourceMetadata pack,
				Optional<ResourceFilter> filter,
				Optional<PackOverlaysMetadata> overlay,
				Optional<PackFeatureSetMetadata> features
		) {
			static final Codec<PackMetadata> CODEC = RecordCodecBuilder.create(instance ->
					instance.group(
							PackResourceMetadata.CODEC.fieldOf(PackResourceMetadata.SERIALIZER.getKey()).forGetter(PackMetadata::pack),
							ResourceFilterAccessor.getCodec().optionalFieldOf(ResourceFilter.SERIALIZER.getKey()).forGetter(PackMetadata::filter),
							PackOverlaysMetadataAccessor.getCodec().optionalFieldOf(PackOverlaysMetadata.SERIALIZER.getKey()).forGetter(PackMetadata::overlay),
							PackFeatureSetMetadataAccessor.getCodec().optionalFieldOf(PackFeatureSetMetadata.SERIALIZER.getKey()).forGetter(PackMetadata::features)
					).apply(instance, PackMetadata::new));
		}

		final var metadata = new PackMetadata(
				new PackResourceMetadata(
						Text.translatableWithFallback("pack.description.modResources", "Mod resources."),
						SharedConstants.getGameVersion().getResourceVersion(type),
						Optional.empty()
				),
				collectFilterBlockEntries().map(ResourceFilter::new),
				collectOverlays().map(PackOverlaysMetadata::new),
				collectFeatures().map(PackFeatureSetMetadata::new)
		);
		return Util.getResult(PackMetadata.CODEC.encodeStart(JsonOps.INSTANCE, metadata), IllegalArgumentException::new).toString();
	}

	private Optional<List<BlockEntry>> collectFilterBlockEntries() {
		return collectMetadata(ResourceFilter.SERIALIZER)
				.flatMap(filter -> ((ResourceFilterAccessor) filter).getBlocks().stream())
				.collect(optionalList());
	}

	private Optional<List<PackOverlaysMetadata.Entry>> collectOverlays() {
		return collectMetadata(PackOverlaysMetadata.SERIALIZER)
				.flatMap(metadata -> metadata.overlays().stream())
				.collect(optionalList());
	}

	private Optional<FeatureSet> collectFeatures() {
		return collectMetadata(PackFeatureSetMetadata.SERIALIZER)
				.map(PackFeatureSetMetadata::flags)
				.reduce(FeatureSet::combine);
	}

	private <T> Stream<T> collectMetadata(ResourceMetadataSerializer<T> serializer) {
		return packs.stream()
				.sorted(Comparator.comparing(ResourcePack::getName)) // Sort to ensure that the overlays are applied in a deterministic order
				.map(pack -> {
					try {
						return pack.parseMetadata(serializer);
					} catch (IOException e) {
						LOGGER.error("Failed to get {} section from pack {}", serializer.getKey(), pack.getName());
						return null; // Not a fatal error, matches LifecycledResourceManagerImpl.parseResourceFilter
					}
				}).filter(Objects::nonNull);
	}

	// Collects to an optional list. The optional is not empty when the list contains at least 1 element
	private <T> Collector<T, ?, Optional<List<T>>> optionalList() {
		return Collectors.collectingAndThen(Collectors.toList(),
				list -> list.isEmpty() ? Optional.empty() : Optional.of(list)
		);
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
