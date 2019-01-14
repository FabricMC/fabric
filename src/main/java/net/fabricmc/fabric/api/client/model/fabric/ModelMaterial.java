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
 * They are only tokens for communicating quad attributes to the ModelRenderer.
 */
public interface ModelMaterial {
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
