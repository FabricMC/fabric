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

package net.fabricmc.fabric.api.client.model;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * BlockView-extending interface which additionally provides data gathered from
 * {@link RenderDataProvidingBlockEntity}.
 *
 * This interface is only guaranteed to be present in the client environment.
 */
public interface RenderCacheView extends BlockView {
	/**
	 * Get cached rendering information from a block entity at a given position.
	 *
	 * @param pos The block position.
	 * @param <T> The type of the data gathered from a given block position.
	 * @return The data gathered from {@link RenderDataProvidingBlockEntity} at that position, or null.
	 */
	default <T> T getCachedRenderData(BlockPos pos) {
		//noinspection unchecked
		return (T) ((RenderDataProvidingBlockEntity) getBlockEntity(pos)).getRenderData();
	}
}
