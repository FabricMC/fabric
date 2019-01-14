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

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;

/**
 * Interface for baked models that output vertex data previously prepared with
 * {@link FastVertexBuilder}.<p>
 * 
 * TODO: Make BakedModel extend this interface and send standard vertex data by default.
 */
public interface FastVertexProducer {
    /**
     * This method will be called during chunk rebuilds to generate the static portion of a
     * block model when the model implements this interface. The method serves the same
     * purpose as {@link BakedModel#getQuads(BlockState, net.minecraft.util.math.Direction, Random)}
     * but operates differently. <p>
     * 
     * This method will always be called exactly one time per block position 
     * per chunk rebuild, irrespective of which or how many faces or block render layers are included 
     * in the model. Models must output all quads in a single pass.<p>
     * 
     * Models with face-planar quads must handle face occlusion and omit quads on faces 
     * obscured by neighboring blocks.  Performing this check in the model allows implementations 
     * to exploit internal knowledge of geometry to avoid unnecessary checks.<p>
     * 
     * With {@link BakedModel#getQuads(BlockState, net.minecraft.util.math.Direction, Random)}, the random 
     * parameter is normally initialized with the same seed prior to each face/render layer.
     * Because this method is called only once per block, implementations should reseed the 
     * provided Random for models that expect it. This is especially important for implementations 
     * that "wrap" existing models.<p>
     */
    void produceFastVertexData(BlockState state, BlockPos pos, Random random, long seed, FastVertexConsumer consumer);
}
