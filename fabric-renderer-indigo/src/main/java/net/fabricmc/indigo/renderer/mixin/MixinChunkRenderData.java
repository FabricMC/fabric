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

package net.fabricmc.indigo.renderer.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.indigo.renderer.accessor.AccessChunkRendererData;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.chunk.ChunkBatcher.ChunkRenderData;

@Mixin(ChunkRenderData.class)
public class MixinChunkRenderData implements AccessChunkRendererData {
	@Shadow private Set<BlockRenderLayer> initialized;
    @Shadow private Set<BlockRenderLayer> nonEmpty;
    @Shadow private boolean empty;
    
	@Override
	public boolean fabric_markInitialized(BlockRenderLayer renderLayer) {
		return initialized.add(renderLayer);
	}

	@Override
	public void fabric_markPopulated(BlockRenderLayer renderLayer) {
		empty = false;
		nonEmpty.add(renderLayer);
	}
}
