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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.SharedConstants;
import net.minecraft.class_7701;
import net.minecraft.class_7712;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

/**
 * Internal utilities for managing resource packs.
 */
public final class ModResourcePackUtil {
	private static final Gson GSON = new Gson();

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

			ModResourcePack pack = ModNioResourcePack.create(new Identifier("fabric", container.getMetadata().getId()), getName(container.getMetadata()), container, null, type, ResourcePackActivationType.ALWAYS_ENABLED);

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
			String description = Objects.requireNonNullElse(info.getName(), "");
			String metadata = serializeMetadata(type.getPackVersion(SharedConstants.getGameVersion()), description);
			return IOUtils.toInputStream(metadata, Charsets.UTF_8);
		default:
			return null;
		}
	}

	public static String serializeMetadata(int packVersion, String description) {
		JsonObject pack = new JsonObject();
		pack.addProperty("pack_format", packVersion);
		pack.addProperty("description", description);
		JsonObject metadata = new JsonObject();
		metadata.add("pack", pack);
		return GSON.toJson(metadata);
	}

	public static String getName(ModMetadata info) {
		if (info.getName() != null) {
			return info.getName();
		} else {
			return "Fabric Mod \"" + info.getId() + "\"";
		}
	}

	/**
	 * Creates the default data pack settings that replaces
	 * {@code DataPackSettings.SAFE_MODE} used in vanilla.
	 * @return the default data pack settings
	 */
	public static class_7712 createDefaultDataPackSettings() {
		ModResourcePackCreator modResourcePackCreator = new ModResourcePackCreator(ResourceType.SERVER_DATA);
		List<ResourcePackProfile> moddedResourcePacks = new ArrayList<>();
		modResourcePackCreator.register(moddedResourcePacks::add);

		List<String> enabled = new ArrayList<>(DataPackSettings.SAFE_MODE.getEnabled());
		List<String> disabled = new ArrayList<>(DataPackSettings.SAFE_MODE.getDisabled());

		// This ensures that any built-in registered data packs by mods which needs to be enabled by default are
		// as the data pack screen automatically put any data pack as disabled except the Default data pack.
		for (ResourcePackProfile profile : moddedResourcePacks) {
			try (ResourcePack pack = profile.createResourcePack()) {
				if (pack instanceof FabricModResourcePack || (pack instanceof ModNioResourcePack && ((ModNioResourcePack) pack).getActivationType().isEnabledByDefault())) {
					enabled.add(profile.getName());
				} else {
					disabled.add(profile.getName());
				}
			}
		}

		return new class_7712(
				new DataPackSettings(enabled, disabled),
				class_7701.field_40183
		);
	}
}
