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

//TODO: add standard attribute binding names for shader materials

public interface VertexEditor extends Vertex {
    /**
     * Sets the geometric vertex position for the given vertex, 
     * relative to block origin. (0,0,0).  Minecraft rendering is designed
     * for models that fit within a single block space and is recommended 
     * that coordinates remain in the 0-1 range, with multi-block meshes
     * split into multiple per-block models.
     */
    VertexEditor pos(float x, float y, float z);
    
    /**
     * Adds a vertex normal. Models that have per-vertex
     * normals should include them to get correct lighting when it matters.
     * Models that have pre-computed face normals <em>can</em> include them
     * in hope of avoiding re-computation in the renderer but this may or may
     * not be useful depending on the implementation and use case<p>
     * 
     * {@link Renderer} implementations should honor vertex normals for
     * diffuse lighting - modifying vertex color(s) or packing normals in the vertex 
     * buffer as appropriate for the rendering method/vertex format in effect.<p>
     * 
     * The "extra" parameter is for shader authors to make use of space normally wasted.
     * In most implementations it will be packed as signed, normalized float 
     * with low effective precision: 1/127.
     */
    VertexEditor normal(float x, float y, float z, float extra);
    
    /**
     * Minimum block brightness. Has no effect unless emissive lighting is
     * enabled in at least one layer of the material for this quad.
     */
    VertexEditor lightmap(int lightmap);
    
    /**
     * Color for first texture layer.
     */
    VertexEditor color(int color);
    
    /**
     * Interpolated ("baked") texture coordinates for first texture layer. 
     */
    VertexEditor uv(float u, float v);
    
    /**
     * Color for second texture layer.
     * Throws IndexOutOfBoundsException if material texture depth < 2.
     */
    VertexEditor color2(int color);
    
    /**
     * Interpolated ("baked") texture coordinates for second texture layer.
     * Throws IndexOutOfBoundsException if material texture depth < 2.
     */
    VertexEditor uv2(float u, float v);
    
    /**
     * Color for third texture layer.
     * Throws IndexOutOfBoundsException if material texture depth < 3.
     */
    VertexEditor color3(int color);
    
    /**
     * Interpolated ("baked") texture coordinates for third texture layer.
     * Throws IndexOutOfBoundsException if material texture depth < 3.
     */
    VertexEditor uv3(float u, float v);
}
