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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.AbstractFileResourcePack;
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
	public InputStream openRoot(String fileName) throws IOException {
		if ("pack.mcmeta".equals(fileName)) {
			String description = "Mod resources.";
			String pack = String.format("{\"pack\":{\"pack_format\":" + ModResourcePackUtil.PACK_FORMAT_VERSION + ",\"description\":\"%s\"}}", description);
			return IOUtils.toInputStream(pack, Charsets.UTF_8);
		} else if ("pack.png".equals(fileName)) {
			InputStream stream = FabricLoader.getInstance().getModContainer("fabric-resource-loader-v0")
					.flatMap(container -> container.getMetadata().getIconPath(512).map(container::getPath))
					.filter(Files::exists)
					.map(iconPath -> {
						try {
							return Files.newInputStream(iconPath);
						} catch (IOException e) {
							return null;
						}
					}).orElse(null);

			if (stream != null) {
				return stream;
			}
		}

		// ReloadableResourceManagerImpl gets away with FileNotFoundException.
		throw new FileNotFoundException("\"" + fileName + "\" in Fabric mod resource pack");
	}

	@Override
	public <T> @Nullable T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		try {
			InputStream inputStream = this.openRoot("pack.mcmeta");
			Throwable error = null;
			T metadata;

			try {
				metadata = AbstractFileResourcePack.parseMetadata(metaReader, inputStream);
			} catch (Throwable e) {
				error = e;
				throw e;
			} finally {
				if (inputStream != null) {
					if (error != null) {
						try {
							inputStream.close();
						} catch (Throwable e) {
							error.addSuppressed(e);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return metadata;
		} catch (FileNotFoundException | RuntimeException e) {
			return null;
		}
	}

	@Override
	public String getName() {
		return "Fabric Mods";
	}
}
