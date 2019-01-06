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
     * For Minecraft-compatible item models quads and vertex buffers.<p>
     * 
     * This format includes vertex normals but no lightmap. Models are
     * expected to provide vertex normals so that plug-ins do not
     * need to compute them. That said, some plug-ins may elect to 
     * validate item normals and populate from a computed
     * face normal when malformed normals are detected.<p>
     */
    FabricVertexFormat STANDARD_ITEM = FabricVertexFormatImpl.STANDARD_ITEM;
    
    /**
     * For Minecraft-compatible block models quads and vertex buffers.<p>
     * 
     * This format includes a light map but no vertex normals. In standard
     * rendering the lightmap coordinates are typically not populated in 
     * model outputs and instead computed and populated from world light values.<p>
     * 
     * Plug-ins may elect to interpret lightmap values sent by models as
     * a minimum sky/block lightmap to achieve emissive rendering, but are
     * not required to do so.
     */
    FabricVertexFormat STANDARD_BLOCK = FabricVertexFormatImpl.STANDARD_BLOCK;
    
    /**
     * This format is uniquely significant: it is used for Minecraft baked quads
     * that have been cast to FabricBakedQuads for consumption by the rendering
     * plug-in.  It signals the plug-in to assume the vertex format is whichever
     * format is appropriate for the current render context. (Item or Block.)<p>
     * 
     * Bespoke quads and model implementations should avoid using this format, 
     * and instead declare a specific vertex format.
     */
    FabricVertexFormat STANDARD_UNSPECIFIED = FabricVertexFormatImpl.STANDARD_UNSPECIFIED;
    
    /**
     * Unique identifier for this format.  A name-spaced identifier is used to
     * encourage re-use of community-developed formats across models and plug-ins.
     */
    Identifier id();
    
    /**
     * The number of integers it takes to form a complete quad.
     */
    int integerQuadStride();
    
    /**
     * True if this format contains more information than a standard vertex format.
     * A false result does <em>not</em> imply this is a standard format!
     */
    default boolean isExtended() {
        return integerQuadStride() > 28;
    }
    
    /**
     * True if the first 28 integers in this format are compatible with {@link #STANDARD_ITEM}
     * in the context of model outputs.  If the format is also extended, it implies the format
     * has a broken vertex stride, because additional attributes will need to come after
     * the first 28 integers.
     */
    boolean isItemModelCompatible();
    
    /**
     * True if the first 28 integers in this format are compatible with {@link #STANDARD_BLOCK}
     * in the context of model outputs.  If the format is also extended, it implies the format
     * has a broken vertex stride, because additional attributes will need to come after
     * the first 28 integers.
     */
    boolean isBlockModelCompatible();
    
    /**
     * The number of texture layers (additional UV and color coordinates) available in this format.
     * Will be at least one for textured block and item rendering, but some specialized
     * formats could report zero.
     */
    int textureDepth();
}
