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

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

/**
 * Specialized {@link MutableQuadView} obtained via {@link MeshBuilder#getEmitter()}
 * to append quads during mesh building.<p>
 * 
 * Also obtained from {@link RenderContext#getEmitter(RenderMaterial)} to submit 
 * dynamic quads one-by-one at render time.<p>
 * 
 * Instances of {@link QuadEmitter} will practically always be
 * threadlocal and/or reused - do not retain references.<p>
 * 
 * Only the renderer should implement or extend this interface. 
 */
public interface QuadEmitter extends MutableQuadView {
    @Override
    QuadEmitter material(RenderMaterial material);

    @Override
    QuadEmitter cullFace(Direction face);

    @Override
    QuadEmitter nominalFace(Direction face);

    @Override
    QuadEmitter colorIndex(int colorIndex);

    @Override
    QuadEmitter fromVanilla(int[] quadData, int startIndex, boolean isItem);

    @Override
    QuadEmitter tag(int tag);

    @Override
    QuadEmitter pos(int vertexIndex, float x, float y, float z);

    @Override
    default QuadEmitter pos(int vertexIndex, Vector3f vec) {
        MutableQuadView.super.pos(vertexIndex, vec);
        return this;
    }

    @Override
    default QuadEmitter normal(int vertexIndex, Vector3f vec) {
        MutableQuadView.super.normal(vertexIndex, vec);
        return this;
    }

    @Override
    QuadEmitter lightmap(int vertexIndex, int lightmap);

    @Override
    default QuadEmitter lightmap(int b0, int b1, int b2, int b3) {
        MutableQuadView.super.lightmap(b0, b1, b2, b3);
        return this;
    }

    @Override
    QuadEmitter spriteColor(int vertexIndex, int spriteIndex, int color);

    @Override
    default QuadEmitter spriteColor(int spriteIndex, int c0, int c1, int c2, int c3) {
        MutableQuadView.super.spriteColor(spriteIndex, c0, c1, c2, c3);
        return this;
    }

    @Override
    QuadEmitter sprite(int vertexIndex, int spriteIndex, float u, float v);

    @Override
    QuadEmitter sprite(int spriteIndex, Sprite sprite, int bakeFlags);

    /**
     * In static mesh building, causes quad to be appended to the mesh being built.
     * In a dynamic render context, create a new quad to be output to rendering.
     * In both cases, current instance is reset to default values.
     */
    QuadEmitter emit();
}
