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

import net.minecraft.block.BlockRenderLayer;

public interface ModelMaterialBuilder {
    ModelMaterial build();
    
    void setBlendMode(int textureLayer, BlockRenderLayer blendMode);
    
    default void setBlendMode(BlockRenderLayer blendMode) {
        setBlendMode(0, blendMode);
    }
    
    void setTextureDepth(int depth);
    
    void setShading(int textureLayer, ShadingMode shading);
    
    default void setShading(ShadingMode shading) {
        setShading(0, shading);
    }
    
    void setEmissive(int textureLayer, boolean isEmissive);
    
    default void setEmissive(boolean isEmissive) {
        setEmissive(0, isEmissive);
    }
}
