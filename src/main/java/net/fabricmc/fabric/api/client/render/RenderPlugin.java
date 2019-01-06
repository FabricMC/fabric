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
import net.fabricmc.fabric.api.client.model.FabricBakedQuadProducer;
import net.fabricmc.fabric.api.client.model.FastRenderableBlockEntity;
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

    /**
     * True if this plug-in implements the feature specified by {@link FastRenderableBlockEntity}
     */
    boolean isFastBlockEntityRenderSupported();

    /**
     * True if the given format can be accepted by this plug-in as output from
     * {@link FabricBakedQuadProducer#produceFabricBakedQuads()}.<p>
     * 
     * All plug-ins <em>must</em> support at least the three standard vertex formats
     * defined in {@link FabricVertexFormat}.
     */
    boolean isSupportedForModelInput(FabricVertexFormat format);
    
    /**
     * True if the given vertex format can be rendered using the standard lighting
     * and coloration logic implemented by this plug-in. This check refers to the model vertex 
     * format output in {@link FabricBakedQuadProducer#produceFabricBakedQuads()}.<p>
     * 
     * If true, it implies that {@link #isSupportedForModelInput(FabricVertexFormat)} is also true.
     * However, some model vertex format can be input but not rendered.  This is typically the
     * case only for specialized vertex formats meant for used by custom shaders.<p>
     * 
     * If the format is not something the plug-in can render directly from a vertex
     * buffer, a true result implies the plug-in will automatically handle any necessary
     * translation.  There is no equivalent to {@link #isTranslationSupported()} for standard renders.
     */
    boolean isSupportedForStandardRender(FabricVertexFormat format);
    
    /**
     * True if the given vertex format can be made available to custom shaders by this plug-in.
     * Will always be false for plug-ins that do not support shaders. A true result does not
     * imply the format can be loaded from a model, but in that case the format will only be
     * available to shaders if the plug-in supports the necessary format translation.
     */
    default boolean isSupportedForShaders(FabricVertexFormat format) {
        return false;
    }
    
    /**
     * True if the plug-in is able to accept the modelFormat as input and make it available
     * to a custom shader in the given shader format. 
     */
    boolean isTranslationSupported(FabricVertexFormat modelFormat, FabricVertexFormat shaderFormat);
    
    /**
     * The maximum number of texture layers supported by this plug-in for multi-texture rendering.
     * Must be at least 1. Plug-ins are not required to support this feature.<p>
     * 
     * The Minecraft light-map texture is, strictly speaking, a form of multi-texture rendering, but
     * this result only refers to additional UV texture coordinates that can be provided from models.
     */
    default int maxTextureDepth() {
        return 1;
    }
    
    /**
     * Identifies the set of feature flags supported by this plug-in. The use of
     * a name-spaced identifier is meant to encourage adoption of shared feature sets.<p>
     * 
     * A null result implies that no additional features are supported.<p>
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
