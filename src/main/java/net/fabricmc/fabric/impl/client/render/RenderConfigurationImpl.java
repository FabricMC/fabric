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

import net.fabricmc.fabric.api.client.render.RenderPlugin;
import net.fabricmc.fabric.api.client.render.RenderConfiguration;

public final class RenderConfigurationImpl implements RenderConfiguration{
    public static final RenderConfigurationImpl INSTANCE = new RenderConfigurationImpl();
    
    // private constructor
    private RenderConfigurationImpl() { };
    
    @Override
    public final void registerRenderPlugIn(RenderPlugin plugin) {
        if(plugin != null && activePlugIn == null) {
            activePlugIn = plugin;
            hasActivePlugIn = true;
        }
    }
    
    private RenderPlugin activePlugIn = null;
    
    /** avoids null test every call to {@link #isRenderPluginActive()} */
    private boolean hasActivePlugIn = false;
    
    @Override
    public final RenderPlugin getActiveRenderPlugin() {
        return activePlugIn;
    }
    
    @Override
    public final boolean isRenderPluginActive() {
        return hasActivePlugIn;
    }
}
