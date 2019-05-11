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

package net.fabricmc.fabric.impl.client.render.fluid;

import net.fabricmc.fabric.api.client.render.v1.fluid.FluidRenderHandler;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

public class FluidRendererHookContainer {
    public ExtendedBlockView view;
    public BlockPos pos;
    public FluidState state;
    public FluidRenderHandler handler;

    public void clear() {
        view = null;
        pos = null;
        state = null;
        handler = null;
    }
}
