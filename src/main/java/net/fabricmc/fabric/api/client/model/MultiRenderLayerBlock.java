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

import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderLayer;

/**
 * Interface implemented by {@link Block}s which wish to render on multiple chunk layers.
 */
public interface MultiRenderLayerBlock {
	/**
	 * Get a mask of additional render layers to use, supplementing the one provided
	 * in {@link Block#getRenderLayer()}, in the form of "(1 << layer1.ordinal()) | (1 << layer2.ordinal()) | ...".
	 *
	 * Please note that getRenderLayer() should ideally be either:
	 * (a) your primary rendering layer, with the additional layers used for "niceties",
	 * (b) your "lower" (earlier in {@link BlockRenderLayer} order) rendering layers, as the one in said method
	 * (the "primary" layer) will be used for rendering items.
	 *
	 * This is because:
	 * (a) the hook for additional render layers is somewhat fragile and may not be immediately available
	 * with a version update,
	 * (b) the hook is only present for chunk rendering and is not, at present, supported for item rendering.
	 *
	 * @return The additional render layer mask.
	 */
	int getExtraRenderLayerMask();
}
