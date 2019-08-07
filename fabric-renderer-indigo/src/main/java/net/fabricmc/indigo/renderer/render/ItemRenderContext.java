/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.indigo.renderer.render;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.indigo.renderer.RenderMaterialImpl;
import net.fabricmc.indigo.renderer.accessor.AccessBufferBuilder;
import net.fabricmc.indigo.renderer.helper.ColorHelper;
import net.fabricmc.indigo.renderer.helper.GeometryHelper;
import net.fabricmc.indigo.renderer.mesh.EncodingFormat;
import net.fabricmc.indigo.renderer.mesh.MeshImpl;
import net.fabricmc.indigo.renderer.mesh.MutableQuadViewImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * The render context used for item rendering. 
 * Does not implement emissive lighting for sake
 * of simplicity in the default renderer. 
 */
public class ItemRenderContext extends AbstractRenderContext implements RenderContext {
    /** used to accept a method reference from the ItemRenderer */
    @FunctionalInterface
    public static interface VanillaQuadHandler {
        void accept(BufferBuilder bufferBuilder, List<BakedQuad> quads, int color, ItemStack stack);
    }

    private final ItemColors colorMap;
    private final Random random = new Random();
    private final Consumer<BakedModel> fallbackConsumer;
    BufferBuilder bufferBuilder;
    AccessBufferBuilder fabricBuffer;
    private int color;
    private ItemStack itemStack;
    private VanillaQuadHandler vanillaHandler;
    private boolean smoothShading = false;
    private boolean enchantment = false;
    
    private final Supplier<Random> randomSupplier = () -> {
        Random result = random;
        result.setSeed(42L);
        return random;
    };
    
    /** 
     * When rendering an enchanted item, input stack will be empty.
     * This value is populated earlier in the call tree when this is the case
     * so that we can render correct geometry and only a single texture.
     */
    public ItemStack enchantmentStack;
    
    private final int[] quadData = new int[EncodingFormat.MAX_STRIDE];;
    
    public ItemRenderContext(ItemColors colorMap) {
        this.colorMap = colorMap;
        this.fallbackConsumer = this::fallbackConsumer;
    }
    
    public void renderModel(FabricBakedModel model, int color, ItemStack stack, VanillaQuadHandler vanillaHandler) {
        this.color = color;
        
        if(stack.isEmpty() && enchantmentStack != null) {
            enchantment = true;
            this.itemStack = enchantmentStack;
            enchantmentStack = null;
        } else {
            enchantment = false;
            this.itemStack = stack;
        }
        
        this.vanillaHandler = vanillaHandler;
        Tessellator tessellator = Tessellator.getInstance();
        bufferBuilder = tessellator.getBufferBuilder();
        fabricBuffer = (AccessBufferBuilder)this.bufferBuilder;
        
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
        model.emitItemQuads(stack, randomSupplier, this);
        tessellator.draw();
        
        if(smoothShading) {
            GlStateManager.shadeModel(GL11.GL_FLAT);
            smoothShading = false;
        }
        
        bufferBuilder = null;
        fabricBuffer = null;
        tessellator = null;
        this.itemStack = null;
        this.vanillaHandler = null;
    }

    private class Maker extends MutableQuadViewImpl implements QuadEmitter {
        {
            data = quadData;
            clear();
        }
        
        @Override
        public Maker emit() {
            lightFace = GeometryHelper.lightFace(this);
            ColorHelper.applyDiffuseShading(this, false);
            renderQuad();
            clear();
            return this;
        }
    }
    
    private final Maker editorQuad = new Maker();
    
    private final Consumer<Mesh> meshConsumer = (mesh) -> {
        MeshImpl m = (MeshImpl)mesh;
        final int[] data = m.data();
        final int limit = data.length;
        int index = 0;
        while(index < limit) {
            RenderMaterialImpl.Value mat = RenderMaterialImpl.byIndex(data[index]);
            final int stride = EncodingFormat.stride(mat.spriteDepth());
            System.arraycopy(data, index, editorQuad.data(), 0, stride);
            editorQuad.load();
            index += stride;
            renderQuad();
        }
    };
    
