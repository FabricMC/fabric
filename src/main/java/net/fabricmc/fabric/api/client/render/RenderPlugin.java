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

/**
 * Sub-types of RenderPlugin that support shaders and uniforms are
 * to be defined outside the scope of the core Fabric API.  Plug-ins are
 * not required to implement such extensions.
 */
public interface RenderPlugin {

    default int maxTextureDepth() {
        return 1;
    }
    
    default int supportedLightingFlags() {
        return 0;
    }
}
