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

package net.fabricmc.fabric.mixin.client.render;

import net.fabricmc.fabric.api.client.model.RenderDataProvidingBlockEntity;
import net.fabricmc.fabric.mixin.client.render.MixinBlockModelRenderer.RenderCachePrivateView;
import net.minecraft.block.Block;
import net.minecraft.block.Block.OffsetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.class_853;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.HashMap;
import java.util.Map;

@Mixin(class_853.class)
public abstract class MixinClass853 implements RenderCachePrivateView {
    private HashMap<BlockPos, Object> fabric_renderDataObjects;
    
    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(World world, int cxOff, int czOff, WorldChunk[][] chunks, BlockPos posFrom, BlockPos posTo, CallbackInfo info) {
        this.fabric_renderDataObjects = new HashMap<>();

        for (WorldChunk[] chunkA : chunks) {
            for (WorldChunk chunkB : chunkA) {
                for (Map.Entry<BlockPos, BlockEntity> entry: chunkB.getBlockEntityMap().entrySet()) {
                    BlockPos entPos = entry.getKey();
                    if (entPos.getX() >= posFrom.getX() && entPos.getX() <= posTo.getX()
                            && entPos.getY() >= posFrom.getY() && entPos.getY() <= posTo.getY()
                            && entPos.getZ() >= posFrom.getZ() && entPos.getY() <= posTo.getZ()) {
                        
                        Object o = ((RenderDataProvidingBlockEntity) entry.getValue()).getRenderData();
                        if (o != null) {
                            fabric_renderDataObjects.put(entPos, o);
                        }
                    }
                }
            }
        }
    }
    
    protected int fabric_sideLookupCompletionFlags = 0;
    protected int fabric_sideLookupResultFlags = 0;
    
    // cache model offsets for plants, etc.
    float fabric_offsetX = 0;
    float fabric_offsetY = 0;
    float fabric_offsetZ = 0;
    
    BlockPos fabric_currentBlockPos = null;
    BlockState fabric_currentBlockState = null;
    Object fabric_currentRenderData = null;
    
    @Override
    public final void prepare(BlockPos pos, BlockState blockState) {
        
        fabric_currentBlockPos = pos;
        fabric_currentBlockState = blockState;
        fabric_currentRenderData = fabric_renderDataObjects.get(pos);
        
        if(blockState.getBlock().getOffsetType() == OffsetType.NONE) {
            fabric_offsetX = 0;
            fabric_offsetY = 0;
            fabric_offsetZ = 0;
        }
        else {
            Vec3d offset = blockState.getOffsetPos(this, pos);
            fabric_offsetX = (float) offset.x;
            fabric_offsetY = (float) offset.y;
            fabric_offsetZ = (float) offset.z;
        }
    }

    @Override
    public final boolean shouldOutputSide(Direction side) {
        final int mask = 1 << (side.ordinal());
        if((fabric_sideLookupCompletionFlags & mask) == 0) {
            fabric_sideLookupCompletionFlags |= mask;
            boolean result = Block.shouldDrawSide(fabric_currentBlockState, this, fabric_currentBlockPos, side);
            if(result)
                fabric_sideLookupResultFlags |= mask;
            return result;
        }
        else
            return (fabric_sideLookupResultFlags & mask) != 0;
    }
    
    @Shadow
    protected abstract int method_3691(BlockPos blockPos_1);

    @Override
    public <T> T getCachedRenderData() {
        //noinspection unchecked
        return (T) fabric_currentRenderData;
    }
}