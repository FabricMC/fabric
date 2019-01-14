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
 * A reference to a model material that specifies texture blending,
 * lighting, coloring and other aspects of quad rendering. Every
 * quad sent to a {@link ModelVertexConsumer} is associated with a material.<p>
 * 
 * A material instance is always immutable and thread-safe.  References to a material 
 * remain valid until the end of the current game session.<p>
 * 
 * This interface has no methods because the internal state is for use only
 * by the {@link ModelRenderer} and the implementation is meant to be
 * optimized according to how the renderer manages vertex buffers and draw calls.<p>
 * 
 * A {@link ModelMaterial} instance can only be created by a {@link ModelMaterialBuilder}
 * instance obtained via {@link ModelRenderer#getMaterialBuilder()}.<p>
 * 
 * Materials can be registered and shared between mods using {@link ModelRenderer#getMaterial(net.minecraft.util.Identifier)}
 * and {@link ModelRenderer#registerMaterial(net.minecraft.util.Identifier, ModelMaterial)}.
 * The registering mod is responsible for creating each registered material at startup.<p>
 * 
 * Materials are not required to know their registration identity, and two materials
 * with the same attributes may or may not satisfy equality and identity tests. Model 
 * implementations should never attempt to analyze materials or implement control logic based on them.
 * They are only tokens for communicating quad attributes to the ModelRenderer.<p>
 * 
 * <b>STANDARD MATERIALS</b><p>
 * 
 * All {@link ModelRenderer} implementations must pre-register the water and lava materials
 * using the identifiers defined below.  This gives models access to any fancy renders that may
 * be implemented for those materials.<p>
 */
public interface ModelMaterial {
    
    /**
     * This will be identical to the material that would be obtained by calling {@link ModelMaterialBuilder#build()}
     * on a new, unaltered, {@link ModelMaterialBuilder} instance.  It is defined here for clarity and convenience.
     * 
     * Quads using this material have a single texture layer, use {@link Block#getRenderLayer()} of the associated
     * block to determine texture blending (or translucent for item models),  honor block color index, 
     * are non-emissive, and have {@link ShadingMode} inferred from {@link BakedModel#useAmbientOcclusion()} 
     * and block light level.  All standard, non-fluid baked models are rendered using this material.<p>
     * 
     * {@link ModelRenderer} implementations may or may not define and use more explicit materials for the 
     * various context-dependent combinations of shading and render layer and those are not defined or exposed.
     */
    Identifier MATERIAL_STANDARD = new Identifier("minecraft", "standard");
    
    /**
     * Material that is used for water rendering.  Useful if the model renderer implements
     * some kind of fancy render for water and you want a model to use the same effect.
     */
    Identifier MATERIAL_WATER = new Identifier("minecraft", "water");
    
    /**
     * Material that is used for lava rendering.  Useful if the model renderer implements
     * some kind of fancy render for lava and you want a model to use the same effect.
     */
    Identifier MATERIAL_LAVA = new Identifier("minecraft", "lava");
    
    /**
     * The number of integers needed to represent a single quad with this materials.
     * For use by models to allocate vertex storage and transfer vertex data.
     */
    int quadStride();
    
    /**
     * Transient unique identifier for this material.  Can be used to serialize a 
     * material in a primitive type (typically int[]).  Materials can be retrieved
     * via {@link ModelRenderer#getMaterial(int)}.<p>.
     * 
     * Index values are only valid in the current game session.  They should never be
     * used in world saves. For that use case, rely on materials registered via 
     * {@link ModelRenderer#registerMaterial(net.minecraft.util.Identifier, ModelMaterial)}
     * and serialize the registration identifier.
     */
    int index();
}
