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

import static net.fabricmc.fabric.impl.client.model.BakedModelMixinHelper.DIRECTIONS;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.fabric.api.client.model.fabric.ModelRenderContext;
import net.fabricmc.fabric.api.client.model.fabric.BlockModelQuadProducer;
import net.fabricmc.fabric.api.client.model.fabric.ModelBlockView;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;

/**
 * Avoids instanceof checks for baked models.
 */
@Mixin(BakedModel.class)
public interface MixinBakedModel extends BlockModelQuadProducer {

    @Override
    default void produceQuads(ModelBlockView blockView, BlockState state, BlockPos pos, Random random, long seed, ModelRenderContext consumer) {
        BakedModel me = (BakedModel)this;
        
        for(int i = 0; i < 6; i++) {
            random.setSeed(seed);
            List<BakedQuad> quads = me.getQuads(state, DIRECTIONS[i], random);
            final int count = quads.size();
            if(count != 0 && Block.shouldDrawSide(state, blockView, pos, DIRECTIONS[i])) {
                consumer.setQuadCullFace(DIRECTIONS[i]);
                for(int j = 0; j < count; j++) {
                    BakedQuad q = quads.get(j);
                    consumer.accept(q, false);
                }
            }
        }

        random.setSeed(seed);
        List<BakedQuad> quads = me.getQuads(state, null, random);
        final int count = quads.size();
        if(count != 0) {
            consumer.setQuadCullFace(null);
            for(int j = 0; j < count; j++) {
                BakedQuad q = quads.get(j);
                consumer.accept(q, false);
            }
        }
    }
}
