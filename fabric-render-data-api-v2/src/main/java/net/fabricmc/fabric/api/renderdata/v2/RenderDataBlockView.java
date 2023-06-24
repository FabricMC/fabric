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

package net.fabricmc.fabric.api.renderdata.v2;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Extensions that allow {@link BlockRenderView} subclasses to provide access to block entity render
 * data in a thread-safe and consistent way.
 *
 * <p>Thread-safety and consistency are especially important in the main use case of this module,
 * which is chunk building. In this context, {@link BlockRenderView#getBlockEntity(BlockPos)} should
 * still work as expected, but this method should not be used because accessing a {@link BlockEntity}
 * directly to get data may not be thread-safe or consistent. It may not be thread-safe since it may
 * lead to non-atomic modification of the internal state of the {@link BlockEntity} instance. It may
 * not be consistent since the internal state of the {@link BlockEntity} instance is not guaranteed
 * to remain the same during chunk building, unlike other data of the {@link BlockRenderView}, such
 * as block states. However, accessing and using render data through
 * {@link #getBlockEntityRenderData(BlockPos)} is guaranteed to be thread-safe and consistent.
 *
 * <p>Note: This interface is automatically implemented on all {@link BlockRenderView} instances via Mixin and interface injection.
 *
 * @see RenderDataBlockEntity
 */
public interface RenderDataBlockView {
	/**
	 * Retrieves block entity render data for a given block position. Always use this method
	 * instead of {@link BlockRenderView#getBlockEntity(BlockPos)} or
	 * {@link RenderDataBlockEntity#getRenderData()} to ensure thread safety and data
	 * consistency.
	 *
	 * <p>Users of this method are required to always check the returned object before using
	 * it. Users must check if it is null and if it is of the correct type to avoid null pointer
	 * and class cast exceptions, as the returned data is not guaranteed to be what the user expects.
	 * A simple way to implement these checks is to use {@code instanceof}, since it always returns
	 * false if the object is null. If the {@code instanceof} returns false, a fallback path should
	 * be used.
	 *
	 * @param pos position of the block entity
	 * @return the render data provided by the block entity, or null if there is no block entity at this position
	 */
	@Nullable
	default Object getBlockEntityRenderData(BlockPos pos) {
		BlockEntity blockEntity = ((BlockRenderView) this).getBlockEntity(pos);
		return blockEntity == null ? null : blockEntity.getRenderData();
	}
}
