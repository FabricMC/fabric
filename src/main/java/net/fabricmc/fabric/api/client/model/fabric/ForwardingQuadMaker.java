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
    public void toVanilla(int layerIndex, int[] target, int targetIndex, boolean isItem) {
        wrapped().toVanilla(layerIndex, target, targetIndex, isItem);
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
    public VertexEditor vertex(int vertexIndex) {
        return wrapped().vertex(vertexIndex);
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
}
