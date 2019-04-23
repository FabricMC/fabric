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

package net.fabricmc.fabric.renderer.v1.mixin;

import java.util.Random;

import org.apache.commons.lang3.tuple.MutablePair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.renderer.v1.api.model.FabricBakedModel;
import net.fabricmc.fabric.renderer.v1.impl.DamageModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

/**
 * Implements hook for block-breaking render.
 */
@Mixin(BlockRenderManager.class)
public abstract class MixinBlockRenderManager {
    @Shadow private BlockModelRenderer renderer;
    @Shadow private Random random;
    
    private static final ThreadLocal<MutablePair<DamageModel, BakedModel>> DAMAGE_STATE = ThreadLocal.withInitial(() -> MutablePair.of(new DamageModel(), null));
    
    /**
     * Intercept the model assignment from getModel() - simpler than capturing entire LVT.
     */
    @ModifyVariable(method = "tesselateDamage", at = @At(value = "STORE", ordinal = 0), allow = 1, require = 1)
    private BakedModel hookTesselateDamageModel(BakedModel modelIn) {
        DAMAGE_STATE.get().right = modelIn;
        return modelIn;
    }
    
    /**
     * If the model we just captured is a fabric model, render it using a specialized 
     * damage render context and cancel rest of the logic. Avoids creating a bunch of
     * vanilla quads for complex meshes and honors dynamic model geometry.
     */
    @Inject(method = "tesselateDamage", cancellable = true, 
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/block/BlockModels;getModel(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/model/BakedModel;"))
    private void hookTesselateDamage(BlockState blockState, BlockPos blockPos, Sprite sprite, ExtendedBlockView blockView, CallbackInfo ci) {
        MutablePair<DamageModel, BakedModel> damageState = DAMAGE_STATE.get();
        if(damageState.right != null && !((FabricBakedModel)damageState.right).isVanillaAdapter()) {
            damageState.left.prepare(damageState.right, sprite, blockState, blockPos);
            this.renderer.tesselate(blockView, damageState.left, blockState, blockPos, Tessellator.getInstance().getBufferBuilder(), true, this.random, blockState.getRenderingSeed(blockPos));
            ci.cancel();
        }
    }
}
