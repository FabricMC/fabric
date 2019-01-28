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

import net.fabricmc.fabric.impl.client.render.RendererAccessImpl;

/**
 * Registration and access for rendering extensions.
 */
public interface RendererAccess {
    RendererAccess INSTANCE = RendererAccessImpl.INSTANCE;

    /**
     * Rendering extension mods must implement {@link Renderer} and 
     * call this method during initialization.<p>
     * 
     * Only one {@link Renderer} plugin can be active in any game instance.
     * If a second mod attempts to register this method will fail softly, 
     * to allow for mods that implement other features in addition to being a 
     * render plug-in. However, this is not recommended. {@link Renderer} mods should do one thing.<p>
     */
    void registerModelRenderer(Renderer plugin);

    /**
     * Access to the current {@link Renderer} for creating and retrieving model builders 
     * and materials. Will return null if no render plug in is active.
     */
    Renderer getActiveRenderer();

    /**
     * Performant test for {@link #getActiveRenderer()} != null;
     */
    boolean isRendererActive();
}
