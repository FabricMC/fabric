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

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

/**
 * Handles most texture-baking use cases for model loaders and model libraries
 * via {@link #bakeTextures(QuadMaker, int, Sprite, int)}. Also used by the API
 * itself to implement automatic block-breaking models for enhanced models.
 */
public class TextureHelper {
    /**
     * Causes texture to appear with no rotation.
     * Pass in bakeFlags parameter to {@link #bakeTextures(QuadMaker, int, Sprite, int)}.
     */
    public static final int ROTATE_NONE = 0;
    
    /**
     * Causes texture to appear rotated 90 deg. relative to nominal face.
     * Pass in bakeFlags parameter to {@link #bakeTextures(QuadMaker, int, Sprite, int)}.
     */
    public static final int ROTATE_90 = 1;
    
    /**
     * Causes texture to appear rotated 180 deg. relative to nominal face.
     * Pass in bakeFlags parameter to {@link #bakeTextures(QuadMaker, int, Sprite, int)}.
     */
    public static final int ROTATE_180 = 2;
    
    /**
     * Causes texture to appear rotated 270 deg. relative to nominal face.
     * Pass in bakeFlags parameter to {@link #bakeTextures(QuadMaker, int, Sprite, int)}.
     */
    public static final int ROTATE_270 = 3;
    
    /**
     * When enabled, texture coordinate are assigned based on vertex position.
     * Any existing uv coordinates will be replaced.
     * Pass in bakeFlags parameter to {@link #bakeTextures(QuadMaker, int, Sprite, int)}.<p>
     * 
     * UV lock always derives texture coordinates based on nominal face, even
     * when the quad is not co-planar with that face, and the result is
     * the same as if the quad were projected onto the nominal face, which
     * is usually the desired result.<p>
     */
    public static final int LOCK_UV = 4;
    
    /**
     * When set, U texture coordinates for the given texture are 
     * flipped as part of baking. Can be useful for some randomization
     * and texture mapping scenarios. Results are different than what
     * can be obtained via rotation and both can be applied.
     * UV lock must be disabled for this feature to work.
     * Pass in bakeFlags parameter to {@link #bakeTextures(QuadMaker, int, Sprite, int)}.
     */
    public static final int FLIP_U = 8;
    
    /**
     * Same as {@link #FLIP_U} but for V coordinate.
     */
    public static final int FLIP_V = 16;
    
    /**
     * UV coordinates by default are assumed to be 0-16 scale for consistency
     * with conventional Minecraft model format. This is scaled to 0-1 during
     * baking before interpolation. Model loaders that already have 0-1 coordinates
     * can avoid wasteful multiplication/division by passing 0-1 coordinates directly.
     * Pass in bakeFlags parameter to {@link #bakeTextures(QuadMaker, int, Sprite, int)}.
     */
    public static final int NORMALIZED = 32;
    
    private static final float NORMALIZER = 1f / 16f;
    
    /**
     * Bakes textures in the provided vertex data, handling UV locking,
     * rotation, interpolation, etc. Textures must not be already baked. 
     */
    public static void bakeTextures(QuadMaker quad, int textureIndex, Sprite sprite, int bakeFlags) {
        if(quad.nominalFace() != null && (LOCK_UV & bakeFlags) != 0) {
            // Assigns normalized UV coordinates based on vertex positions
            applyModifier(quad, textureIndex, UVLOCKERS[quad.nominalFace().getId()]);
        } else if ((NORMALIZED & bakeFlags) == 0) {
            // Scales from 0-16 to 0-1
            applyModifier(quad, textureIndex, (q, i, t) -> q.uv(i, t, q.u(i, t) * NORMALIZER, q.v(i, t) * NORMALIZER));
        }
        
        final int rotation = bakeFlags & 3;
        if(rotation != 0) {
             // Rotates texture around the center of sprite.
             // Assumes normalized coordinates.
            applyModifier(quad, textureIndex, ROTATIONS[rotation]);
        }
        
        if((FLIP_U & bakeFlags) != 0) {
            // Inverts U coordinates.  Assumes normalized (0-1) values.
            applyModifier(quad, textureIndex, (q, i, t) -> q.uv(i, t, 1 - q.u(i, t), q.v(i, t)));
        }
        
        if((FLIP_V & bakeFlags) != 0) {
            // Inverts V coordinates.  Assumes normalized (0-1) values.
            applyModifier(quad, textureIndex, (q, i, t) -> q.uv(i, t, q.u(i, t), 1 - q.v(i, t)));
        }
        
        interpolate(quad, textureIndex, sprite);
    }

    /**
     * Faster than sprite method. Sprite computes span and normalizes inputs each call, 
     * so we'd have to denormalize before we called, only to have the sprite renormalize immediately.
     */
    private static void interpolate(QuadMaker q, int textureIndex, Sprite sprite) {
        final float uMin = sprite.getMinU();
        final float uSpan = sprite.getMaxU() - uMin;
        final float vMin = sprite.getMinV();
        final float vSpan = sprite.getMaxV() - vMin;
        for(int i = 0; i < 4; i++) {
            q.uv(i, textureIndex, uMin + q.u(i, textureIndex) * uSpan, vMin + q.v(i, textureIndex) * vSpan);
        }
    }
    
    @FunctionalInterface
    private static interface VertexModifier {
        void apply(QuadMaker quad, int vertexIndex, int textureIndex);
    }
    
    private static void applyModifier(QuadMaker quad, int textureIndex, VertexModifier modifier) {
        for(int i = 0; i < 4; i++) {
            modifier.apply(quad, i, textureIndex);
        }
    }
    
    private static final VertexModifier [] ROTATIONS = new VertexModifier[] {
        null,
        (q, i, t) -> q.uv(i, t, q.v(i, t), q.u(i, t)), //90
        (q, i, t) -> q.uv(i, t, 1 - q.u(i, t), 1 - q.v(i, t)), //180
        (q, i, t) -> q.uv(i, t, 1 - q.v(i, t), q.u(i, t)) // 270
    };
    
    private static final VertexModifier [] UVLOCKERS = new VertexModifier[6];

    static {
        UVLOCKERS[Direction.EAST.getId()] = (q, i, t) -> q.uv(i, t, 1 - q.z(i), 1 - q.y(i));
        UVLOCKERS[Direction.WEST.getId()] = (q, i, t) -> q.uv(i, t, q.z(i), 1 - q.y(i));
        UVLOCKERS[Direction.NORTH.getId()] = (q, i, t) -> q.uv(i, t, 1 - q.x(i), 1 - q.y(i));
        UVLOCKERS[Direction.SOUTH.getId()] = (q, i, t) -> q.uv(i, t, q.x(i), 1 - q.y(i));
        UVLOCKERS[Direction.DOWN.getId()] = (q, i, t) -> q.uv(i, t, q.x(i), 1 - q.z(i));
        UVLOCKERS[Direction.UP.getId()] = (q, i, t) -> q.uv(i, t, q.x(i), 1 - q.z(i));
    }
}
