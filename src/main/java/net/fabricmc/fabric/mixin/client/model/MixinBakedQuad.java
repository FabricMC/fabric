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

package net.fabricmc.fabric.mixin.client.model;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.fabric.api.client.model.FabricBakedQuad;
import net.fabricmc.fabric.api.client.model.FabricVertexFormat;
import net.minecraft.client.render.model.BakedQuad;

/**
 * Causes all BakedQuad instances to also implement FabricBakedQuad.
 * Purely additive - makes no change to internals or existing method contracts.
 */
@Mixin(BakedQuad.class)
public abstract class MixinBakedQuad implements FabricBakedQuad {
    @Override
    public int getRenderLayerFlags() {
        return 0;
    }

    @Override
    public FabricVertexFormat getFormat() {
        return FabricVertexFormat.STANDARD_UNSPECIFIED;
    }

    @Override
    public int getLightingFlags() {
        return 0;
    }
}
