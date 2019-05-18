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

package net.fabricmc.indigo.renderer.render;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ExtendedBlockView;

/**
 * Holds, manages and provides access to the block/world related state
 * needed by fallback and mesh consumers.<p>
 * 
 * Exception: per-block position offsets are tracked in {@link ChunkRenderInfo}
 * so they can be applied together with chunk offsets.
 */
public class BlockRenderInfo {
    private final BlockColors blockColorMap = MinecraftClient.getInstance().getBlockColorMap();
    public final Random random = new Random();
    public ExtendedBlockView blockView;
    public BlockPos blockPos;
    public BlockState blockState; 
    public long seed;
    boolean defaultAo;
    int defaultLayerIndex;
    
    public final Supplier<Random> randomSupplier = () -> {
        final Random result = random;
        long seed = this.seed;
        if(seed == -1L) {
            seed = blockState.getRenderingSeed(blockPos);
            this.seed = seed;
        }
        result.setSeed(seed);
        return result;
    };
    
    public void setBlockView(ExtendedBlockView blockView) {
        this.blockView = blockView;
    }
    
    public void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAO) {
        this.blockPos = blockPos;
        this.blockState = blockState;
        // in the unlikely case seed actually matches this, we'll simply retrieve it more than one
        seed = -1L; 
        defaultAo = modelAO && MinecraftClient.isAmbientOcclusionEnabled() && blockState.getLuminance() == 0;
        defaultLayerIndex = blockState.getBlock().getRenderLayer().ordinal();
    }
    
    public void release() {
        blockPos = null;
        blockState = null; 
    }
    
    int blockColor(int colorIndex) {
        return 0xFF000000 | blockColorMap.getColorMultiplier(blockState, blockView, blockPos, colorIndex);
    }
    
    boolean shouldDrawFace(Direction face) {
        return true;
    }
    
    int layerIndexOrDefault(BlockRenderLayer layer) {
        return layer == null ? this.defaultLayerIndex : layer.ordinal();
    }
}
