/*
 * Copyright 2016 FabricMC
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

package net.fabricmc.fabric.resources;

import com.google.common.base.Charsets;
import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.ModContainer;
import net.fabricmc.loader.ModInfo;
import net.minecraft.resource.ResourcePack;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public final class ModResourcePackUtil {
    public static final int PACK_FORMAT_VERSION = 4;

    private ModResourcePackUtil() {

    }

    public static void appendModResourcePacks(List<ResourcePack> packList) {
        for (ModContainer container : FabricLoader.INSTANCE.getMods()) {
            File file = container.getOriginFile();
            if (file.isDirectory()) {
                packList.add(new ModDirectoryResourcePack(container.getInfo(), file));
            } else {
                String name = file.getName().toLowerCase(Locale.ROOT);
                if (name.endsWith(".zip") || name.endsWith(".jar")) {
                    packList.add(new ModZipResourcePack(container.getInfo(), file));
                }
            }
        }
    }

    public static boolean containsDefault(ModInfo info, String filename) {
        return "pack.mcmeta".equals(filename) || "icon.png".equals(filename);
    }

    public static InputStream openDefault(ModInfo info, String filename) {
        switch (filename) {
            case "icon.png":
                return ModResourcePackUtil.class.getClassLoader().getResourceAsStream("assets/fabric/textures/misc/default_icon.png");
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
                throw new RuntimeException("Mismatch with .containsDefault(...)!");
        }
    }

    public static String getName(ModInfo info) {
        if (info.getName() != null) {
            return info.getName();
        } else {
            return "Fabric Mod \"" + info.getId() + "\"";
        }
    }
}
