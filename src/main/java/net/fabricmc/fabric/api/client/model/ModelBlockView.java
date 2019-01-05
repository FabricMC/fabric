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
 * BlockView-extending interface to be used by {@link FabricBakedModel} for model
 * customization. It ensures thread safety and exploits data cached in render 
 * chunks for performance and data consistency.<p>
 * 
 * There are differences from BlockView consumers must understand:<p>
 * 
 * <li> BlockEntity implementations that provide data for model customization should implement 
 * {@link RenderDataProvidingBlockEntity} which will be queried on the main thread when a render
 * chunk is enqueued for rebuild. The model should retrieve the results via {@link #getCachedRenderData()}.
 * While {@link #getBlockEntity(net.minecraft.util.math.BlockPos)} is not disabled, it
 * is not thread-safe for use on render threads.  Models that violate this guidance are
 * responsible for any necessary synchronization or collision detection.</li><p>
 * 
 * <li> {@link #getBlockState(net.minecraft.util.math.BlockPos)} and {@link #getFluidState(net.minecraft.util.math.BlockPos)}
 * will always reflect the state cached with the render chunk.  Block and fluid states
 * can thus be different from main-thread world state due to lag between block update
 * application from network packets and render chunk rebuilds. Use of {@link #getCachedRenderData()}
 * will ensures consistency of model state with the rest of the chunk being rendered.</li><p>
 *
 * This interface is only guaranteed to be present in the client environment.
 */
public interface ModelBlockView extends BlockView {
    /**
     * For models associated with Block Entities that implement {@link RenderDataProvidingBlockEntity}
     * this will be the most recent value provided by that implementation for the current block position.<p>

     * Null in all other cases, or if the result from the implementation was null.<p>
     *
     * @param <T> The render data type specific to the consuming model.
     * @param pos Position of the block for the block model.
     */
    <T> T getCachedRenderData(BlockPos pos);
}