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
import net.minecraft.util.Identifier;

/**
 * A reference to a model material that specifies texture blending,
 * lighting, coloring and other aspects of quad rendering. Every
 * quad sent to a {@link RenderContext} is associated with a material.<p>
 * 
 * A material instance is always immutable and thread-safe.  References to a material 
 * remain valid until the end of the current game session.<p>
 * 
 * A {@link RenderMaterial} instance can only be created by a {@link MaterialFinder}
 * instance obtained via {@link Renderer#getMaterialBuilder()}.<p>
 * 
 * Materials can be registered and shared between mods using {@link Renderer#getMaterial(net.minecraft.util.Identifier)}
 * and {@link Renderer#registerMaterial(net.minecraft.util.Identifier, RenderMaterial)}.
 * The registering mod is responsible for creating each registered material at startup.<p>
 * 
 * Materials are not required to know their registration identity, and two materials
 * with the same attributes may or may not satisfy equality and identity tests. Model 
 * implementations should never attempt to analyze materials or implement control logic based on them.
 * They are only tokens for communicating quad attributes to the ModelRenderer.<p>
 * 
 * <b>STANDARD MATERIALS</b><p>
 * 
 * All {@link Renderer} implementations must pre-register the water and lava materials
 * using the identifiers defined below.  This gives models access to any fancy renders that may
 * be implemented for those materials.<p>
 */
public interface RenderMaterial {
    
    /**
     * This will be identical to the material that would be obtained by calling {@link MaterialFinder#build()}
     * on a new, unaltered, {@link MaterialFinder} instance.  It is defined here for clarity and convenience.
     * 
     * Quads using this material have a single texture layer, use {@link Block#getRenderLayer()} of the associated
     * block to determine texture blending (or translucent for item models),  honor block color index, 
     * are non-emissive, and have {@link ShadingMode} inferred from {@link BakedModel#useAmbientOcclusion()} 
     * and block light level.  All standard, non-fluid baked models are rendered using this material.<p>
     * 
     * {@link Renderer} implementations may or may not define and use more explicit materials for the 
     * various context-dependent combinations of shading and render layer and those are not defined or exposed.
     */
    Identifier MATERIAL_STANDARD = new Identifier("minecraft", "standard");
    
    /**
     * Transient unique identifier for this material.  Can be used to serialize a 
     * material in a primitive type (typically int[]).  Materials can be retrieved
     * via {@link Renderer#getMaterial(int)}.<p>.
     * 
     * Index values are only valid in the current game session.  They should never be
     * used in world saves. For that use case, rely on materials registered via 
     * {@link Renderer#registerMaterial(net.minecraft.util.Identifier, RenderMaterial)}
     * and serialize the registration identifier.
     */
    int index();
    
    /**
     * How many color/uv layers are in the material. Minimum is 1.
     * See {@link MaterialFinder#setTextureDepth(int)}
     */
    int textureDepth();

    /**
     * Texture blending mode renderer should use for the given layer.
     * See {@link MaterialFinder#setBlendMode(BlockRenderLayer)}
     */
    BlockRenderLayer blendMode(int layerIndex);

    /**
     * Vertex color(s) will be modified for diffuse shading unless disabled.
     */
    boolean disableDiffuse(int layerIndex);
    
    /**
     * Vertex color(s) will be modified for ambient occlusion unless disabled.
     */
    boolean disableAo(int layerIndex);

    /**
     * If true, renderer applies custom brightness to the given layer.
     * See {@link MaterialFinder#setEmissive(boolean)}
     */
    boolean isEmissive(int layerIndex);

    /**
     * If true, color index is not applied to the given layer.
     * See {@link MaterialFinder#enableColorIndex(int, boolean)}.
     */
    boolean disableColorIndex(int layerIndex);
}
