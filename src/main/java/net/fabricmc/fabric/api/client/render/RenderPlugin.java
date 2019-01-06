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

import net.fabricmc.fabric.api.client.model.FabricBakedQuad;
import net.minecraft.util.Identifier;

/**
 * Interface for rendering plug-ins that provide enhanced capabilities
 * for model lighting, buffering and rendering.  All plug-ins should
 * minimally accept block and item models using standard vertex formats. <p>
 * 
 * The currently installed and active render plug-in, if present, can be 
 * retrieved via {@link RenderConfiguration#getActiveRenderPlugin()}.<p>
 * 
 * Sub-types of RenderPlugin that support additional capabilities, including
 * shaders and uniforms are to be defined outside the scope of the core Fabric API.
 */
public interface RenderPlugin {

    boolean isSupportedForModelInput(FabricVertexFormat format);
    
    boolean isSupportedForStandardRender(FabricVertexFormat format);
    
    boolean isSupportedForShaders(FabricVertexFormat format);
    
    boolean isTranslationSupported(FabricVertexFormat modelFormat, FabricVertexFormat shaderFormat);
    
    default int maxTextureDepth() {
        return 1;
    }
    
    /**
     * Identifies the set of feature flag supported by this plug-in.
     * Intended to support and encourage adoption of shared feature sets.<p>
     * 
     * @see {@link RenderPlugin#supportedFeatureFlags()}, {@link FabricBakedQuad#getFeatureFlags()}
     */
    default Identifier featureSetId() {
        return null;
    }
    
    /**
     * Identifies which options in the current feature set are supported by this plug-in.
     * Plug-ins are not required to implement all features declared in a feature set.<p>
     * 
     * @see {@link RenderPlugin#featureSetId()}, {@link FabricBakedQuad#getFeatureFlags()}
     */
    default int supportedFeatureFlags() {
        return 0;
    }
}
