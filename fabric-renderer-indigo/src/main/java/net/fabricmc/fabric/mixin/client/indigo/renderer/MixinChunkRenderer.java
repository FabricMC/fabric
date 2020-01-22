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

package net.fabricmc.fabric.mixin.client.indigo.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.chunk.ChunkBuilder.BuiltChunk;

import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessChunkRenderer;

@Mixin(BuiltChunk.class)
public abstract class MixinChunkRenderer implements AccessChunkRenderer {
	@Shadow
	abstract void beginBufferBuilding(BufferBuilder builder);

	/**
	 * Access method for renderer.
	 */
	@Override
	public void fabric_beginBufferBuilding(BufferBuilder builder) {
		beginBufferBuilding(builder);
	}
}
