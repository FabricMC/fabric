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

package net.fabricmc.fabric.api.client.model;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

/**
 * Accessor for enhanced baked model inputs and block entity renderers.
 * Do not retain a reference. Do not use outside the current thread.
 */
public interface BlockModelData {
    /**
     * In-world block position for model customization.<p>
     * 
     * This may be a mutable instance and you should avoid retaining a reference to it.
     */
    BlockPos pos();
    
    /**
     * Block view access for model customization.<p>
     * 
     * This will likely be a cached view and you should avoid retaining a reference to it.
     */
    ExtendedBlockView world();
    
    /**
     * Block state for model customization.<p>
     * 
     * Will be same as what you'd get from {@link #world()} but this method will be more performant.
     */
    BlockState blockState();
    
    /**
     * Block Entity if block has one. Null if not.<p>
     * 
     * Will be same as what you'd get from {@link #world()} but this method will be more performant.
     */
    BlockEntity blockEntity();
    
    /**
     * Will be deterministically initialized based on block pos. using same logic as 
     * what is normally passed to getQuads but handled lazily.<p>
     * 
     * Important distinction for enhanced vs. normal baked models: getQuads is called only 1X.  
     * Normally random is initialized with the same seed prior to each call.  For sake of performance
     * this isn't done with enhanced models.  Implementors will need to apply the same random bits
     * to parts of the model that expect same inputs.
     */
    Random random();
    
}
