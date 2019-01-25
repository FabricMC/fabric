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

import java.util.function.Consumer;

import net.minecraft.client.render.model.BakedModel;

/**
 * This defines the object made available to models for buffering vertex data at render time.<p>
 *
 * A future enhancement will offer a quad transformer (for animations) and a dynamic
 * quad builder for per-frame renders.
 */
public interface ModelRenderContext {
    /**
     * Used by models to send vertex data previously baked 
     * via {@link QuadPackager}. The fastest option and preferred whenever feasible.
     */
    Consumer<PackagedQuads> packagedQuadConsumer();
    
    /**
     * Fabric causes vanilla baked models to send themselves 
     * via this interface. Can also be used by compound models that contain a mix
     * of vanilla baked models, packaged quads and/or dynamic elements.
     */
    Consumer<BakedModel> fallbackModelConsumer();
}
