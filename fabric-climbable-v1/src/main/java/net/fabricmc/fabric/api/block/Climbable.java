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

package net.fabricmc.fabric.api.block;

import net.fabricmc.fabric.api.util.TriState;
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
     * @param state The block state of the block being climbed.
     * @param pos The position of the block being climbed.
	 *
	 * @return Should either result TriState.True or TriState.False to indicate
	 * whether or not this block can be climbed. Returning TriState.Default is used
	 * to indicate that the game should ignore the result of this method and instead
	 * perform the hardcoded, vanilla checks. Only vanilla blocks should return
	 * TriState.Default.
     */
    TriState canClimb(LivingEntity entity, BlockState state, BlockPos pos);

    /**
     * @return The suffix of the death message when falling off this block and dying.
	 * Your translation file should include the translation key
	 * "death.fell.accident.suffix", where "suffix" is the string returned by this
	 * method By default, this returns "generic", which is the default suffix vanilla
	 * suffix, so you only need to override this if your block has a custom fall death
	 * message.
     */
    default String getFallDeathSuffix() {
		return "generic";
	}

}
