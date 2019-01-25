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

/**
 * {@link ModelRenderer#getVertexBuilder()} will return an instance of this interface.
 * Instances are meant to be re-used once obtained. They are not thread-safe and vertex
 * building is not atomic.  Instances should not be shared without synchronization.<p>
 * 
 * The builder is used to pre-bake vertex data to optimize the run-time performance of 
 * the active renderer.  The renderer controls (and hides) the format of the baked data, 
 * and the behavior of the quad is defined via {@link ModelMaterial}.<p>
 */
public interface FastVertexBuilder extends QuadBuilder {
    /**
     * Must be called before any vertex data is input.  Identifies
     * output location for baked vertex data.<p>
     * 
     * @param target    Array where vertex data will be output. Array
     * must have sufficient space for all quads to be written. Space per
     * quad can be determined via {@link ModelMaterial#quadStride()}.<p>
     * 
     * @param targetIndex  Array index where vertex output will start.
     * Builder will advance the index internally as quads are output.<p>
     * 
     * @param isItem Signals rendering intent for renderer to enable
     * format optimizations. If true, packed vertex data is expected to be
     * used for item rendering. Use when building item models. When false, 
     * builder will assume the intent is block rendering. Not all renderers
     * make use of or benefit from this hint.<p>
     */
    void prepare(int[] target, int targetIndex, boolean isItem);
    
    /**
     * Releases reference to target output array.  Not strictly necessary but good practice.
     * Builder can still be reused by setting a new target array.
     */
    void release();
}
