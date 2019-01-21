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
 * Interface for models with static, pre-baked vertex data to quickly
 * send that data into the rendering pipeline. The vertex data must have
 * been previously baked using {@link ModelMaterialBuilder}.<p>
 * 
 * TODO: not sure below paragraph is really true - renderer is going to need
 * to sort quads to buffers no matter what - especially using fixed pipeline.
 * And may become irrelevant per TODOs below...
 * 
 * For best performance, model should send all quads for a given material before sending 
 * quads with a different material. To achieve this it is helpful to keep vertex
 * data in a single array organized by material and side and storing offsets
 * and material identifiers either elsewhere in the same array or a different structure.
 * 
 * TODO: Evaluate adding a multi-quad variant for array-packed quads, or...
 * 
 * TODO: Consider having renderer serialize the material with the vertex data instead of
 * requiring the model to send it.  Material has to match anyway, and it seems this just
 * creates additional burden on the model implementation and also the opportunity
 * for the model to break stuff by sending mismatched materials.  If change is made, renderer
 * should probably provide a way to query the material of packed vertices.
 * 
 * TODO: Same question for color index. Does it need to be dynamic in the model or should
 * it be pre-baked? If pre-baked, then this call could simply be 
 * acceptFastVertexData(int[] vertexData, int startIndex, int endIndex) and it
 * could iterate quads until it gets to the end of the range.
 */
@FunctionalInterface
public interface FastVertexConsumer {
    void acceptFastVertexData(ModelMaterial material, int colorIndex, int[] vertexData, int startIndex);
}
