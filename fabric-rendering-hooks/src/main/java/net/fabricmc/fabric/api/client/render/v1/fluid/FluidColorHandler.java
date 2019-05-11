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

package net.fabricmc.fabric.api.client.render.v1.fluid;

import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

/**
 * Interface for getting the tint color of a FluidState. Currently only used
 * as part of {@link FluidRenderHandler}.
 */
@FunctionalInterface
public interface FluidColorHandler {
    /**
     * Get the tint color for a fluid being rendered at a given position.
     * Please note that the default vanilla implementation ignores the
     * alpha value - as such, it must be contained in the texture itself!
     *
     * @param view The world view pertaining to the fluid. May be null!
     * @param pos The position of the fluid in the world. May be null!
     * @param state The current state of the fluid.
     * @return The tint color of the fluid.
     */
    int getFluidColor(/* Nullable */ ExtendedBlockView view, /* Nullable */ BlockPos pos, FluidState state);
}
