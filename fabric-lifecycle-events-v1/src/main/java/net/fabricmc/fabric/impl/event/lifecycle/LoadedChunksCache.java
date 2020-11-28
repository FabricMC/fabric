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

package net.fabricmc.fabric.impl.event.lifecycle;

import java.util.Set;

import net.minecraft.util.math.ChunkPos;

/**
 * A simple marker interface which holds references to chunks which block entities may be loaded or unloaded from.
 */
public interface LoadedChunksCache {
	Set<ChunkPos> fabric_getLoadedPositions();

	/**
	 * Marks a chunk as loaded in a world.
	 *
	 * @param pos the chunk pos
	 */
	void fabric_markLoaded(ChunkPos pos);

	/**
	 * Marks a chunk as unloaded in a world.
	 *
	 * @param pos the chunk pos
	 */
	void fabric_markUnloaded(ChunkPos pos);
}
