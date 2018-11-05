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

import net.fabricmc.loader.ModInfo;
import net.minecraft.class_3266;
import net.minecraft.resource.DirectoryResourcePack;

import java.io.*;

public class ModDirectoryResourcePack extends DirectoryResourcePack {
    private final ModInfo info;

    public ModDirectoryResourcePack(ModInfo info, File file) {
        super(file);
        this.info = info;
    }

    @Override
    public String getName() {
        return ModResourcePackUtil.getName(info);
    }

    @Override
    protected InputStream openFilename(String filename) throws IOException {
        try {
            return super.openFilename(filename);
        } catch (FileNotFoundException e) {
            InputStream stream = ModResourcePackUtil.openDefault(info, filename);
            if (stream == null) {
                throw new class_3266(this.base, filename);
            }
            return stream;
        }
    }

    @Override
    protected boolean containsFilename(String filename) {
        return super.containsFilename(filename) || ModResourcePackUtil.containsDefault(info, filename);
    }
}
