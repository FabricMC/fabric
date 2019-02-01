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
     * Vertices on {@link QuadMaker} are mutable.
     */
    @Override
    VertexEditor vertex(int vertexIndex);
    
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
     * In static mesh building, causes quad to be appended to the mesh being built.
     * In a dynamic render context, causes quad to be output for rendering.
     * In all cases, invalidates the current instance.
     */
    void emit();
}
