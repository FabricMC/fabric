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
import java.util.List;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.SharedConstants;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.loader.api.FabricLoader;

/**
 * The Fabric mods resource pack, holds all the mod resource packs as one pack.
 */
public class FabricModResourcePack extends GroupResourcePack {
	public FabricModResourcePack(ResourceType type, List<ModResourcePack> packs) {
		super(type, packs);
	}

	@Override
	public InputSupplier<InputStream> openRoot(String... pathSegments) {
		String fileName = String.join("/", pathSegments);

		if ("pack.mcmeta".equals(fileName)) {
			String description = "Mod resources.";
			String pack = String.format("{\"pack\":{\"pack_format\":" + type.getPackVersion(SharedConstants.getGameVersion()) + ",\"description\":\"%s\"}}", description);
			return () -> IOUtils.toInputStream(pack, Charsets.UTF_8);
		} else if ("pack.png".equals(fileName)) {
			return FabricLoader.getInstance().getModContainer("fabric-resource-loader-v0")
					.flatMap(container -> container.getMetadata().getIconPath(512).map(container::getPath))
					.filter(Files::exists)
					.map(path -> (InputSupplier<InputStream>) (() -> Files.newInputStream(path)))
					.orElse(null);
		}

		return null;
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
		return "Fabric Mods";
	}
}
