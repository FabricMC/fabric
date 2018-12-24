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

package net.fabricmc.fabric.mixin.client.rendercache;

import net.fabricmc.fabric.api.client.model.RenderCacheView;
import net.fabricmc.fabric.api.client.model.RenderDataProvidingBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.class_853;
import net.minecraft.util.math.BlockPos;
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
public abstract class MixinClass853 implements RenderCacheView {
	private Map<BlockPos, Object> fabric_renderDataObjects;

	@Shadow
	private int field_4486;
	@Shadow
	private int field_4484;
	@Shadow
	private int field_4482;

	@Inject(at = @At("RETURN"), method = "<init>")
	public void init(World world, int cxOff, int czOff, WorldChunk[][] chunks, BlockPos posFrom, BlockPos posTo, CallbackInfo info) {
		this.fabric_renderDataObjects = new HashMap<>();

		for (WorldChunk[] chunkA : chunks) {
			for (WorldChunk chunkB : chunkA) {
				for (BlockEntity entity : chunkB.getBlockEntityMap().values()) {
					Object o = ((RenderDataProvidingBlockEntity) entity).getRenderData();
					if (o != null) {
						BlockPos entPos = entity.getPos();
						// TODO: If we're using a Map and not an array, should this stay?
						if (entPos.getX() >= posFrom.getX() && entPos.getX() <= posTo.getX()
							&& entPos.getY() >= posFrom.getY() && entPos.getY() <= posTo.getY()
							&& entPos.getZ() >= posFrom.getZ() && entPos.getY() <= posTo.getZ()) {
							fabric_renderDataObjects.put(entity.getPos(), o);
						}
					}
				}
			}
		}
	}

	@Shadow
	protected abstract int method_3691(BlockPos blockPos_1);

	@Override
	public <T> T getCachedBlockEntityRenderData(BlockPos pos) {
		//noinspection unchecked
		return (T) fabric_renderDataObjects.get(pos);
	}
}