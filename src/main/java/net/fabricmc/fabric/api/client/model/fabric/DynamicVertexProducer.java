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

/**
 * Returned by {@link DynamicRenderBlockEntity} to re-buffer vertex data
 * at render time. Must be safe to call from non-main thread and instance
 * returned must contain any model state it needs to render.
 * Renderer will not retain a reference - valid to reuse instances.
 */
@FunctionalInterface
public interface DynamicVertexProducer {
    void produceVertexData(DynamicVertexConsumer consumer);
}
