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

public interface Vertex {
    Vector3f copyPos(Vector3f target);
    float x();
    float y();
    float z();
   
    boolean hasNormal();
    Vector3f copyNormal(Vector3f target);
    Vector4f copyNormal(Vector4f target);
    float normX();
    float normY();
    float normZ();
    float normExtra();
    
    int lightmap();
    
    int color();
    
    int color2();
    
    int color3();
    
    float u();
    float v();
    
    float u2();
    float v2();
    
    float u3();
    float v3();
}
