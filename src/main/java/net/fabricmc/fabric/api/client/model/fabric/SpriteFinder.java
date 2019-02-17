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

import net.fabricmc.fabric.impl.client.model.SpriteFinderImpl;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;

/**
 * Indexes a texture atlas to allow fast lookup of Sprites from
 * baked vertex coordinates.  Main use is for {@link Mesh}-based models
 * to generate vanilla quads on demand without tracking and retaining
 * the sprites that were baked into the mesh. In other words, this class 
 * supplies the sprite parameter for {@link Quad#toBakedQuad(int, Sprite, boolean)}.
 */
public interface SpriteFinder {
    /**
     * Retrieves or creates the finder for the given atlas.
     * Instances should not be retained as fields or they must be
     * refreshed whenever there is a resource reload or other event
     * that causes atlas textures to be re-stitched.
     */
    public static SpriteFinder get(SpriteAtlasTexture atlas) {
        return SpriteFinderImpl.get(atlas);
    }
    
    /**
     * Finds the atlas sprite containing the vertex centroid of the quad.
     * Vertex centroid is essentially the mean u,v coordinate - the intent being
     * to find a point that is unambiguously inside the sprite (vs on an edge.)<p>
     * 
     * Should be reliable for any convex quad or triangle. May fail for non-convex quads.
     * Note that all the above refers to u,v coordinates. Geometric vertex does not matter,
     * except to the extent it was used to determine u,v.
     */
    Sprite find(Quad quad, int textureIndex);

    /**
     * Alternative to {@link #find(Quad, int)} when vertex centroid is already
     * known or unsuitable.  Coordinates must be in sprite interior for reliable results.
     */
    Sprite find(float u, float v);
}
