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

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;

public interface VertexEditor extends Vertex {
    /**
     * Sets the geometric vertex position for the given vertex, 
     * relative to block origin. (0,0,0).  Minecraft rendering is designed
     * for models that fit within a single block space and is recommended 
     * that coordinates remain in the 0-1 range, with multi-block meshes
     * split into multiple per-block models.<p>
     * 
     * Standard shader binding: gl_Vertex
     */
    VertexEditor pos(float x, float y, float z);
    
    /**
     * Same as {@link #pos(float, float, float)} but accepts vector type.
     */
    default VertexEditor pos(Vector3f vec) {
        return pos(vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Adds a vertex normal. Models that have per-vertex
     * normals should include them to get correct lighting when it matters.
     * Computed face normal is used when no vertex normal is provided.<p>
     * 
     * {@link Renderer} implementations should honor vertex normals for
     * diffuse lighting - modifying vertex color(s) or packing normals in the vertex 
     * buffer as appropriate for the rendering method/vertex format in effect.<p>
     * 
     * The "extra" parameter is for shader authors to make use of space normally wasted.
     * In most implementations it will be packed as a signed, normalized float 
     * with low effective precision: 1/127.<p>
     * 
     * Note for renderer authors: normals should always be buffered for shader
     * materials, even if the renderer's lighting model does not require normals
     * in the GPU. Populate with face normal when vertex normals aren't present.<p>
     * 
     * Standard shader binding: vec4 in_normal
     */
    VertexEditor normal(float x, float y, float z, float extra);
    
    /**
     * Same as {@link #normal(float, float, float, extra)} but accepts vector type.
     */
    default VertexEditor normal(Vector3f vec) {
        return normal(vec.x(), vec.y(), vec.z(), 0);
    }
    
    /**
     * Same as {@link #normal(float, float, float, extra)} but accepts vector type.
     */
    default VertexEditor normal(Vector4f vec) {
        return normal(vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Minimum block brightness. Has no effect unless emissive lighting is
     * enabled in at least one layer of the material for this quad.
     * Standard shader binding: vec4 in_lightmap<p>
     * 
     * While this has a standard binding, it should not be used by
     * shaders if lighting is enabled in the material. Renderers may alter
     * the format of lightmaps to support the renderer's lighting model.
     */
    VertexEditor lightmap(int lightmap);
    
    /**
     * Indexed color setter.
     */
    default VertexEditor color(int layerIndex, int color) {
        switch(layerIndex) {
        case 0:
            return color1(color);
        case 1:
            return color2(color);
        case 2:
            return color3(color);
        default:
            throw new IndexOutOfBoundsException();
        }
    }
    
    /**
     * Color for first texture layer.
     * Standard shader binding: vec4 in_color
     */
    VertexEditor color1(int color);
    
    /**
     * Indexed texture coordinate setter.
     */
    default VertexEditor uv(int layerIndex, float u, float v) {
        switch(layerIndex) {
        case 0:
            return uv1(u, v);
        case 1:
            return uv2(u, v);
        case 2:
            return uv3(u, v);
        default:
            throw new IndexOutOfBoundsException();
        }
    }
    
    /**
     * Interpolated ("baked") texture coordinates for first texture layer.
     * Standard shader binding: vec2 in_uv
     */
    VertexEditor uv1(float u, float v);
    
    /**
     * Color for second texture layer.
     * Throws IndexOutOfBoundsException if material texture depth < 2.
     * Standard shader binding: vec4 in_color2
     */
    VertexEditor color2(int color);
    
    /**
     * Interpolated ("baked") texture coordinates for second texture layer.
     * Throws IndexOutOfBoundsException if material texture depth < 2.
     * Standard shader binding: vec2 in_uv2
     */
    VertexEditor uv2(float u, float v);
    
    /**
     * Color for third texture layer.
     * Throws IndexOutOfBoundsException if material texture depth < 3.
     * Standard shader binding: vec4 in_color3
     */
    VertexEditor color3(int color);
    
    /**
     * Interpolated ("baked") texture coordinates for third texture layer.
     * Throws IndexOutOfBoundsException if material texture depth < 3.
     * Standard shader binding: vec2 in_uv3
     */
    VertexEditor uv3(float u, float v);
}
