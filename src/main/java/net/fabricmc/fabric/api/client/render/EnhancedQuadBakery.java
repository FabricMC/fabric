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

package net.fabricmc.fabric.api.client.render;

import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.util.math.Direction;

/**
 * Helper class to generate vertex data needed to implement EnhancedBakedQuad.<p>
 * 
 * For conventional quads, render results will be identical to normal Minecraft. However,
 * Enhanced Quads support the following additional features:<p>
 * 
 * <li>Multiple texture layers. Fabric will render these as separate quads using the
 * conventional Minecraft render pipeline but advanced rendering mods may render them in a single pass.</li><p>
 * 
 * <li>Custom lightmap / emissive rendering. You can specify an additive lightmap for emissive effects.</li><p>
 * 
 * <li>Per-layer control of BlockRenderLayer, emissive lightmap, diffuse shading and ambient occlusion.</li><p>
 * 
 * <li>Vertex normals.  These are not sent to the BlockRender pipeline (absent a rendering add-on) but are used
 * to improve diffuse shading for models with non-cube-aligned faces.</li><p>
 * 
 * <li>Support for non-square quads.  Uses an improved AO shading algorithm to handle these correctly.</li><p>
 *
 * <li>Vertex data includes face and tint usage metadata to simplify EnhancedQuad implementations and
 * enable performant and consistent application of vertex data.</li><p>
 * 
 * Usage:<br>
 * <li>Obtain an instance. You can and should reuse instances of this class if you are baking multiple quads.</li>
 * <li>Set vertex and quad attributes, texture depth, render layers, etc.  This can be done in any order.</li>
 * <li>Call {@link #outputBakedQuadData(int[], int)}</li>
 * <li>Repeat as needed with changes, optionally calling {@link #clearSettings()} first.</li>  
 * 
 */
public final class EnhancedQuadBakery {
    /**
     * Controls alpha and mip-map settings for each texture layer via BlockRenderLayer.<p>
     * 
     * Some restrictions apply:<p>
     * <li>If SOLID occurs it must be first. Only one SOLID layer is possible. (Anything else would be pointless.)</li>
     * <li>TRANSLUCENT layers must be "above" SOLID and CUTOUT layers.</li>
     */
    public final void setRenderLayer(TextureDepth textureLayer, BlockRenderLayer layer) {
        
    }
    
    /**
     * Sets texture dept for next output quad. Changing does not clear values 
     * for unused layers - higher layers not used simply won't be included in output.<p>
     * 
     * SINGLE by default. Change applies to the next output quad and remains 
     * in effect for all subsequent quads until changed.
     */
    public final void setTextureDepth(TextureDepth textureDepth) {
        
    }
    
    public final void position(int vertexIndex, float x, float y, float z) {
        
    }
    
    public final void normal(int vertexIndex, float x, float y, float z) {
        
    }
    
    public final void uv(int vertexIndex, TextureDepth textureDepth, float u, float v) {
        
    }
    
    public final void color(int vertexIndex, TextureDepth textureDepth, int colorRGBA) {
        
    }
   
    /**
     * Set true if your model/quad may need color tinting based on Biome, etc. Applied
     * per layer.  Default is false for all layers.<p> 
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableBlockColor(TextureDepth textureLayer, boolean enable) {
        
    }
    
    /**
     * Use this to control lighting of the texture in the given layer.
     * If enabled, the vertex light set by {@link #setVertexLight(int)} will be added
     * to world light for this texture layer. This result in full emissive rendering if 
     * vertex light is set to white (the default.)  However, more subtle lighting effects
     * are possible with other lightmap colors.<p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableVertexLight(TextureDepth textureLayer, boolean enable) {
        
    }
    
    /**
     * If enabled, textures lit by world light will have diffuse shading applied 
     * based on face/vertex normals.  In the standard lighting model shading is 
     * arbitrary (doesn't reflect movement of the sun, for example) but enhanced lighting
     * models may shade differently.
     * 
     * Enabled by default. Rarely disabled in world lighting but may be useful for some cutout
     * textures (vines, for example) that are pre-shaded or which render poorly with shading.<p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableWorldLightDiffuse(boolean enable) {
        
    }
    
    /**
     * Works the same as {@link #enableWorldLightDiffuse(boolean)}, but applies
     * to surfaces lit by the provided vertex light color. (See {@link #enableVertexLight(TextureDepth, boolean)}.)<p>
     * 
     * Disabled by default.  Most textures with vertex lighting will be fully emissive
     * and it will not make sense to shade them. But this could be useful for partially 
     * illuminated surfaces. <p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableVertexLightDiffuse(boolean enable) {
        
    }
    
    /**
     * If enabled, textures lit by world light will have ambient occlusion applied. 
     * 
     * Enabled by default and changes are rarely needed in world lighting.<p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableWorldLightAmbientOcclusion(boolean enable) {
        
    }
    
    /**
     * Works the same as {@link #enableWorldLightAmbientOcclusion(boolean)}, but applies
     * to surfaces lit by the provided vertex light color. (See {@link #enableVertexLight(TextureDepth, boolean)}.)<p>
     * 
     * Disabled by default.  Most textures with vertex lighting will be fully emissive
     * and it will not make sense to shade them. But this could be useful for partially 
     * illuminated surfaces. <p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableVertexLightAmbientOcclusion(boolean enable) {
        
    }
    
    /**
     * Accepts a light value in the form of an RGB color value to be used as the minimum block light
     * for the next <em>vertex</em>. The light will always be additive with other light sources.
     * Vertex lighting will be interpolated like any other vertex value.<p>
     * 
     * In the standard lighting model, this is converted into a monochromatic value based on luminance.
     * 
     * Vertex light is always <em>additive</em>.  For example, if you provide a dim blue vertex light and
     * your surface is next to a torch, your surface will render with mostly torch light (with flicker) but with a
     * boosted blue component. (Clamped to full white brightness.)  In direct sunlight dim vertex light probably
     * won't be noticeable.<p>
     * 
     * Will be full brightness (0xFFFFFF) by default.<br>
     * Changes apply to all subsequent vertices until changed.<p>
     */
    public final void setVertexLight(int lightRGB) {
        
    }
    
