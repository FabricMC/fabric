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

/**
 * Finds standard {@link RenderMaterial} instances used to communicate
 * quad rendering characteristics to a {@link RenderContext}.<p>
 *
 * Must be obtained via {@link Renderer#materialFinder()}.
 */
public interface MaterialFinder {
    /**
     * Returns the standard material encoding all
     * of the current settings in this finder. The settings in
     * this finder are not changed.<p>
     * 
     * Resulting instances can and should be re-used to prevent
     * needless memory allocation. {@link Renderer} implementations
     * may or may not cache standard material instances.
     */
    RenderMaterial find();
    
    /**
     * When > 1, {@link QuadMaker} will accept additional
     * color and UV coordinates for each vertex. Standard materials
     * support up to three texture layers.<p>
     * 
     * Additional layers are useful for overlay textures -
     * borders, decals, patterns, machine status, etc.
     * Specifying overlay textures as part of the same quad
     * can enable the {@link Renderer} to optimize memory
     * usage and texture blending, depending on implementation.<p>
     * 
     * Extra color/UV coordinates can also be re-purposed for
     * customer shaders that require expanded vertex attributes.
     */
    MaterialFinder textureDepth(int depth);

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
    MaterialFinder blendMode(int layerIndex, BlockRenderLayer blendMode);

    /**
     * Vertex color(s) will be modified for quad color index unless disabled.<p>
     * 
     * This is useful when there are multiple texture layers and only some of 
     * them should have color index applied. If there is only layer or all
     * layers are disabled, it is simpler to disable the color index itself
     * by sending a colorIndex value of -1 to the {@link RenderContext}.
     */
    MaterialFinder disableColorIndex(int layerIndex, boolean disable);
    
    /**
     * Vertex color(s) will be modified for diffuse shading unless disabled.
     */
    MaterialFinder disableDiffuse(int layerIndex, boolean disable);
    
    /**
     * Vertex color(s) will be modified for ambient occlusion unless disabled.
     */
    MaterialFinder disableAo(int layerIndex, boolean disable);
    
    /**
     * When true, brightness value provided via {@link VertexEditor#lightmap()}
     * will be used as the minimum lightmap brightness.  Usually this is used to 
     * implement full brightness but less-than-full brightness values are valid.
     * False by default<p>
     * 
     * Note that color will still be modified by diffuse shading and ambient occlusion,
     * by default.  Most of the time, you will want to disable those via {@link #disableAo(int, boolean)}
     * and {@link #disableDiffuse(int, boolean)}.
     */
    MaterialFinder emissive(int layerIndex, boolean isEmissive);
}
