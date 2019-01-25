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
 * This is the consumer made available to models for buffering vertex data at render time.
 * Is the union of three different interfaces:<p>
 * <li>{@link FastVertexConsumer} Used by models to send vertex data previously baked 
 * via {@link FastVertexBuilder}. The fastest option and preferred whenever feasible.</li><p>
 * 
 * <li>{@link VertexBuilder} For models that need to generate vertex data on the fly.
 * Should be used sparingly - only for model components that can't be pre-baked.</li><p>
 * 
 * <li>{@link StandardQuadConsumer} Fabric causes vanilla baked models to send their 
 * quads via this interface. Can also be used by "hybrid" models that contain a mix
 * of vanilla baked quads and fast/dynamic vertexes to render the vanilla quad parts.</li>
 */
public interface DynamicVertexConsumer extends FastVertexConsumer, VertexBuilder, StandardQuadConsumer {
}
