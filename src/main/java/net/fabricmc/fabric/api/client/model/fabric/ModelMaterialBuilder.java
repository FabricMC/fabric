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

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.model.BakedModel;

/**
 * Creates a {@link ModelMaterial} instance used to communicate
 * quad rendering characteristics to a {@link DynamicVertexConsumer}.<p>
 *
 * Must be obtained via {@link ModelRenderer#getMaterialBuilder()}.<p>
 * 
 * Implementations or future Fabric API extensions may add attributes
 * for shaders or other features that modify quad rendering.
 */
public interface ModelMaterialBuilder {
    /**
     * Create a new {@link ModelMaterial} instance encoding all
     * of the current settings in this builder. The settings in
     * this builder are not changed.<p>
     * 
     * Resulting instances can and should be re-used to prevent
     * needless memory allocation. ModelRenderer implementations
     * may or may not cache material construction.
     */
    ModelMaterial build();
    
    /**
     * When > 1, ModelVertexConsumer will accept additional
     * color and UV coordinates for each vertex. {@link ModelRenderer}
     * implementations should support at least three texture layers.
     * The default, minimum value is 1.<p>
     * 
     * Additional layers are useful for overlay textures -
     * borders, decals, patterns, machine status, etc.
     * Specifying overlay textures as part of the same quad
     * can enable the {@link ModelRenderer} to optimize memory
     * usage and texture blending, depending on implementation.
     */
    void setTextureDepth(int depth);

    /**
     * Defines how texture pixels will be blended with the scene.
     * Accepts {link @BlockRenderLayer} values and blending behavior
     * will emulate the way that Minecraft renders each pass. But this does 
     * NOT mean the texture will be rendered in a specific render pass - some
     * implementations may not use the standard Minecraft render passes.<p>
     * 
     * CAN be null and is null by default. A null value means the renderer
     * will use {@link Block#getRenderLayer()} for the associate block, or
     * {@link BlockRenderLayer#TRANSLUCENT} for item renders. (Normal Minecraft rendering)
     */
    default void setBlendMode(BlockRenderLayer blendMode) {
        setBlendMode(0, blendMode);
    }
    
    /**
     * Sets blend mode for a specific texture layer. Useful when texture depth is > 1.
     */
    void setBlendMode(int layerIndex, BlockRenderLayer blendMode);

    /**
     * Enables or disables application of quad color index to the texture in 
     * a given layer.  Enabled by default.<p>
     * 
     * This is useful when there are multiple texture layers and only some of 
     * them should have color index applied. If there is only layer or all
     * layers are disabled, it is simpler to disable the color index itself
     * by sending a colorIndex value of -1 to the {@link DynamicVertexConsumer}.
     */
    void enableColorIndex(int layerIndex, boolean isEnabled);
    
    /**
     * Specifies if and how pixel color should be modified by diffuse 
     * shading and ambient occlusion. See {@link ShadingMode}.<p>
     * 
     * ShadingMode CAN be null and is null by default.  A null value
     * means the shading mode should be inferred from {@link BakedModel#useAmbientOcclusion()}
     * and block light level. (Normal Minecraft rendering)
     */
    default void setShading(ShadingMode shading) {
        setShading(0, shading);
    }
    
    /**
     * Sets shading mode for a specific texture layer. Useful when texture depth is > 1.
     */
    void setShading(int layerIndex, ShadingMode shading);
    
    /**
     * When true, brightness value provided via {@link DynamicVertexConsumer#brightness(int)}
     * will be used as the minimum lightmap brightness.  Usually this is used to 
     * implement full brightness but less-than-full brightness values are valid.<p>
     * 
     * Note that color will still be modified by diffuse shading and ambient occlusion,
     * by default.  Most of the time, you will want to disable those via {@link #setShading(ShadingMode)}.
     */
    default void setEmissive(boolean isEmissive) {
        setEmissive(0, isEmissive);
    }
    
    /**
     * Controls application of custom brightness for a specific texture layer. Useful when texture depth is > 1.
     */
    void setEmissive(int layerIndex, boolean isEmissive);
}
