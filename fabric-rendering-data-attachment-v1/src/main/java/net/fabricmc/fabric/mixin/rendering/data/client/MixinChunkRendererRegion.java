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

package net.fabricmc.fabric.mixin.rendering.data.client;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ChunkRendererRegion.class)
public abstract class MixinChunkRendererRegion implements RenderAttachedBlockView {
    private HashMap<BlockPos, Object> fabric_renderDataObjects;

    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(World world, int cxOff, int czOff, WorldChunk[][] chunks, BlockPos posFrom, BlockPos posTo, CallbackInfo info) {
        HashMap<BlockPos, Object> map = new HashMap<>();

        for (WorldChunk[] chunkA : chunks) {
            for (WorldChunk chunkB : chunkA) {
                for (Map.Entry<BlockPos, BlockEntity> entry: chunkB.getBlockEntities().entrySet()) {
                    BlockPos entPos = entry.getKey();
                    if (entPos.getX() >= posFrom.getX() && entPos.getX() <= posTo.getX()
                            && entPos.getY() >= posFrom.getY() && entPos.getY() <= posTo.getY()
                            && entPos.getZ() >= posFrom.getZ() && entPos.getZ() <= posTo.getZ()) {

                        Object o = ((RenderAttachmentBlockEntity) entry.getValue()).getRenderAttachmentData();
                        if (o != null) {
                            map.put(entPos, o);
                        }
                    }
                }
            }
        }

        this.fabric_renderDataObjects = map;
    }

    @Override
    public Object getBlockEntityRenderAttachment(BlockPos pos) {
        return fabric_renderDataObjects.get(pos);
    }
}
