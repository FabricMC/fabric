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

import net.fabricmc.fabric.impl.client.model.DamageModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;

/**
 * Base class for specialized model implementations that need to wrap {@link QuadMaker}.
 * Avoids boilerplate code for pass-through methods. For example usage see {@link DamageModel}.
 */
public abstract class ForwardingQuadMaker implements QuadMaker {
    protected abstract QuadMaker wrapped();
    
    @Override
    public QuadMaker material(RenderMaterial material) {
        wrapped().material(material);
        return this;
    }
    
    @Override
    public void toVanilla(int spriteIndex, int[] target, int targetIndex, boolean isItem) {
        wrapped().toVanilla(spriteIndex, target, targetIndex, isItem);
    }

    @Override
    public void copyTo(QuadMaker target) {
        wrapped().copyTo(target);
    }

    @Override
    public RenderMaterial material() {
        return wrapped().material();
    }

    @Override
    public int colorIndex() {
        return wrapped().colorIndex();
    }

    @Override
    public Direction lightFace() {
        return wrapped().lightFace();
    }

    @Override
    public Direction cullFace() {
        return wrapped().cullFace();
    }

    @Override
    public Direction nominalFace() {
        return wrapped().nominalFace();
    }

    @Override
    public Vector3f faceNormal() {
        return wrapped().faceNormal();
    }

    @Override
    public int tag() {
        return wrapped().tag();
    }

    @Override
    public QuadMaker cullFace(Direction face) {
        wrapped().cullFace(face);
        return this;
    }

    @Override
    public QuadMaker nominalFace(Direction face) {
        wrapped().nominalFace(face);
        return this;
    }

    @Override
    public QuadMaker colorIndex(int colorIndex) {
        wrapped().colorIndex(colorIndex);
        return this;
    }

    @Override
    public QuadMaker fromVanilla(int[] quadData, int startIndex, boolean isItem) {
        wrapped().fromVanilla(quadData, startIndex, isItem);
        return this;
    }

    @Override
    public QuadMaker tag(int tag) {
        wrapped().tag(tag);
        return this;
    }
    
    @Override
    public void emit() {
        wrapped().emit();
    }

    @Override
    public Vector3f copyPos(int vertexIndex, Vector3f target) {
        return wrapped().copyPos(vertexIndex, target);
    }

    @Override
    public float posByIndex(int vertexIndex, int coordinateIndex) {
        return wrapped().posByIndex(vertexIndex, coordinateIndex);
    }
    
    @Override
    public float x(int vertexIndex) {
        return wrapped().x(vertexIndex);
    }

    @Override
    public float y(int vertexIndex) {
        return wrapped().y(vertexIndex);
    }

    @Override
    public float z(int vertexIndex) {
        return wrapped().z(vertexIndex);
    }

    @Override
    public boolean hasNormal(int vertexIndex) {
        return wrapped().hasNormal(vertexIndex);
    }

    @Override
    public Vector3f copyNormal(int vertexIndex, Vector3f target) {
        return wrapped().copyNormal(vertexIndex, target);
    }

    @Override
    public Vector4f copyNormal(int vertexIndex, Vector4f target) {
        return wrapped().copyNormal(vertexIndex, target);
    }

    @Override
    public float normX(int vertexIndex) {
        return wrapped().normX(vertexIndex);
    }

    @Override
    public float normY(int vertexIndex) {
        return wrapped().normY(vertexIndex);
    }

    @Override
    public float normZ(int vertexIndex) {
        return wrapped().normZ(vertexIndex);
    }

    @Override
    public float normExtra(int vertexIndex) {
        return wrapped().normExtra(vertexIndex);
    }

    @Override
    public int lightmap(int vertexIndex) {
        return wrapped().lightmap(vertexIndex);
    }

    @Override
    public int spriteColor(int vertexIndex, int spriteIndex) {
        return wrapped().spriteColor(vertexIndex, spriteIndex);
    }

    @Override
    public float spriteU(int vertexIndex, int spriteIndex) {
        return wrapped().spriteU(vertexIndex, spriteIndex);
    }

    @Override
    public float spriteV(int vertexIndex, int spriteIndex) {
        return wrapped().spriteV(vertexIndex, spriteIndex);
    }

    @Override
    public QuadMaker pos(int vertexIndex, float x, float y, float z) {
        wrapped().pos(vertexIndex, x, y, z);
        return this;
    }

    @Override
    public QuadMaker normal(int vertexIndex, float x, float y, float z, float extra) {
        wrapped().normal(vertexIndex, x, y, z, extra);
        return this;
    }

    @Override
    public QuadMaker lightmap(int vertexIndex, int lightmap) {
        wrapped().lightmap(vertexIndex, lightmap);
        return this;
    }

    @Override
    public QuadMaker spriteColor(int vertexIndex, int spriteIndex, int color) {
        wrapped().spriteColor(vertexIndex, spriteIndex, color);
        return this;
    }

    @Override
    public QuadMaker sprite(int vertexIndex, int spriteIndex, float u, float v) {
        wrapped().sprite(vertexIndex, spriteIndex, u, v);
        return this;
    }
}
