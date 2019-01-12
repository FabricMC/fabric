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

import net.minecraft.util.Identifier;

/**
 * Interface for rendering plug-ins that provide enhanced capabilities
 * for model lighting, buffering and rendering.  All plug-ins must
 * minimally accept the three standard vertex formats defined in {@link FabricVertexFormat}.<p>
 * 
 * The currently installed and active render plug-in, if present, can be 
 * retrieved via {@link ModelRendererAccess#getActiveRenderPlugin()}.<p>
 * 
 * Sub-types of ModelRenderer that support additional capabilities, including
 * shaders and uniforms are to be defined outside the scope of the core Fabric API.
 */
public interface ModelRenderer {
    ModelBuilder getModelBuilder();
    
    ModelMaterialBuilder getMaterialBuilder();

    ModelMaterial getMaterial(Identifier id);
    
    boolean registerMaterial(Identifier id, ModelMaterial material);
}