    /**
     * Vanilla normally renders items with flat shading - meaning only
     * the last vertex normal is applied for lighting purposes. We 
     * support non-cube vertex normals so we need to change this to smooth
     * for models that use them.  We don't change it unless needed because
     * OpenGL state changes always impose a performance cost and this happens
     * for every item, every frame.
     */
    private void handleShading() {
        if(!smoothShading && editorQuad.hasVertexNormals()) {
            smoothShading = true;
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        }
    }
    
    private int quadColor() {
        final int colorIndex = editorQuad.colorIndex();
        int quadColor = color;
        if (!enchantment && quadColor == -1 && colorIndex != -1) {
            quadColor = colorMap.getColorMultiplier(itemStack, colorIndex);
            quadColor |= -16777216;
         }
        return quadColor;
    }
    
    private void colorizeAndOutput(int quadColor) {
        final MutableQuadViewImpl q = editorQuad;
        for(int i = 0; i < 4; i++) {
            int c = q.spriteColor(i, 0);
            c = ColorHelper.multiplyColor(quadColor, c);
            q.spriteColor(i, 0, ColorHelper.swapRedBlueIfNeeded(c));
        }
        fabricBuffer.fabric_putQuad(q);
    }
    
    private void renderQuad() {
        final MutableQuadViewImpl quad = editorQuad;
        if(!transform(editorQuad)) {
            return;
        }
        
        RenderMaterialImpl.Value mat = quad.material();
        final int quadColor = quadColor();
        final int textureCount = mat.spriteDepth();
        
        handleShading();
        
        // A bit of a hack - copy packed normals on top of lightmaps.
        // Violates normal encoding format but the editor content will be discarded
        // and this avoids the step of copying to a separate array.
        quad.copyNormals(quadData, EncodingFormat.VERTEX_START_OFFSET);
        
        colorizeAndOutput(!enchantment && mat.disableColorIndex(0) ? -1 : quadColor);
        
        // no need to render additional textures for enchantment overlay
        if(!enchantment && textureCount > 1) {
            quad.copyColorUV(1, quadData, EncodingFormat.VERTEX_START_OFFSET);
            colorizeAndOutput(mat.disableColorIndex(1) ? -1 : quadColor);
            
            if(textureCount == 3) {
                quad.copyColorUV(2, quadData, EncodingFormat.VERTEX_START_OFFSET);
                colorizeAndOutput(mat.disableColorIndex(2) ? -1 : quadColor);
            }
        }
    }
    
    @Override
    public Consumer<Mesh> meshConsumer() {
        return meshConsumer;
    }

    private void fallbackConsumer(BakedModel model) {
        if(hasTransform()) {
            // if there's a transform in effect, convert to mesh-based quads so that we can apply it
            for(int i = 0; i < 7; i++) {
                random.setSeed(42L);
                final Direction cullFace = ModelHelper.faceFromIndex(i);
                renderFallbackWithTransform(bufferBuilder, model.getQuads((BlockState)null, cullFace, random), color, itemStack, cullFace);
             }
        } else {
            for(int i = 0; i < 7; i++) {
                random.setSeed(42L);
                vanillaHandler.accept(bufferBuilder, model.getQuads((BlockState)null, ModelHelper.faceFromIndex(i), random), color, itemStack);
             }
        }
    };
    
    private void renderFallbackWithTransform(BufferBuilder bufferBuilder, List<BakedQuad> quads, int color, ItemStack stack, Direction cullFace) {
        if(quads.isEmpty()) {
            return;
        }
        if(CompatibilityHelper.canRender(quads.get(0).getVertexData())) {
            Maker editorQuad = this.editorQuad;
            for(BakedQuad q : quads) {
                editorQuad.clear();
                editorQuad.fromVanilla(q.getVertexData(), 0, false);
                editorQuad.cullFace(cullFace);
                final Direction lightFace = q.getFace();
                editorQuad.lightFace(lightFace);
                editorQuad.nominalFace(lightFace);
                editorQuad.colorIndex(q.getColorIndex());
                renderQuad();
            }
        } else {
            vanillaHandler.accept(bufferBuilder, quads, color, stack);
        }
    }
    
    @Override
    public Consumer<BakedModel> fallbackConsumer() {
        return fallbackConsumer;
    }

    @Override
    public QuadEmitter getEmitter() {
        editorQuad.clear();
        return editorQuad;
    }
}
