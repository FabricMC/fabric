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

package net.fabricmc.fabric.api.renderer.v1.material;

import net.minecraft.block.BlockRenderLayer;

public enum BlendMode {
	DEFAULT(null),
	SOLID(BlockRenderLayer.field_9178),
	CUTOUT_MIPPED(BlockRenderLayer.CUTOUT_MIPPED),
	CUTOUT(BlockRenderLayer.field_9174),
	TRANSLUCENT(BlockRenderLayer.field_9179);
	
	public final BlockRenderLayer blockRenderLayer;
	
	private BlendMode(BlockRenderLayer blockRenderLayer) {
		this.blockRenderLayer = blockRenderLayer;
	}

	public static BlendMode fromRenderLayer(BlockRenderLayer renderLayer) {
		if (renderLayer == BlockRenderLayer.field_9178) {
			return SOLID;
		} else if (renderLayer == BlockRenderLayer.CUTOUT_MIPPED) {
			return CUTOUT_MIPPED;
		} else if (renderLayer == BlockRenderLayer.field_9174) {
			return CUTOUT;
		} else if (renderLayer == BlockRenderLayer.field_9179) {
			return TRANSLUCENT;
		} else {
			return DEFAULT;
		}
	}
}
