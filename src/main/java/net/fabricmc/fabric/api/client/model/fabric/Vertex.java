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

/**
 * Read-only view of vertex data created via {@link VertexEditor}.
 * See that class for additional detail.
 */
public interface Vertex {
    /**
     * Pass a non-null target to avoid allocation - will be returned with values.
     * Otherwise returns a new instance.
     * See {@link VertexEditor#pos(float, float, float)}
     */
    Vector3f copyPos(Vector3f target);
    
    /**
     * Geometric position, x coordinate. See {@link VertexEditor#pos(float, float, float)}
     */
    float x();
    
    /**
     * Geometric position, y coordinate. See {@link VertexEditor#pos(float, float, float)}
     */
    float y();
    
    /**
     * Geometric position, z coordinate. See {@link VertexEditor#pos(float, float, float)}
     */
    float z();
   
    /**
     * If false, no vertex normal was provided.
     * Lighting should use face normal in that case.
     * See {@link VertexEditor#normal(float, float, float, float)}
     */
    boolean hasNormal();
    
    /**
     * Pass a non-null target to avoid allocation - will be returned with values.
     * Otherwise returns a new instance. Returns null if normal not present.
     * See {@link VertexEditor#normal(float, float, float, float)}
     */
    Vector3f copyNormal(Vector3f target);
    
    /**
     * Pass a non-null target to avoid allocation - will be returned with values.
     * Otherwise returns a new instance. Returns null if normal not present.
     * See {@link VertexEditor#normal(float, float, float, float)}
     */
    Vector4f copyNormal(Vector4f target);
    
    /**
     * Will return {@link Float#NaN} if normal not present.
     * See {@link VertexEditor#normal(float, float, float, float)}
     */
    float normX();
    
    /**
     * Will return {@link Float#NaN} if normal not present.
     * See {@link VertexEditor#normal(float, float, float, float)}
     */
    float normY();
    
    /**
     * Will return {@link Float#NaN} if normal not present.
     * See {@link VertexEditor#normal(float, float, float, float)}
     */
    float normZ();
    
    /**
     * Will return {@link Float#NaN} if normal not present.
     * See {@link VertexEditor#normal(float, float, float, float)}
     */
    float normExtra();
    
    /**
     * Minimum block brightness. Zero if not set.
     */
    int lightmap();

    /**
     * Indexed color retrieval.
     */
    default int color(int textureIndex) {
        switch(textureIndex) {
        case 0:
            return color1();
        case 1:
            return color2();
        case 2:
            return color3();
        default:
            throw new IndexOutOfBoundsException();
        }
    }
    
    /**
     * Color for first texture. Black (0) if not set.
     */
    int color1();
    
    /**
     * Indexed texture coordinate retrieval.
     */
    default float u(int textureIndex) {
        switch(textureIndex) {
        case 0:
            return u1();
        case 1:
            return u2();
        case 2:
            return u3();
        default:
            throw new IndexOutOfBoundsException();
        }
    }
    
    /**
     * Interpolated ("baked") horizontal texture coordinate for first texture. 
     */
    float u1();
    
    /**
     * Indexed texture coordinate retrieval.
     */
    default float v(int textureIndex) {
        switch(textureIndex) {
        case 0:
            return v1();
        case 1:
            return v2();
        case 2:
            return v3();
        default:
            throw new IndexOutOfBoundsException();
        }
    }
    
    /**
     * Interpolated ("baked") vertical texture coordinate for first texture. 
     */
    float v1();
    
    /**
     * Color for second texture. Black (0) if not set.
     * Throws IndexOutOfBoundsException if material texture depth < 2.
     */
    int color2();
    
    /**
     * Interpolated ("baked") horizontal texture coordinate for second texture. 
     * Throws IndexOutOfBoundsException if material texture depth < 2.
     */
    float u2();
    
    /**
     * Interpolated ("baked") vertical texture coordinate for second texture. 
     * Throws IndexOutOfBoundsException if material texture depth < 2.
     */
    float v2();
    
    /**
     * Color for third texture. Black (0) if not set.
     * Throws IndexOutOfBoundsException if material texture depth < 3.
     */
    int color3();
    
    /**
     * Interpolated ("baked") horizontal texture coordinate for third texture. 
     * Throws IndexOutOfBoundsException if material texture depth < 3.
     */
    float u3();
    
    /**
     * Interpolated ("baked") vertical texture coordinate for third texture. 
     * Throws IndexOutOfBoundsException if material texture depth < 3.
     */
    float v3();
}