    /**
     * Version of {@link #setEmissiveLightMap(int)} that accepts unpacked int components.
     */
    public final void setVertexLight(int red, int green, int blue) {
        
    }
    
    /**
     * Version of {@link #setEmissiveLightMap(int, int, int)} that accepts unpacked float components.
     */
    public final void setVertexLight(float red, float green, float blue) {
        
    }
    
    /**
     * Returns all settings to default values.
     */
    public final void clearSettings() {
        
    }
    
    /**
     * Returns the minimum array size to store this quad. Useful 
     * for {@link #outputBakedQuadData(int[], int)}
     */
    public final int intSize() {
        return 0;
    }
    
    /**
     * Generates baked quad data in the provided array starting at the given index.<p>
     * 
     * Returns the number of integers written to the array for benefit of implementations
     * that are packing data for multiple quads into a single array.<p>
     * 
     * Will throw an exception if the array is too small. Use {@link #intSize()} to check.
     */
    public final int outputBakedQuadData(int [] target, int start) {
        
        // Will need to do the stuff below, with modifications and additions for the expanded feature set
        return 0;
  //    
//      public BakedQuad bake(Vector3f vector3f_1, Vector3f vector3f_2, ModelElementFace modelElementFace_1, Sprite sprite_1, Direction direction_1, ModelRotationContainer modelRotationContainer_1, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation_1, boolean boolean_1) {
//          ModelElementTexture modelElementTexture_1 = modelElementFace_1.textureData;
//          if (modelRotationContainer_1.isUvLocked()) {
//             modelElementTexture_1 = this.method_3454(modelElementFace_1.textureData, direction_1, modelRotationContainer_1.getRotation());
//          }
  //
//          float[] floats_1 = new float[modelElementTexture_1.uvs.length];
//          System.arraycopy(modelElementTexture_1.uvs, 0, floats_1, 0, floats_1.length);
//          float float_1 = (float)sprite_1.getWidth() / (sprite_1.getMaxU() - sprite_1.getMinU());
//          float float_2 = (float)sprite_1.getHeight() / (sprite_1.getMaxV() - sprite_1.getMinV());
//          float float_3 = 4.0F / Math.max(float_2, float_1);
//          float float_4 = (modelElementTexture_1.uvs[0] + modelElementTexture_1.uvs[0] + modelElementTexture_1.uvs[2] + modelElementTexture_1.uvs[2]) / 4.0F;
//          float float_5 = (modelElementTexture_1.uvs[1] + modelElementTexture_1.uvs[1] + modelElementTexture_1.uvs[3] + modelElementTexture_1.uvs[3]) / 4.0F;
//          modelElementTexture_1.uvs[0] = MathHelper.lerp(float_3, modelElementTexture_1.uvs[0], float_4);
//          modelElementTexture_1.uvs[2] = MathHelper.lerp(float_3, modelElementTexture_1.uvs[2], float_4);
//          modelElementTexture_1.uvs[1] = MathHelper.lerp(float_3, modelElementTexture_1.uvs[1], float_5);
//          modelElementTexture_1.uvs[3] = MathHelper.lerp(float_3, modelElementTexture_1.uvs[3], float_5);
//          int[] ints_1 = this.method_3458(modelElementTexture_1, sprite_1, direction_1, this.method_3459(vector3f_1, vector3f_2), modelRotationContainer_1.getRotation(), modelRotation_1, boolean_1);
//          Direction direction_2 = method_3467(ints_1);
//          System.arraycopy(floats_1, 0, modelElementTexture_1.uvs, 0, floats_1.length);
//          if (modelRotation_1 == null) {
//             this.method_3462(ints_1, direction_2);
//          }
  //
//          return new BakedQuad(ints_1, modelElementFace_1.tintIndex, direction_2, sprite_1);
//       }
    }

    /**
     * Use when you know you are keeping a reference to the 
     * baked vertex data and aren't trying to avoid allocation overhead.<p>
     * 
     * Models that do dynamic baking should use {@link #outputBakedQuadData(int[], int)}
     * and manage their own allocations.
     */
    public final int[] outputBakedQuadData() {
        int[] result = new int[intSize()];
        outputBakedQuadData(result, 0);
        return result;
    }

    /**
     * Retrieves face direction from a baked quad. Returns null for quads not coplanar with a block face.
     */
    public static Direction getBlockFace(int[] vertexData, int index) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns bit flags position-aligned to BlockRenderLayer ordinals indicating
     * which block render layers are present in the given baked quad.
     */
    public static int getExtantLayers(int[] vertexData, int index) {
        // TODO Auto-generated method stub
        return 0;
    }
}
