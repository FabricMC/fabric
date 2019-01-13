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

import net.fabricmc.fabric.api.client.model.fabric.ModelRendererAccess;
import net.fabricmc.fabric.api.client.model.fabric.ModelRenderer;

public final class ModelRendererAccessImpl implements ModelRendererAccess{
    public static final ModelRendererAccessImpl INSTANCE = new ModelRendererAccessImpl();

    // private constructor
    private ModelRendererAccessImpl() { };

    @Override
    public final void registerModelRenderer(ModelRenderer renderer) {
        if(renderer != null && activeRenderer == null) {
            activeRenderer = renderer;
            hasActiveRenderer = true;
        }
    }

    private ModelRenderer activeRenderer = null;

    /** avoids null test every call to {@link #isRendererActive()} */
    private boolean hasActiveRenderer = false;

    @Override
    public final ModelRenderer getActiveRenderer() {
        return activeRenderer;
    }

    @Override
    public final boolean isRendererActive() {
        return hasActiveRenderer;
    }
}
