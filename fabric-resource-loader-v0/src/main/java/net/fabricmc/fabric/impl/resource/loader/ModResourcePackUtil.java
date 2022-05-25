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
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

/**
 * Internal utilities for managing resource packs.
 */
public final class ModResourcePackUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModResourcePackUtil.class);
	private ModResourcePackUtil() {
	}

	/**
	 * Appends mod resource packs to the given list.
	 *
	 * @param packs   the resource pack list to append
	 * @param type    the type of resource
	 * @param subPath the resource pack sub path directory in mods, may be {@code null}
	 */
	public static void appendModResourcePacks(List<ModResourcePack> packs, ResourceType type, @Nullable String subPath) {
		for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
			if (container.getMetadata().getType().equals("builtin")) {
				continue;
			}

			ModResourcePack pack = ModNioResourcePack.create(getName(container.getMetadata()), container, null, type, ResourcePackActivationType.ALWAYS_ENABLED);

			if (pack != null) {
				packs.add(pack);
			}
		}
	}

	public static boolean containsDefault(ModMetadata info, String filename) {
		return "pack.mcmeta".equals(filename);
	}

	public static InputStream openDefault(ModMetadata info, ResourceType type, String filename) {
		switch (filename) {
		case "pack.mcmeta":
			String description = info.getName();

			if (description == null) {
				description = "";
			} else {
				description = description.replaceAll("\"", "\\\"");
			}

			String pack = String.format("{\"pack\":{\"pack_format\":" + type.getPackVersion(SharedConstants.getGameVersion()) + ",\"description\":\"%s\"}}", description);
			return IOUtils.toInputStream(pack, Charsets.UTF_8);
		default:
			return null;
		}
	}

	// Basically a copy of the vanilla method, with an additional parameter for Text
	@Nullable
	public static ResourcePackProfile of(String name, Text text, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, ResourcePackProfile.Factory profileFactory, ResourcePackProfile.InsertionPosition insertionPosition, ResourcePackSource packSource) {
		try {
			ResourcePack resourcePack = packFactory.get();

			ResourcePackProfile profile;

			parseMetadata: {
				try (resourcePack) {
					PackResourceMetadata packResourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.READER);

					if (packResourceMetadata != null) {
						profile = profileFactory.create(name, text, alwaysEnabled, packFactory, packResourceMetadata, insertionPosition, packSource);
						break parseMetadata;
					}

					LOGGER.warn("Couldn't find pack meta for pack {}", name);
				}

				return null;
			}

			return profile;
		} catch (IOException var11) {
			LOGGER.warn("Couldn't get pack info for: {}", var11.toString());
			return null;
		}
	}

	public static String getName(ModMetadata info) {
		if (info.getName() != null) {
			return info.getName();
		} else {
			return "Fabric Mod \"" + info.getId() + "\"";
		}
	}
}
