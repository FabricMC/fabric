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

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.fabric.api.client.model.FabricBakedQuad;
import net.fabricmc.fabric.api.client.model.FabricBakedQuadProducer;
import net.fabricmc.fabric.api.client.model.ModelBlockView;
import net.fabricmc.fabric.impl.client.model.BakedModelMixinHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Causes all BakedModel instances to also implement FabricBakedQuadProducer.
 * Purely additive - makes no change to internals or existing method contracts.
 */
@Mixin(BakedModel.class)
public interface MixinBakedModel extends FabricBakedQuadProducer {

    @Override
    default public void produceFabricBakedQuads(ModelBlockView blockView, BlockState state, BlockPos pos, Random random, long seed, Consumer<FabricBakedQuad> consumer) {
        final BakedModel me = (BakedModel)this;
	    final Direction[] DIRECTIONS = BakedModelMixinHelper.DIRECTIONS;

        for(int i = 0; i < 6; i++) {
            random.setSeed(seed);
            List<BakedQuad> quads = me.getQuads(state, DIRECTIONS[i], random);
            if(!quads.isEmpty() && Block.shouldDrawSide(state, blockView, pos, DIRECTIONS[i])) {
                final int count = quads.size();
                for(int j = 0; j < count; j++)
                    consumer.accept((FabricBakedQuad) quads.get(j));
            }
        }

        random.setSeed(seed);
        List<BakedQuad> quads = me.getQuads(state, null, random);
        if(!quads.isEmpty()) {
            final int count = quads.size();
            for(int j = 0; j < count; j++)
                consumer.accept((FabricBakedQuad) quads.get(j));
        }
    }
}
