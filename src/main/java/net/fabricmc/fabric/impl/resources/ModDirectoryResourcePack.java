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

import net.fabricmc.fabric.resources.ModResourcePack;
import net.fabricmc.loader.ModInfo;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourceNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ModDirectoryResourcePack extends DirectoryResourcePack implements ModResourcePack {
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
				throw new ResourceNotFoundException(this.base, filename);
			}
			return stream;
		}
	}

	@Override
	protected boolean containsFilename(String filename) {
		return super.containsFilename(filename) || ModResourcePackUtil.containsDefault(info, filename);
	}

	@Override
	public ModInfo getFabricModInfo() {
		return info;
	}
}
