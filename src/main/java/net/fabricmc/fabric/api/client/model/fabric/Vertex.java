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
     */
    Vector3f copyPos(Vector3f target);
    float x();
    float y();
    float z();
   
    /**
     * If false, no vertex normal was provided.
     * Lighting should use face normal in that case.
     */
    boolean hasNormal();
    
    /**
     * Pass a non-null target to avoid allocation - will be returned with values.
     * Otherwise returns a new instance. Returns null if normal not present.
     */
    Vector3f copyNormal(Vector3f target);
    
    /**
     * Pass a non-null target to avoid allocation - will be returned with values.
     * Otherwise returns a new instance. Returns null if normal not present.
     */
    Vector4f copyNormal(Vector4f target);
    
    /**
     * Will return {@link Float#NaN} if normal not present.
     */
    float normX();
    
    /**
     * Will return {@link Float#NaN} if normal not present.
     */
    float normY();
    
    /**
     * Will return {@link Float#NaN} if normal not present.
     */
    float normZ();
    
    /**
     * Will return {@link Float#NaN} if normal not present.
     */
    float normExtra();
    
    int lightmap();

    int color();
    float u();
    float v();
    
    int color2();
    float u2();
    float v2();
    
    int color3();
    float u3();
    float v3();
}
