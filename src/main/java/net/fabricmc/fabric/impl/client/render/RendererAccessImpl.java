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

package net.fabricmc.fabric.impl.client.render;

import net.fabricmc.fabric.api.client.model.fabric.RendererAccess;
import net.fabricmc.fabric.api.client.model.fabric.Renderer;

public final class RendererAccessImpl implements RendererAccess{
    public static final RendererAccessImpl INSTANCE = new RendererAccessImpl();

    // private constructor
    private RendererAccessImpl() { };

    @Override
    public final void registerRenderer(Renderer renderer) {
        if(renderer != null && activeRenderer == null) {
            activeRenderer = renderer;
            hasActiveRenderer = true;
        }
    }

    private Renderer activeRenderer = null;

    /** avoids null test every call to {@link #hasRenderer()} */
    private boolean hasActiveRenderer = false;

    @Override
    public final Renderer getRenderer() {
        return activeRenderer;
    }

    @Override
    public final boolean hasRenderer() {
        return hasActiveRenderer;
    }
}
