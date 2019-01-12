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

import net.fabricmc.fabric.impl.client.render.ModelRendererAccessImpl;

/**
 * Registration and access for rendering extensions.
 */
public interface ModelRendererAccess {
    ModelRendererAccess INSTANCE = ModelRendererAccessImpl.INSTANCE;

    /**
     * Rendering extension mods must implement {@link ModelRenderer} and 
     * call this method during initialization.<p>
     * 
     * Only one render plugin can be active in any game instance.
     * If a second mod attempts to register as the render plug in
     * this method will fail softly, to allow for mods that implement
     * other features in addition to being a render plug-in. However,
     * this is not recommended. Plug-in mods should do one thing.<p>
     * 
     * @see {@link ModelRenderer}
     */
    void registerRenderPlugIn(ModelRenderer plugin);

    /**
     * Access to render plug-in for creating and retrieving shaders and uniforms.
     * Plug-ins are not required to support those features.
     * Will return null if no render plug in is active.
     */
    ModelRenderer getActiveRenderer();

    /**
     * Performant test for {@link #getActiveRenderPlugin()} != null;
     */
    boolean isRendererActive();
}
