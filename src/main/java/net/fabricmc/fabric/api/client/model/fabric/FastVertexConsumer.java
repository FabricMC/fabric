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
 * been previously baked using {@link FastVertexBuilder}.<p>
 * 
 * Designed to favor implementations that pack multiple quads into a single
 * [] int array and will iterate all quads in the given range. However,
 * models are still responsible for face culling and, for multi-part or
 * compound models, determining which parts to render.  This means models still
 * need to track array ranges by face/part.<p>
 */
@FunctionalInterface
public interface FastVertexConsumer {
    /**
     * Renders/processes vertex data previously packed using {@link FastVertexBuilder}.<p>
     * 
     * @param vertexData  Array with one or more packed quads.
     * @param startIndex  Array index of first quad.
     * @param endIndex    End of range (exclusive). Range can include multiple quads.
     */
    void acceptFastVertexData(int[] vertexData, int startIndex, int endIndex);
}
