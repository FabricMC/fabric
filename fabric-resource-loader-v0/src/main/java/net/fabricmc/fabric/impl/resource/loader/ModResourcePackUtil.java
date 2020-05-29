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
import java.nio.file.Path;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;

import net.minecraft.SharedConstants;
import net.minecraft.client.resource.Format4ResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.DefaultResourcePack;

import net.fabricmc.fabric.mixin.resource.loader.MixinFormat4ResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

/**
 * Internal utilities for managing resource packs.
 */
public final class ModResourcePackUtil {
	public static final int PACK_FORMAT_VERSION = SharedConstants.getGameVersion().getPackVersion();

	private ModResourcePackUtil() { }

	public static void appendModResourcePacks(List<ResourcePack> packList, ResourceType type) {
		for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
			if (container.getMetadata().getType().equals("builtin")) {
				continue;
			}

			Path path = container.getRootPath();
			ResourcePack pack = new ModNioResourcePack(container.getMetadata(), path, null);

			if (!pack.getNamespaces(type).isEmpty()) {
				packList.add(pack);
			}
		}
	}

	public static boolean containsDefault(ModMetadata info, String filename) {
		return "pack.mcmeta".equals(filename);
	}

	public static InputStream openDefault(ModMetadata info, String filename) {
		switch (filename) {
		case "pack.mcmeta":
			String description = info.getName();

			if (description == null) {
				description = "";
			} else {
				description = description.replaceAll("\"", "\\\"");
			}

			String pack = String.format("{\"pack\":{\"pack_format\":" + PACK_FORMAT_VERSION + ",\"description\":\"%s\"}}", description);
			return IOUtils.toInputStream(pack, Charsets.UTF_8);
		default:
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

	public static void modifyResourcePackList(List<ResourcePack> list) {
		List<ResourcePack> oldList = Lists.newArrayList(list);
		list.clear();

		boolean appended = false;

		for (int i = 0; i < oldList.size(); i++) {
			ResourcePack pack = oldList.get(i);
			list.add(pack);

			boolean isDefaultResources = pack instanceof DefaultResourcePack;

			if (!isDefaultResources && pack instanceof Format4ResourcePack) {
				MixinFormat4ResourcePack fixer = (MixinFormat4ResourcePack) pack;
				isDefaultResources = fixer.getParent() instanceof DefaultResourcePack;
			}

			if (isDefaultResources) {
				ModResourcePackUtil.appendModResourcePacks(list, ResourceType.CLIENT_RESOURCES);
				appended = true;
			}
		}

		if (!appended) {
			StringBuilder builder = new StringBuilder("Fabric could not find resource pack injection location!");

			for (ResourcePack rp : oldList) {
				builder.append("\n - ").append(rp.getName()).append(" (").append(rp.getClass().getName()).append(")");
			}

			throw new RuntimeException(builder.toString());
		}
	}
}
