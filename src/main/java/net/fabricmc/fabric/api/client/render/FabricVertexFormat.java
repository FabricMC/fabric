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

package net.fabricmc.fabric.api.client.render;

import net.fabricmc.fabric.impl.client.model.FabricVertexFormatImpl;
import net.minecraft.util.Identifier;

public interface FabricVertexFormat {
    /**
     * For Minecraft-compatible item models quads and vertex buffers.
     */
    FabricVertexFormat STANDARD_ITEM = FabricVertexFormatImpl.STANDARD_ITEM;
    
    /**
     * For Minecraft-compatible block models quads and vertex buffers.
     */
    FabricVertexFormat STANDARD_BLOCK = FabricVertexFormatImpl.STANDARD_BLOCK;
    
    /**
     * This format is uniquely significant: it is used for Minecraft baked quads
     * that have been cast to FabricBakedQuads for consumption by the rendering
     * plug-in.  It signals the plug in to assume the vertex format is whichever
     * format is appropriate for the current render context. (Item or Block.)<p>
     * 
     * Bespoke quads and model implementations should avoid using this format, 
     * and instead declare a specific vertex format.
     */
    FabricVertexFormat STANDARD_UNSPECIFIED = FabricVertexFormatImpl.STANDARD_UNSPECIFIED;
    
    Identifier id();
    int integerQuadStride();
    boolean isItemModelCompatible();
    boolean isBlockModelCompatible();
    int textureDepth();
}
