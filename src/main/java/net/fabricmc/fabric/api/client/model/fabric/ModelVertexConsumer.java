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
import net.minecraft.util.math.Direction;

/**
 * Similar in concept to {@link BufferBuilder} but designed to
 * accept baked texture coordinates and with little transformation
 * and restricted options for mutating vertex data.<p>
 * 
 * Decouples models from the vertex format(s) used by
 * ModelRenderer to allow compatibility across diverse implementations.<p>
 */
public interface ModelVertexConsumer {
    void begin(ModelMaterial material);
    
    void end();
    
    void setTintIndex(int tintIndex);
    
    void setLightingFace(Direction face);
    
    void setDefaultShading(ShadingMode shading);
    
    void reset();
    
    void vertex(float x, float y, float z);
    
    void normal(float x, float y, float z);
    
    void colorTexture(int color, float u, float v);
    
    void brightness(int brightness);
    
    default void colorTextureBrightness(int color, float u, float v, int brightness) {
        colorTexture(color, u, v);
        brightness(brightness);
    }
    
    void putStandardVertexData(int[] vertexData, int startIndex, boolean isItem);
    
    default void putStandardQuadData(int[] quadData, int startIndex, boolean isItem) {
        putStandardVertexData(quadData, startIndex, isItem);
        putStandardVertexData(quadData, startIndex + 7, isItem);
        putStandardVertexData(quadData, startIndex + 14, isItem);
        putStandardVertexData(quadData, startIndex + 21, isItem);
    }
    
    void setTexture(int vertexIndex, int layerIndex, float u, float v);
    
    default void setTexture(int vertexIndex, float u, float v) {
        setTexture(vertexIndex, 0, u, v);
    }
    
    void setColor(int layerIndex, int c0, int c1, int c2, int c3);
    
    default void setColor(int c0, int c1, int c2, int c3) {
        setColor(0, c0, c1, c2, c3);
    }
    
    void setBrightness(int b0, int b1, int b2, int b3);
}
