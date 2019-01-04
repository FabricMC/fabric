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
 * Specialized block view access for vertex lighters. 
 * Implementation exploits render chunk cache for performance.
 * 
 * TODO: WIP - extend block view or just implement needed subset?
 */
public interface LighterBlockView {
    
    /**
     * For plants or other blocks with randomized location.
     * Lighter is responsible for transforming vertex positions before buffering.
     */
    float offsetX();
    float offsetY();
    float offsetZ();
}
