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
public interface QuadPackager {
    /**
     * Must be called at the start of each quad, before
     * sending any vertex data.  Material must be an
     * instanced provided by the active {@link ModelRenderer}.
     */
    void beginQuad(ModelMaterial material);
    
    /**
     * Call to flush a completed quad, after calling {@link #beginQuad(ModelMaterial)}
     * and inputing quad properties and vertex data.<p>
     * 
     * Implementations should reset the builder state when this
     * method is called (clear normals, colors, etc.), unless the
     * state is explicitly meant to persist across invocations, as
     * defined in method docs. For example, see {@link #setQuadCullFace(Direction)}.<p>
     */
    void buildQuad();
    
    /**
     * Concise alias for {@link #buildQuad()} followed by {@link #beginQuad(ModelMaterial)}.
     */
    default void endBeginQuad(ModelMaterial material) {
        buildQuad();
        beginQuad(material);
    }
    
    PackagedQuads buildPackage();
    
    /**
     * If non-null, quad is coplanar with a block face which, if known, simplifies
     * or shortcuts geometric analysis that might otherwise be needed.
     * Set to null if quad is not coplanar or if this is not known. <p>
     * 
     * Value remains in effect for all subsequent quads sent to this builder until changed.<p>
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
     * Calling this method does not begin or end a quad.  It should be called after {@link #beginQuad(ModelMaterial)}.
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
    void setPos(int vertexIndex, float x, float y, float z);
    
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
     * Per-vertex texture coordinate input. 
     * 
     * @param layerIndex Texture layer.  Must be within the range implied by the
     * texture depth of the material.
     * 
     * @param u  texture coordinate - MUST BE PRE-BAKED
     * @param v  texture coordinate - MUST BE PRE-BAKED
     */
    void setTexture(int vertexIndex, int layerIndex, float u, float v);
}
