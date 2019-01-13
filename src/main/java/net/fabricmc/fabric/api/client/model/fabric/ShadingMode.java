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
 * Options to control application of shading to models lit by the
 * active {@link ModelRenderer} as part of a {@link ModelMaterial} specification.
 */
public enum ShadingMode {
    /**
     * Vertex color(s) will be modified (reduced) for both diffuse shading
     * and ambient occlusion.  This is the default for normal solid blocks.
     */
    SHADED,
    
    /**
     * Vertex color(s) will be modified (reduced) for ambient occlusion but
     * not for diffuse shading.  Potentially useful for models with pre-shaded
     * vertex colors based on geometry that still require in-world smooth
     * lighting adjustment via AO (if enabled.)
     * 
     * Use of pre-shaded vertex colors may circumvent some aspects of non-standard 
     * lighting models implemented by the future {@link ModelRenderer}s and
     * may lead to inconsistent or unattractive lighting when a more 
     * sophisticated lighting model is in use.
     */
    AMBIENT_OCCLUSION_ONLY,
    
    /**
     * Vertex color(s) will be modified (reduced) for diffuse shading but
     * not for ambient occlusion.  Potentially useful for models with 
     * partially emissive surfaces that benefit from the visual definition 
     * afforded by diffuse shading but not necessarily from the potentially
     * more variable effects of ambient occlusion.
     */
    DIFFUSE_ONLY,
    
    /**
     * Vertex color(s) is unmodified except for world brightness, which can
     * also be overridden by enabling emissive rendering and providing
     * a per-vertex brightness.
     */
    FLAT
}
