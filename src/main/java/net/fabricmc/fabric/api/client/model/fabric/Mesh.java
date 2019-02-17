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

/**
 * A bundle of one or more {@link Quad} instances encoded by the renderer,
 * typically via {@link Renderer#meshBuilder()}.<p>
 * 
 * Similar in purpose to the List<BakedQuad> instances returned by BakedModel, but 
 * affords the renderer the ability to optimize the format for performance
 * and memory allocation.
 */
public interface Mesh {
    /**
     * Use to access all of the quads encoded in this mesh. The quad instances
     * sent to the consumer will likely be threadlocal/reused and should never
     * be retained by the consumer.
     */
    public void forEach(Consumer<Quad> consumer);
}
