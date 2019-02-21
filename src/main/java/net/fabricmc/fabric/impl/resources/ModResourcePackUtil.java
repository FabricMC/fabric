/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

import com.google.common.base.Charsets;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Internal utilities for managing resource packs.
 */
public final class ModResourcePackUtil {
    public static final int PACK_FORMAT_VERSION = 4;

    private ModResourcePackUtil() {

    }

    public static void appendModResourcePacks(List<ResourcePack> packList, ResourceType type) {
        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            Path path = container.getRoot();
            ResourcePack pack = new ModNioResourcePack(container.getMetadata(), path, null);
	        if (!pack.getNamespaces(type).isEmpty()) {
		        packList.add(pack);
	        }
        }
    }

    public static boolean containsDefault(ModMetadata info, String filename) {
        return "pack.mcmeta".equals(filename) || "pack.png".equals(filename);
    }

    public static InputStream openDefault(ModMetadata info, String filename) {
        switch (filename) {
            case "pack.png":
                return ModResourcePackUtil.class.getClassLoader().getResourceAsStream("assets/fabric/textures/misc/default_icon.png");
            case "pack.mcmeta":
                String description = info.getId(); // TODO getName
                if (description == null) {
                    description = "";
                } else {
                    description = description.replaceAll("\"", "\\\"");
                }
                String pack = String.format("{\"pack\":{\"pack_format\":" + PACK_FORMAT_VERSION + ",\"description\":\"%s\"}}", description);
                return IOUtils.toInputStream(pack, Charsets.UTF_8);
            default:
                throw new RuntimeException("Mismatch with .containsDefault(...)!");
        }
    }

    public static String getName(ModMetadata info) {
/*        if (info.getName() != null) {
            return info.getName();
        } else */ { // TODO getName
            return "Fabric Mod \"" + info.getId() + "\"";
        }
    }
}
