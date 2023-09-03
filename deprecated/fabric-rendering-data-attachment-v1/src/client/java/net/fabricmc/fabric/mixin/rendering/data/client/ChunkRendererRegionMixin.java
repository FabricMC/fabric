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

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.world.WorldView;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;

/**
 * Since {@link RenderAttachedBlockView} is only automatically implemented on {@link WorldView} instances and
 * {@link ChunkRendererRegion} does not implement {@link WorldView}, this mixin manually implements
 * {@link RenderAttachedBlockView} on {@link ChunkRendererRegion}. The BlockView API v2 implementation ensures
 * that all default method implementations of {@link RenderAttachedBlockView} work here automatically.
 */
@Mixin(ChunkRendererRegion.class)
public abstract class ChunkRendererRegionMixin implements RenderAttachedBlockView {
}
