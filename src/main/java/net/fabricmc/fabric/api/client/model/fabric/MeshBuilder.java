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

import net.minecraft.client.render.BufferBuilder;

/**
 * Similar in concept to {@link BufferBuilder} but simpler
 * and not tied to NIO or any other specific implementation.
 * Decouples models from the vertex format(s) used by
 * ModelRenderer to allow compatibility across diverse implementations.<p>
 */
public interface MeshBuilder {
    /**
     * Starts a new quad. Invalidates any {@link QuadMaker} returned 
     * earlier.  Do not retain references.  Must call {@link QuadMaker#emit()}
     * to add the quad to the mesh.<p>
     * 
     * Material must be an instance provided by the active {@link ModelRenderer}.
     */
    QuadMaker quad(ModelMaterial material);
    
    /**
     * Returns a new {@link Mesh} instance containing all
     * quads added to this builder and resets the builder to an empty state<p>
     */
    Mesh build();
}
