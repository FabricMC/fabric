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

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.resource.ResourceType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

/**
 * Internal utilities for managing resource packs.
 */
public final class ModResourcePackUtil {
	public static final int PACK_FORMAT_VERSION = 4;

	private ModResourcePackUtil() {
	}

	public static void appendModResourcePacks(List<? super ModNioResourcePack> packList, ResourceType type) {
		for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
			if (container.getMetadata().getType().equals("builtin")) {
				continue;
			}
			ModNioResourcePack pack = new ModNioResourcePack(container, null);
			if (!pack.getNamespaces(type).isEmpty()) {
				packList.add(pack);
			}
		}
	}

	public static String getName(ModMetadata info) {
		if (info.getName() != null) {
			return info.getName();
		} else {
			return "Fabric Mod \"" + info.getId() + "\"";
		}
	}
	
	static boolean requestsStandaloneProfile(ModMetadata meta) {
		CustomValue value = meta.getCustomValue("fabric-resource-loader:requestStandaloneProfile");
		return value != null && value.getAsBoolean();
	}

	static boolean canPackBeDisabled(ModMetadata meta) {
		CustomValue value = meta.getCustomValue("fabric-resource-loader:packDisableable");
		return value != null && value.getAsBoolean();
	}

	static void setPackIcon(ModContainer mod, CustomImageResourcePackProfile info) {
		String file = mod.getMetadata().getIconPath(64).orElse(null);
		if (file == null)
			return;
		try (InputStream stream = Files.newInputStream(mod.getPath(file))) {
			info.setImage(stream);
		} catch (IOException ignored) {
		}
	}

	static void setPackIcon(ModNioResourcePack pack, CustomImageResourcePackProfile info) {
		String file = pack.getFabricModMetadata().getIconPath(64).orElse(null);
		if (file == null)
			return;
		try (InputStream stream = pack.openFile(file)) {
			info.setImage(stream);
		} catch (IOException ignored) {
		}
	}
}
