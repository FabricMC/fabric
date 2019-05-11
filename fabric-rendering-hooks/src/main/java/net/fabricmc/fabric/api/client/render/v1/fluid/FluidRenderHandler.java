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

package net.fabricmc.fabric.api.client.render.fluid;

import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

/**
 * Interface for handling the rendering of a FluidState.
 */
public interface FluidRenderHandler extends FluidColorHandler {
    /**
     * Get the sprites for a fluid being rendered at a given position.
     * For optimal performance, the sprites should be loaded as part of a
     * resource reload and *not* looked up every time the method is called!
     *
     * The "fabric-textures" module contains sprite rendering facilities, which may come in handy here.
     *
     * @param view The world view pertaining to the fluid. May be null!
     * @param pos The position of the fluid in the world. May be null!
     * @param state The current state of the fluid.
     * @return An array of size two: the first entry contains the "still" sprite,
     * while the second entry contains the "flowing" sprite.
     */
    Sprite[] getFluidSprites(/* Nullable */ ExtendedBlockView view, /* Nullable */ BlockPos pos, FluidState state);

    @Override
    default int getFluidColor(ExtendedBlockView view, BlockPos pos, FluidState state) {
        return -1;
    }
}
