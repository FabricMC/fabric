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

package net.fabricmc.fabric.api.client.model.fabric;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.fabricmc.fabric.impl.client.model.DamageModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Base class for specialized model implementations that need to wrap other baked models.
 * Avoids boilerplate code for pass-through methods. For example usage see {@link DamageModel}.
 */
public abstract class ForwardingBakedModel implements BakedModel, FabricBakedModel {
    protected abstract BakedModel wrapped();
    
    @Override
    public void produceBlockQuads(TerrainBlockView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        ((FabricBakedModel)wrapped()).produceBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    @Override
    public boolean isVanilla() {
        return ((FabricBakedModel)wrapped()).isVanilla();
    }

    @Override
    public void produceItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        ((FabricBakedModel)wrapped()).produceItemQuads(stack, randomSupplier, context);
    }

    @Override
    public boolean isRegular() {
        return ((FabricBakedModel)wrapped()).isRegular();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState blockState, Direction face, Random rand) {
        return wrapped().getQuads(blockState, face, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return wrapped().useAmbientOcclusion();
    }

    @Override
    public boolean hasDepthInGui() {
        return wrapped().hasDepthInGui();
    }

    @Override
    public boolean isBuiltin() {
        return wrapped().isBuiltin();
    }

    @Override
    public Sprite getSprite() {
        return wrapped().getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return wrapped().getTransformation();
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return wrapped().getItemPropertyOverrides();
    }
}
