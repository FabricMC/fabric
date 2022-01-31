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

import java.util.WeakHashMap;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackSource;

/**
 * Tracks the sources of resource pack profiles.
 */
public final class ResourcePackSourceTracker {
	private static final WeakHashMap<ResourcePack, ResourcePackSource> SOURCES = new WeakHashMap<>();

	/**
	 * Gets the source of a pack.
	 *
	 * @param pack the resource pack
	 * @return the source, or {@link ResourcePackSource#PACK_SOURCE_NONE} if not tracked
	 */
	public static ResourcePackSource getSource(ResourcePack pack) {
		return SOURCES.getOrDefault(pack, ResourcePackSource.PACK_SOURCE_NONE);
	}

	/**
	 * Sets the source of a pack.
	 *
	 * @param pack the resource pack
	 * @param source the source
	 */
	public static void setSource(ResourcePack pack, ResourcePackSource source) {
		SOURCES.put(pack, source);
	}
}
