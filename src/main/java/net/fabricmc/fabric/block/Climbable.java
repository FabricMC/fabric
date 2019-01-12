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

package net.fabricmc.fabric.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Blocks that implement this can be climbed by LivingEntities.
 */
public interface Climbable {

    /**
     * Determines if the passed LivingEntity can climb this block.
     *
     * @param entity The LivingEntity that is attempting to climb this block.
     * @param state The block state of the ladder being climbed.
     * @param pos The position of the block.
     */
    default boolean canClimb(LivingEntity entity, BlockState state, BlockPos pos) {
        return true;
    }

    /**
     * The suffix of the death message when falling off this block.
     *
     * @return the suffix of the death message.
     */
    String getFallDeathSuffix();

}
