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

package net.fabricmc.fabric.impl.client.model;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import net.fabricmc.fabric.api.client.model.fabric.FabricBakedModel;
import net.fabricmc.fabric.api.client.model.fabric.ForwardingBakedModel;
import net.fabricmc.fabric.api.client.model.fabric.ForwardingQuadMaker;
import net.fabricmc.fabric.api.client.model.fabric.Mesh;
import net.fabricmc.fabric.api.client.model.fabric.ModelHelper;
import net.fabricmc.fabric.api.client.model.fabric.QuadMaker;
import net.fabricmc.fabric.api.client.model.fabric.RenderContext;
import net.fabricmc.fabric.api.client.model.fabric.RenderMaterial;
import net.fabricmc.fabric.api.client.model.fabric.RendererAccess;
import net.fabricmc.fabric.api.client.model.fabric.TextureHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ExtendedBlockView;

/**
 * Specialized model wrapper that implements a general-purpose 
 * block-breaking render for enhanced models.<p>
 * 
 * Works by intercepting all model output and redirecting to dynamic
 * quads that are baked with single-layer, UV-locked damage texture.
 */
public class DamageModel extends ForwardingBakedModel {
    static final RenderMaterial DAMAGE_MATERIAL = RendererAccess.INSTANCE.hasRenderer() ? RendererAccess.INSTANCE.getRenderer().materialFinder().find() : null;
    
    private final DamageContext damageContext;
    private BakedModel wrappedModel;
    
    public DamageModel() {
        damageContext = new DamageModel.DamageContext();
    }
    
    public void prepare(BakedModel wrappedModel, Sprite sprite, BlockState blockState, BlockPos blockPos) {
        this.wrappedModel = wrappedModel;
        this.damageContext.damageQuad.damageSprite = sprite;
        this.damageContext.damageBlockState = blockState;
        this.damageContext.damageBlockPos = blockPos;
    }
    
    @Override
    protected BakedModel wrapped() {
        return wrappedModel;
    }
    
    @Override
    public void produceBlockQuads(ExtendedBlockView blockView, Function<BlockPos, Object> safeAccessor,
            BlockState state, BlockPos pos, Random random, long seed, RenderContext context) {
        damageContext.wrappedContext = context;
        ((FabricBakedModel)wrappedModel).produceBlockQuads(blockView, safeAccessor, state, pos, random, seed, damageContext);
    }
    
    private static class DamageContext implements RenderContext {
        private final DamageQuadMaker damageQuad = new DamageQuadMaker();
        private final Random random = new Random();
        private BlockState damageBlockState;
        private BlockPos damageBlockPos;
        RenderContext wrappedContext;
        
        @Override
        public BiConsumer<Mesh, Consumer<QuadMaker>> meshConsumer() {
            return (mesh, editor) -> {
                mesh.forEach(q -> {
                    damageQuad.wrappedQuadMaker = wrappedContext.quad(q.material());
                    q.copyTo(damageQuad.wrappedQuadMaker);
                    if(editor == null) {
                        damageQuad.emit();
                    } else {
                        editor.accept(damageQuad);
                    }
                });
            };
        }
    
        @Override
        public Consumer<BakedModel> fallbackConsumer() {
            return model -> {
                final long seed = damageBlockState.getRenderingSeed(damageBlockPos);
                for(int i = 0; i < 7; i++) {
                    Direction face = ModelHelper.faceFromIndex(i);
                    random.setSeed(seed);
                    List<BakedQuad> quads = model.getQuads(damageBlockState, face, random);
                    final int limit = quads.size();
                    for(int j = 0; j < limit; j++) {
                        damageQuad.wrappedQuadMaker = wrappedContext.quad(DAMAGE_MATERIAL);
                        BakedQuad q = quads.get(i);
                        damageQuad.fromVanilla(q.getVertexData(), 0, false);
                        damageQuad.cullFace(face);
                        damageQuad.nominalFace(q.getFace());
                        damageQuad.emit();
                    }
                }
            };
        }
    
        @Override
        public QuadMaker quad(RenderMaterial material) {
            damageQuad.wrappedQuadMaker = wrappedContext.quad(material);
            return damageQuad;
        }
    }

    private static class DamageQuadMaker extends ForwardingQuadMaker {
        private QuadMaker wrappedQuadMaker;
        private Sprite damageSprite;
        
        @Override
        protected QuadMaker wrapped() {
            return wrappedQuadMaker;
        }
        
        @Override
        public void emit() {
            wrappedQuadMaker.material(DAMAGE_MATERIAL);
            TextureHelper.bakeTextures(wrappedQuadMaker, 0, damageSprite, TextureHelper.LOCK_UV);
            wrappedQuadMaker.colorIndex(-1);
            wrappedQuadMaker.emit();
        }
    }
}