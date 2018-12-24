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

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface RenderCacheView extends BlockView {
	default <T> T getRenderDataObject(BlockPos pos) {
		BlockState state = getBlockState(pos);
		Object obj = ((RenderDataProvidingBlock) state.getBlock()).getRenderDataObject(state, this, pos);

		if (obj == null && state.getBlock() instanceof BlockEntityProvider) {
			BlockEntity entity = getBlockEntity(pos);
			if (entity instanceof RenderDataProvidingBlockEntity) {
				obj = ((RenderDataProvidingBlockEntity) entity).getRenderDataObject();
			}
		}

		//noinspection unchecked
		return obj != null ? (T) obj : null;
	}
}
