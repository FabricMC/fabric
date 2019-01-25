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
 * Similar in concept to {@link BufferBuilder} but simpler
 * and not tied to NIO or any other specific implementation.
 * Decouples models from the vertex format(s) used by
 * ModelRenderer to allow compatibility across diverse implementations.<p>
 */
public interface VertexBuilder {
    /**
     * Must be called at the start of each quad, before
     * sending any vertex data.  Material must be an
     * instanced provided by the active {@link ModelRenderer}.
     */
    void begin(ModelMaterial material);
    
    /**
     * Call to flush a completed quad, after calling {@link #begin(ModelMaterial)}
     * and inputing quad properties and vertex data.<p>
     * 
     * Implementations should reset the builder state when this
     * method is called (clear normals, colors, etc.), unless the
     * state is explicitly meant to persist across invocations, as
     * defined in method docs. For example, see {@link #setQuadCullFace(Direction)}.<p>
     */
    void end();
    
    /**
     * Concise alias for {@link #end()} followed by {@link #begin(ModelMaterial)}.
     */
    default void endBegin(ModelMaterial material) {
        end();
        begin(material);
    }
    
    /**
     * If non-null, quad is coplanar with a block face which, if known, simplifies
     * or shortcuts geometric analysis that might otherwise be needed.
     * Set to null if quad is not coplanar or if this is not known. <p>
     * 
     * Value remains in effect for all subsequent quads sent to this consumer until changed.<p>
     * 
     * This is different than the value reported by {@link BakedQuad#getFace()}. That value
     * is computed based on face geometry and must be non-null in vanilla quads.
     * Model render implementations will emulate this behavior as needed.
     */
    void setQuadCullFace(Direction face);
    
    /**
     * Value functions identically to {@link BakedQuad#getColorIndex()} and is
     * used by renderer / model builder in same way.  Value remains in effect
     * for all subsequent quads sent to this consumer until changed. Default value is -1.
     */
    void setColorIndex(int colorIndex);
    
    /**
     * Enables bulk vertex data transfer using the standard Minecraft vertex formats.
     * This method should be performant whenever caller's vertex representation makes it feasible.<p>
     * 
     * Calling this method does not begin or end a quad.  It should be called after {@link #begin(ModelMaterial)}.
     * Intended use is for quick input when formats allow.
     */
    void putStandardQuadData(int[] quadData, int startIndex, boolean isItem);
    
    /**
     * Sets the geometric vertex position for the given vertex, 
     * relative to block origin. (0,0,0).  Minecraft rendering is designed
     * for models that fit within a single block space and is recommended 
     * that coordinates remain in the 0-1 range, with multi-block meshes
     * split into multiple per-block models.
     */
    void setVertex(int vertexIndex, float x, float y, float z);
    
    /**
     * All-at-once per-vertex method for the major vertex components.
     * Only handles first texture layer.
     */
    default void setVertex(int vertexIndex, float x, float y, float z, int color, float u, float v) {
        setVertex(vertexIndex, x, y, z);
        setColorTexture(vertexIndex, color, u, v);
    }
    
    /**
     * All-at-once per-vertex method for the major vertex components.
     * Includes normals. Only handles first texture layer.
     */
    default void setVertex(int vertexIndex, float x, float y, float z, int color, float u, float v, float normX, float normY, float normZ) {
        setVertex(vertexIndex, x, y, z, color, u, v);
        setVertexNormal(vertexIndex, normX, normY, normZ);
    }
    
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
    void setVertexNormal(int vertexIndex, float x, float y, float z);
    
    /**
     * Sets vertex color and texture coordinate for the given vertex and texture layer.<p>
     * 
     * TEXTURE COORDINATES MUST BE PRE-BAKED. The {@link ModelRenderer} implementation
     * will not handle texture interpolation or adjustment.<p>
     * 
     * Color should have red in the low byte and alpha in the high byte.  
     * The {@link ModelRenderer} implementation will handle any component 
     * swizzling needed for OpenGL or to account for local endian-ness.<p>
     */
    default void setColorTexture(int vertexIndex, int textureLayer, int color, float u, float v) {
        setColor(vertexIndex, textureLayer, color);
        setTexture(vertexIndex, textureLayer, u, v);
    }
    
    /**
     * Concise version of {@link #setColorTexture(int, int, int, float, float)} for single-layer models.
     */
    default void setColorTexture(int vertexIndex, int color, float u, float v) {
        setColor(vertexIndex, 0, color);
        setTexture(vertexIndex, 0, u, v);
    }
    
    /**
     * Per-vertex brightness input.
     */
    void setBrightness(int vertexIndex, int brightness);
    
    /**
     * Per-vertex color coordinate input. 
     * 
     * @param layerIndex Texture layer.  Must be within the range implied by the
     * texture depth of the material.
     */
    void setColor(int vertexIndex, int layerIndex, int color);
    
    /**
     * Concise version of {@link #setColor(int, int, int)} for single-layer models.
     */
    default void setColor(int vertexIndex, int color) {
        setColor(vertexIndex, 0, color);
    }
    
    /**
     * Per-vertex texture coordinate input. 
     * 
     * @param layerIndex Texture layer.  Must be within the range implied by the
     * texture depth of the material.
     * 
     * @param u  texture coordinate - MUST BE PRE-BAKED
     * @param v  texture coordinate - MUST BE PRE-BAKED
     */
    void setTexture(int vertexIndex, int layerIndex, float u, float v);
    
    /**
     * Concise version of {@link #setTexture(int, int, float, float)} for single-layer models.
     */
    default void setTexture(int vertexIndex, float u, float v) {
        setTexture(vertexIndex, 0, u, v);
    }
    
    /**
     * Quad color input - accepts all four vertices at once.<p>
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
    default void setQuadColor(int layerIndex, int c0, int c1, int c2, int c3) {
        setColor(0, layerIndex, c0);
        setColor(1, layerIndex, c1);
        setColor(2, layerIndex, c2);
        setColor(3, layerIndex, c3);
    }
    
    /**
     * Concise version of {@link #setColor(int, int, int, int, int)} for single-layer models.
     */
    default void setQuadColor(int c0, int c1, int c2, int c3) {
        setQuadColor(0, c0, c1, c2, c3);
    }
    
    /**
     * Quad brightness input - accepts all four vertices at once.<p>
     */
    default void setQuadBrightness(int b0, int b1, int b2, int b3) {
        setBrightness(0, b0);
        setBrightness(1, b1);
        setBrightness(2, b2);
        setBrightness(3, b3);
    }
}
