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

package net.fabricmc.fabric.api.client.model.fabric;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.render.model.BakedModel;

/**
 * This defines the instance made available to models for buffering vertex data at render time.
 */
public interface RenderContext {
    /**
     * Used by models to send vertex data previously baked 
     * via {@link MeshBuilder}. The fastest option and preferred whenever feasible.<p>
     * 
     * Meshes sent to the consumer may be accompanied by a {@link QuadMaker} consumer - an "editor".
     * If non-null, all quads in the mesh will be passed to the editor for modification before
     * offsets, face culling or lighting are applied.  Meant for animation and mesh customization.
     * The editor can also filter out quads by exiting before calling {@link QuadMaker#emit()}.<p>
     * 
     * Meshes are never mutated by the editor - only the buffered quads. This ensures thread-safe
     * use of meshes across multiple chunk builders.
     */
    BiConsumer<Mesh, Consumer<QuadMaker>> meshConsumer();
    
    /**
     * Fabric causes vanilla baked models to send themselves 
     * via this interface. Can also be used by compound models that contain a mix
     * of vanilla baked models, packaged quads and/or dynamic elements.
     */
    Consumer<BakedModel> fallbackConsumer();
    
    /**
     * Returns a {@link QuadMaker} instance that emits directly to the render buffer.
     * It remains necessary to call {@link QuadMaker#emit()} to output the quad.<p>
     * 
     * This method will always be less performant than passing pre-baked meshes
     * via {@link #meshConsumer()}. It should be used sparingly for model components that
     * demand it - text, icons, dynamic indicators, or other elements that vary too 
     * much for static baking to be feasible.<p>
     * 
     * Calling this method invalidates any {@link QuadMaker} returned earlier.  
     * Will be threadlocal/re-used - do not retain references.<p>
     * 
     * Material must be an instance provided by the active {@link Renderer}.
     */
    QuadMaker quad(RenderMaterial material);
}
