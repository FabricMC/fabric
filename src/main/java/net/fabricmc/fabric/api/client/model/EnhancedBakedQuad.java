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

package net.fabricmc.fabric.api.client.model;

/**
 * Similar to BakedQuad in purpose and operation but not meant
 * to be an extension of that class.  Some key differences:<p>
 * 
 * <li>Does not expose a face attribute because that is baked into the vertex data.</li><p>
 * 
 * <li>Does not expose a sprite attribute because enhanced quads can be
 * multi-textured. Not intended to support re-texturing.</li><p>
 * 
 * <li>Vertex data includes additional meta-data to support efficient 
 * chunk rebuilds and hide details not useful for the implementation.
 * Vertex data <em>must</em> be packed via the EnhancedQuadBakery.</li>
 */
public interface EnhancedBakedQuad {
    
    /**
     * Serves the same purpose as BakedQuad.getVertexData() but
     * data must be baked with EnhancedQuadBakery.<p>
     * 
     * The other significant difference is that quad data does not 
     * need to start at array index 0. Consumer will look for vertex
     * data starting at {@link #firstVertexIndex()}.  This can improve
     * efficiency for implementations that keep multiple quads in a single array.
     */
    int[] getVertexData();
    
    /**
     * If your vertex data is in a shared array, override this to provide
     * the start index instead of copying your data to a transfer array.
     */
    default int firstVertexIndex() {
        return 0;
    }
    
    /**
     * Same as BakedQuad.hasColor() but will only apply to texture
     * layers that are configured to use it.  Configuration is
     * packed in baked vertex data by EnhancedQuadBakery.
     */
    default boolean hasColor() {
       return getColorIndex() != -1;
    }

    /**
     * Same as BakedQuad.getColorIndex() but will only apply to texture
     * layers that are configured to use it.  Configuration is
     * packed in baked vertex data by EnhancedQuadBakery.
     */
    default int getColorIndex() {
       return -1;
    }
}
