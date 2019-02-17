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

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;

/**
 * A mutable {@link Quad} instance. Used during static mesh 
 * building and also for dynamic renders/mesh transforms.<p>
 * 
 * Instances of {@link QuadMaker} will practically always be
 * threadlocal and/or reused - do not retain references.
 */
public interface QuadMaker extends Quad {
    /**
     * Assigns a different material to this quad. Useful for transformation of 
     * existing meshes because lighting and texture blending are controlled by material.<p>
     * 
     * Restriction: texture depth of the new material must be the same or less than 
     * the texture depth of the material used to create the quad.  This restriction 
     * prevents the need for renderer implementations to allocate additional storage
     * after a quad is already loaded for buffering or editing.<p>
     * 
     * Violations of this restriction will throw UnsupportedOperationException errors.
     */
    QuadMaker material(RenderMaterial material);
        
    /**
     * If non-null, quad is coplanar with a block face which, if known, simplifies
     * or shortcuts geometric analysis that might otherwise be needed.
     * Set to null if quad is not coplanar or if this is not known. 
     * Also controls face culling during block rendering.<p>
     * 
     * Null by default.<p>
     * 
     * When called with a non-null value, also sets {@link #nominalFace(Direction)}
     * to the same value.<p>
     * 
     * This is different than the value reported by {@link BakedQuad#getFace()}. That value
     * is computed based on face geometry and must be non-null in vanilla quads.
     * That computed value is returned by {@link #lightFace()}.
     */
    QuadMaker cullFace(Direction face);
    
    /**
     * Provides a hint to renderer about the facing of this quad. Not required,
     * but if provided can shortcut some geometric analysis if the quad is parallel to a block face. 
     * Should be the expected value of {@link #lightFace()}. Value will be confirmed
     * and if invalid the correct light face will be calculated.<p>
     * 
     * Null by default, and set automatically by {@link #cullFace()}.<p>
     * 
     * Models may also find this useful as the face for texture UV locking and rotation semantics.<p>
     * 
     * NOTE: This value is not persisted independently when the quad is encoded.
     * When reading encoded quads, this value will always be the same as {@link #lightFace()}.
     */
    QuadMaker nominalFace(Direction face);
    
    /**
     * Value functions identically to {@link BakedQuad#getColorIndex()} and is
     * used by renderer / model builder in same way. Default value is -1.
     */
    QuadMaker colorIndex(int colorIndex);
    
    /**
     * Enables bulk vertex data transfer using the standard Minecraft vertex formats.
     * This method should be performant whenever caller's vertex representation makes it feasible.<p>
     * 
     * Calling this method does not emit the quad.  
     */
    QuadMaker fromVanilla(int[] quadData, int startIndex, boolean isItem);
    
    /**
     * Encodes an integer tag with this quad that can later be retrieved via
     * {@link Quad#tag()}.  Useful for models that want to perform conditional
     * transformation or filtering on static meshes.
     */
    QuadMaker tag(int tag);
    
    /**
     * Sets the geometric vertex position for the given vertex, 
     * relative to block origin. (0,0,0).  Minecraft rendering is designed
     * for models that fit within a single block space and is recommended 
     * that coordinates remain in the 0-1 range, with multi-block meshes
     * split into multiple per-block models.<p>
     * 
     * Standard shader binding: gl_Vertex
     */
    QuadMaker pos(int vertexIndex, float x, float y, float z);
    
    /**
     * Same as {@link #pos(float, float, float)} but accepts vector type.
     */
    default QuadMaker pos(int vertexIndex, Vector3f vec) {
        return pos(vertexIndex, vec.x(), vec.y(), vec.z());
    }
    
    /**
     * Adds a vertex normal. Models that have per-vertex
     * normals should include them to get correct lighting when it matters.
     * Computed face normal is used when no vertex normal is provided.<p>
     * 
     * {@link Renderer} implementations should honor vertex normals for
     * diffuse lighting - modifying vertex color(s) or packing normals in the vertex 
     * buffer as appropriate for the rendering method/vertex format in effect.<p>
     * 
     * The "extra" parameter is for shader authors to make use of space normally wasted.
     * In most implementations it will be packed as a signed, normalized float 
     * with low effective precision: 1/127.<p>
     * 
     * Note for renderer authors: normals should always be buffered for shader
     * materials, even if the renderer's lighting model does not require normals
     * in the GPU. Populate with face normal when vertex normals aren't present.<p>
     * 
     * Standard shader binding: vec4 in_normal
     */
    QuadMaker normal(int vertexIndex, float x, float y, float z, float extra);
    
    /**
     * Same as {@link #normal(float, float, float, extra)} but accepts vector type.
     */
    default QuadMaker normal(int vertexIndex, Vector3f vec) {
        return normal(vertexIndex, vec.x(), vec.y(), vec.z(), 0);
    }
    
    /**
     * Same as {@link #normal(float, float, float, extra)} but accepts vector type.
     */
    default QuadMaker normal(int vertexIndex, Vector4f vec) {
        return normal(vertexIndex, vec.x(), vec.y(), vec.z(), vec.w());
    }
    
    /**
     * Minimum block brightness. Has no effect unless emissive lighting is
     * enabled in at least one texture of the material for this quad.
     * Standard shader binding: vec4 in_lightmap<p>
     * 
     * While this has a standard binding, it should not be used by
     * shaders if lighting is enabled in the material. Renderers may alter
     * the format of lightmaps to support the renderer's lighting model.
     */
    QuadMaker lightmap(int vertexIndex, int lightmap);
    
    /** 
     * Convenience: set lightmap for all vertices at once.
     */
    default QuadMaker lightmap(int b0, int b1, int b2, int b3) {
        lightmap(0, b0);
        lightmap(1, b1);
        lightmap(2, b2);
        lightmap(3, b3);
        return this;
    }
    
    /**
     * Set vertex color.
     */
    QuadMaker color(int vertexIndex, int textureIndex, int color);
    
    /** 
     * Convenience: set vertex color for all vertices at once.
     */
    default QuadMaker color(int textureIndex, int c0, int c1, int c2, int c3) {
        color(0, textureIndex, c0);
        color(1, textureIndex, c1);
        color(2, textureIndex, c2);
        color(3, textureIndex, c3);
        return this;
    }
    
    /**
     * Set texture coordinates.
     */
    QuadMaker uv(int vertexIndex, int textureIndex, float u, float v);
    
    /**
     * In static mesh building, causes quad to be appended to the mesh being built.
     * In a dynamic render context, causes quad to be output for rendering.
     * In all cases, invalidates the current instance.
     */
    void emit();
}
