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
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;

/**
 * Similar in concept to {@link BufferBuilder} with 
 * restricted options for mutating vertex data.<p>
 * 
 * Decouples models from the vertex format(s) used by
 * ModelRenderer to allow compatibility across diverse implementations.<p>
 * 
 * Caller must call {@link #vertex(float, float, float)} to start each vertex, 
 * but remaining per-vertex operations are more loosely ordered
 * because the inner vertex format isn't known to the caller.<p>
 * 
 * A sequence restriction does apply when the material texture depth > 1.
 * In that case, calls to {@link #colorTexture(int, float, float)} must
 * come in layer order. (0, 1, etc..) or the layer-indexed methods
 * {@link #setColor(int, int, int, int, int)} and {@link #setTexture(int, float, float)}
 * can be used.
 */
public interface ModelVertexConsumer {
    /**
     * Must be called at the start of each quad, before
     * sending any vertex data.  Material must be an
     * instanced provided by the active {@link ModelRenderer}.
     */
    void begin(ModelMaterial material);
    
    /**
     * Call to explicitly flush a completed quad. 
     * Useful in dynamic rendering contexts to flush the last quad.
     * Will be called implicitly when {@link #begin(ModelMaterial)} is
     * called or when this is a {@link ModelBuilder} instance and a model is built.
     */
    void end();
    
    /**
     * Value functions identically to {@link BakedQuad#getColorIndex()} and is
     * used by renderer / model builder in same way.  Value remains in effect
     * for all subsequent quads sent to this consumer until changed. Default value is -1.
     */
    void setQuadColorIndex(int colorIndex);
    
    /**
     * Value functions identically to {@link BakedQuad#getFace()} and is
     * used by renderer / model builder in same way.  Value remains in effect
     * for all subsequent quads sent to this consumer until changed.<p>
     * 
     * This is NOT the cull face and must be non-null. It controls some aspects 
     * in the standard Minecraft lighting model and is necessary to emulate
     * standard rendering.  The default value is {@link Direction#UP}.
     */
    void setQuadLightingFace(Direction face);
    
    /**
     * Enables bulk vertex data transfer using the standard Minecraft vertex formats.
     * This method or {@link #putStandardQuadData(int[], int, boolean)} are preferable
     * whenever caller's vertex representation makes them feasible.<p>
     * 
     * When this method (or the full quad variant) are used, it is not valid to call
     * {@link #vertex(float, float, float)} or the per-element additive methods including
     * {@link #colorTexture()} and {@link #brightness(int)}.
     * 
     * However, it IS valid to call {@link #setBrightness()}, {@link #setColor()} and
     * other set... methods before calling {@link #end()} or {@link #begin(ModelMaterial)}.
     * 
     * @param vertexData array containing vertex data in standard format
     * @param startIndex index to start of vertex data in array
     * @param isItem If true, last (7th) integer will be interpreted as packed
     * vertex normals.  If false, (block format) last integer will be interpreted as brightness.
     */
    void putStandardVertexData(int[] vertexData, int startIndex, boolean isItem);
    
    /**
     * Same purpose as {@link #putStandardVertexData(int[], int, boolean)} but accepts
     * entire quad at once.  Requires that vertex data be closely packed into 28 
     * contiguous array elements.
     * 
     * {@link ModelVertexConsumer} implementations should override this default
     * implementation if it offers a performance benefit.
     */
    default void putStandardQuadData(int[] quadData, int startIndex, boolean isItem) {
        putStandardVertexData(quadData, startIndex, isItem);
        putStandardVertexData(quadData, startIndex + 7, isItem);
        putStandardVertexData(quadData, startIndex + 14, isItem);
        putStandardVertexData(quadData, startIndex + 21, isItem);
    }
    
    /**
     * Starts a new vertex and sets the geometric vertex position, 
     * relative to block origin. (0,0,0).  Minecraft rendering is designed
     * for models that fit within a single block space and is recommended 
     * that coordinates remain in the 0-1 range, with multi-block meshes
     * split into multiple per-block models.
     */
    void vertex(float x, float y, float z);
    
    /**
     * Adds a vertex normal. Models that have per-vertex
     * normals should include them to get correct lighting when it matters.
     * Models that have pre-computed face normals <em>can</em> include them
     * in hope of avoiding re-computation in the renderer but this may or may
     * not be useful depending on the implementation and use case<p>
     * 
     * {@link ModelRenderer} implementations should honor vertex normals for
     * diffuse lighting - modifying vertex color(s) or packing normals in the vertex 
     * buffer as appropriate for the rendering method/vertex format in effect.<p>
     */
    void normal(float x, float y, float z);
    
    /**
     * Adds a vertex color and texture coordinate.<p>
     * 
     * TEXTURE COORDINATES MUST BE PRE-BAKED. The {@link ModelRenderer} implementation
     * will not handle texture interpolation or adjustment.<p>
     * 
     * Color should have red in the low byte and alpha in the high byte.  
     * The {@link ModelRenderer} implementation will handle any component 
     * swizzling needed for OpenGL or to account for local endian-ness.<p>
     */
    void colorTexture(int color, float u, float v);
    
    /**
     * Adds a minimum lightmap to be used when emissive rendering is enabled. 
     */
    void brightness(int brightness);
    
    /**
     * Single-call for multiple attributes. Some implementations may
     * override this if it offers an optimization benefit.
     */
    default void colorTextureBrightness(int color, float u, float v, int brightness) {
        colorTexture(color, u, v);
        brightness(brightness);
    }
    
    /**
     * Indexed texture coordinate input. 
     * 
     * @param vertexIndex  Vertex must have already been created via {@link #vertex()} or
     * one of the direct transfer methods: {@link #putStandardVertexData(int[], int, boolean)} or
     * {@link #putStandardQuadData(int[], int, boolean)}<p>
     * 
     * @param layerIndex Texture layer.  Must be within the range implied by the
     * texture depth of the material.
     * 
     * @param u  texture coordinate - MUST BE PRE-BAKED
     * @param v  texture coordinate - MUST BE PRE-BAKED
     */
    void setTexture(int vertexIndex, int layerIndex, float u, float v);
    
    /**
     * Concise version of {@link #setTexture(int, float, float)} for single-layer models.
     */
    default void setTexture(int vertexIndex, float u, float v) {
        setTexture(vertexIndex, 0, u, v);
    }
    
    /**
     * Indexed color input - accepts all four vertices at once.<p>
     * 
     * All vertices must have already been created via {@link #vertex()} or
     * one of the direct transfer methods: {@link #putStandardVertexData(int[], int, boolean)} or
     * {@link #putStandardQuadData(int[], int, boolean)}<p>
     * 
     * @param layerIndex Texture layer.  Must be within the range implied by the
     * texture depth of the material.<p>
     * 
     * @param c0  first vertex color
     * @param c1  second vertex color
     * @param c2  third vertex color
     * @param c3  fourth vertex color
     */
    void setColor(int layerIndex, int c0, int c1, int c2, int c3);
    
    /**
     * Concise version of {@link #setColor(int, int, int, int, int)} for single-layer models.
     */
    default void setColor(int c0, int c1, int c2, int c3) {
        setColor(0, c0, c1, c2, c3);
    }
    
    /**
     * Indexed brightness input - accepts all four vertices at once.<p>
     * 
     * All vertices must have already been created via {@link #vertex()} or
     * one of the direct transfer methods: {@link #putStandardVertexData(int[], int, boolean)} or
     * {@link #putStandardQuadData(int[], int, boolean)}<p>
     */
    void setBrightness(int b0, int b1, int b2, int b3);
}
