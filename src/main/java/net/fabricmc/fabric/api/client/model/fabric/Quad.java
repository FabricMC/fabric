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
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

/**
 * Utility interface for working with quad data packed by
 * {@link MeshBuilder}.  Enables models to do analysis,
 * re-texturing or standard quad generation without knowing the
 * renderer's vertex formats and without retaining redundant information.<p>
 */
public interface Quad {
    /**
     * Reads baked vertex data and outputs a standard baked quad 
     * vertex data in the given array and location. Uses texture
     * coordinates and colors from the indicated layer.<p>
     * 
     * Primary use case is generating damage models or other operations outside
     * the renderer. Models that are able to generate mesh geometry on the fly or 
     * retrieve it from a pre-baked source may find that a cleaner approach.<p>
     * 
     * Note that for damage models, the original texture sprite is needed
     * to derive the non-interpolated UV coordinates for re-texturing.
     * Model renderers only accept baked UV coordinates and so models
     * that use this approach still need to track the original sprites.
     * 
     * @param source Data previously packed by {@link FastVertexBuilder}.
     * 
     * @param sourceIndex Index where packed data starts.
     * 
     * @param layerIndex The texture layer to be used for the quad.
     * Pass 0 for single-layer quads.
     * 
     * @param target Target array for the baked quad data.
     * 
     * @param targetIndex Starting position in target array - array must have
     * at least 28 elements available at this index.
     * 
     * @param isItem If true, will output vertex normals. Otherwise will output
     * lightmaps, per Minecraft vertex formats for baked models.
     */
    void toMinecraft(int layerIndex, int[] target, int targetIndex, boolean isItem);
    
    /**
     * Extracts all quad properties to the given vertex builder.
     * Typically the target will be something implemented by a 
     * model or model library. Meant for re-texturing, analysis
     * and transformation use cases.
     */
    void toPackager(MeshBuilder target);
    
    /**
     * Retrieves the material serialized with the quad.
     */
    ModelMaterial material();
    
    /**
     * Retrieves the quad color index serialized with the quad.
     */
    int colorIndex();
    
    /**
     * Equivalent to {@link BakedQuad#getFace()}. This is the face used for vanilla lighting
     * calculations and will be the block face to which the quad is most closely aligned. Always
     * the same as cull face for quads that are on a block face, but never null.
     */
    Direction lightFace();
    
    /**
     * If non-null, quad should not be rendered in-world if the 
     * opposite face of a neighbor block occludes it.
     */
    Direction cullFace();
    
    /**
     * See {@link QuadMaker#nominalFace(Direction)}.
     */
    Direction nominalFace();
    
    Vector3f faceNormal();
    
    /**
     * Generates a new BakedQuad instance with texture
     * coordinates and colors from the indicated layer.<p>
     * 
     * @param source Data previously packed by {@link FastVertexBuilder}.
     * 
     * @param sourceIndex Index where packed data starts.
     * 
     * @param layerIndex The texture layer to be used for the quad.
     * Pass 0 for single-layer quads.
     * 
     * @param sprite  {@link FastVertexBuilder} does not serialize sprites,
     * so the sprite must be provided. Models that use this method need to
     * track sprites per quad/layer.
     * 
     * @param isItem If true, will output vertex normals. Otherwise will output
     * lightmaps, per Minecraft vertex formats for baked models.
     * 
     * @return A new baked quad instance with the closest-available appearance
     * supported by vanilla features. Will retain emissive light maps, for example,
     * but the standard Minecraft renderer will not use them.
     */
    default BakedQuad toBakedQuad(int layerIndex, Sprite sprite, boolean isItem) {
        int vertexData[] = new int[28];
        toMinecraft(layerIndex, vertexData, 0, isItem);
        return new BakedQuad(vertexData, colorIndex(), lightFace(), sprite);
    }
    
    Vertex vertex(int vertexIndex);
}
