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

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.fabric.api.client.model.fabric.TerrainMeshProducer;
import net.fabricmc.fabric.api.client.model.fabric.ModelBlockView;
import net.fabricmc.fabric.api.client.model.fabric.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;

/**
 * Avoids instanceof checks for baked models.
 */
@Mixin(BakedModel.class)
public interface MixinBakedModel extends TerrainMeshProducer {
    @Override
    default void produceQuads(ModelBlockView blockView, BlockState state, BlockPos pos, Random random, long seed, RenderContext context) {
        context.fallbackConsumer().accept((BakedModel)this);
    }
}
