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

package net.fabricmc.fabric.fluidrender;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.fluid.Fluids;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class FluidRendererModClient implements ClientModInitializer, SimpleSynchronousResourceReloadListener {
    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.ASSETS).registerReloadListener(this);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier("fabric:fluid_renderer_test");
    }

    @Override
    public void apply(ResourceManager rm) {
        FluidRenderHandler lavaHandler = FluidRenderHandlerRegistry.INSTANCE.get(Fluids.LAVA);
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.WATER, lavaHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.FLOWING_WATER, lavaHandler);
    }
}
