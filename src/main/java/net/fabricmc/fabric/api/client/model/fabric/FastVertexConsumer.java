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

import net.minecraft.util.math.Direction;

/**
 * Interface for models with static, pre-baked vertex data to quickly
 * send that data into the rendering pipeline. The vertex data must have
 * been previously baked using {@link ModelMaterialBuilder}.<p>
 * 
 * For best performance, model should send multiple quads per invocation
 * and send all quads for a given material before sending quads with 
 * a different material. To achieve this is is helpful to material keep vertex
 * data in a single array organized by material and side and storing offsets
 * and material identifiers either elsewhere in the same array or a different structure.
 */
@FunctionalInterface
public interface FastVertexConsumer {
    void acceptFastVertexData(ModelMaterial material, int colorIndex, Direction cullFace, int[] vertexData, int startIndex, int quadCount);
}
