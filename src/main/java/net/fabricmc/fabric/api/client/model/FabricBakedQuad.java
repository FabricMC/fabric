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

import net.minecraft.block.Block;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

/**
 * Mirror of {@link BakedQuad} in purpose and operation but provides extended
 * vertex data and does not force concrete subclassing of BakedQuad.<p>
 * 
 * Fabric causes BakedQuads to implement FabricBakedQuad, so you if you
 * already have a BakedQuad instance you can safely cast it to FabricBakedQuad
 * any place a FabricBakedQuad instance is required.<p>
 * 
 * Conversely, a FabricBakedQuad does <em>not</em> need to be a BakedQuad. 
 * However, it is trivial to convert to convert to a BakedQuad and a default 
 * method for that purpose is provided. See {@link #toBakedQuad()}<p>
 * 
 * For compatibility, implementations must ensure the first texture layer is
 * always the desired default appearance when a rendering plugin is not available,
 * and also ensure the first layer is associated with {@link Block#getRenderLayer()}.
 */
public interface FabricBakedQuad {

    
    /**
     * Serves the same purpose as {@link BakedQuad#getVertexData()} but
     * data may be extended to conform to a fabric vertex format.<p>
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
     * Same as {@link BakedQuad#hasColor()} but will only apply to texture
     * layers that are configured to use it via {@link #getLightingFlags()}.
     */
    default boolean hasColor() {
        return getColorIndex() != -1;
    }

    /**
     * Same as {@link BakedQuad#getColorIndex()} but will only apply to texture
     * layers that are configured to use it. 
     */
    default int getColorIndex() {
        return -1;
    }

    /**
     * Same as {@link BakedQuad#getFace()}. Identifies the geometric face of 
     * this polygon. Must be null if not co-planar with a block face.
     */
    Direction getFace();

    /**
     * Same as {@link BakedQuad#getSprite()}
     */
    public Sprite getSprite();
     
    int getRenderLayerFlags();

    public FabricVertexFormat getFormat();

    // block color
    // lightmap
    // world diffuse
    // lightmap diffuse
    // world ao
    // lightmap ao
    // raw quad

    public int getLightingFlags();
    
    /**
     * Create a new baked quad based on the first texture layer.
     * The first 28 elements of all vertex formats are identical to ensure
     * this is always a trivial operation, because the need for backward
     * compatibility is inevitable.<p>
     * 
     * Non-dynamic implementations should override this method to cache the result.
     */
    public default BakedQuad toBakedQuad()
    {
        int[] vertexData = new int[28];
        System.arraycopy(getVertexData(), firstVertexIndex(), vertexData, 0, 28);
        int color0 = getColorIndex(); // TODO - check if color applies to layer 0
        return new BakedQuad(vertexData, color0, getFace(), getSprite());
    }
    
    /**
     * All implementations of FabricBakedQuad are expected to be immutable by default.<p>
     * 
     * "Immutable" in this case means that all <em>public</em> properties and vertex data
     * reported by this instance will never change.  An immutable instance can therefore
     * be reliably wrapped or aggregated by some other mod or implementation without copying.<p>
     * 
     * Note that this implies a quad will have either a block or item vertex format, and
     * will have no way for this to be changed.  In particular, mod authors should be 
     * cautious of capturing a quad with an unspecified (context-dependent) vertex format
     * and using it in a different context.  In such cases, depending on usage,  it may be
     * necessary to infer the format based on the context in which the quad was obtained, 
     * and then create an instance with the specific and defined vertex format you require.<p>
     * 
     * Consumers that "wrap" or keep a reference to an FabricBakedQuad instance should always
     * check {@link #isImmutable()} and if it returns false, obtain an immutable reference via
     * {@link #toImmutable()}.  (Unless some specific functionality in this implementation
     * provides for using or keeping a mutable reference.)<p>
     * 
     * The means for obtaining or editing a mutable instance are left to implementations.
     * Mutable implementations <em>must</em> override this method to return true.
     */
    public default boolean isImmutable() {
        return true;
    }
    
    /**
     * All implementations that offer mutability <em>must</em> override this method to 
     * produce a reliably immutable instance.
     */
    public default FabricBakedQuad toImmutable() {
        return this;
    }
}
