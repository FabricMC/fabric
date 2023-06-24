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

package net.fabricmc.fabric.api.rendering.data.v1;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.WorldView;

import net.fabricmc.fabric.api.renderdata.v2.RenderDataBlockView;

/**
 * This interface is guaranteed to be implemented on all {@link WorldView} instances.
 * It is likely to be implemented on a {@link BlockRenderView} instance, but this is
 * not guaranteed.
 *
 * @deprecated Use {@link RenderDataBlockView} instead.
 */
@Deprecated
public interface RenderAttachedBlockView extends BlockRenderView {
	/**
	 * This method will call {@link RenderDataBlockView#getBlockEntityRenderData(BlockPos)} by default.
	 *
	 * @deprecated Use {@link RenderDataBlockView#getBlockEntityRenderData(BlockPos)} instead.
	 */
	@Deprecated
	@Nullable
	default Object getBlockEntityRenderAttachment(BlockPos pos) {
		return getBlockEntityRenderData(pos);
	}
}
