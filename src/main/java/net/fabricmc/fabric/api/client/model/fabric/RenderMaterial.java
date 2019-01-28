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
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;

/**
 * All model quads have an associated render material governing
 * how the quad will be rendered.<p>
 * 
 * A material instance is always immutable and thread-safe.  References to a material 
 * remain valid until the end of the current game session.<p>
 * 
 * A {@link RenderMaterial} instance can only be created by a {@link MaterialFinder}
 * instance obtained via {@link Renderer#materialFinder()}.<p>
 * 
 * Materials can be registered and shared between mods using {@link Renderer#registerMaterial(net.minecraft.util.Identifier, RenderMaterial)}.
 * The registering mod is responsible for creating each registered material at startup.<p>
 * 
 * Materials are not required to know their registration identity, and two materials
 * with the same attributes may or may not satisfy equality and identity tests. Model 
 * implementations should never attempt to analyze materials or implement control logic based on them.
 * They are only tokens for communicating quad attributes to the ModelRenderer.<p>
 * 
 * There are three classes of materials... <p>
 * 
 * <b>STANDARD MATERIALS</b><p>
 * 
 * Standard materials have "normal" rendering with options for additional
 * texture layers (up to three, total) and per-layer control over lighting,
 * color, and texture blending. In the default renderer, "normal" rendering
 * emulates unmodified Minecraft. Other renderers may offer a different aesthetic.<p>
 * 
 * The number of standard materials is finite, but not small.  With the various options, 
 * as of this writing there are 266,304 possible configurations for a standard material.
 * To find a standard material, use {@link Renderer#materialFinder()}.<p>
 * 
 * All renderer implementations should support standard materials.<p>
 * 
 * <b>SHADER MATERIALS</b><p>
 * 
 * Shader materials are standard materials with a vertex and fragment shader attached.
 * The quad attributes for standard materials have standard vertex attribute bindings
 * for cross-compatibility of shaders. Vertex colors will be modified (lit) by the 
 * renderer before the vertex shader runs, unless lighting (AO and diffuse) has been
 * disabled in the standard material.<p>
 * 
 * Shader materials do not have to implement any sort of "standard" render, and the quad
 * vertex attributes can be re-purposed to fit the needs of the shader author.<p>
 * 
 * Shader materials are an optional {@link Renderer} feature.
 * {@link Renderer#shaderManager()} will return null if shaders are unsupported.
 * 
 * <b>SPECIAL MATERIALS</b><p>
 * 
 * Special materials are implemented directly by the Renderer implementation, typically
 * with the aim of providing advanced/extended features. Such materials may offer additional
 * vertex attributes via extensions to {@link MeshBuilder} and {@link QuadMaker}.<p>
 * 
 * Special materials can be obtained using {@link Renderer#materialById(Identifier)}
 * with a known identifier. Renderers may provided other means of access. Popular 
 * special materials could be implemented by multiple renderers, however there is 
 * no requirement that special materials be cross-compatibile.
 */
public interface RenderMaterial {
    /**
     * This will be identical to the material that would be obtained by calling {@link MaterialFinder#find()}
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
     * How many color/uv layers are in the material. Minimum is 1.
     * See {@link MaterialFinder#setTextureDepth(int)}
     */
    int textureDepth();
}
