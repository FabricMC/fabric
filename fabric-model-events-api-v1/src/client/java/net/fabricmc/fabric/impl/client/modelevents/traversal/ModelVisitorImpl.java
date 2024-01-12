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

package net.fabricmc.fabric.impl.client.modelevents.traversal;

import java.util.function.BiPredicate;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.modelevents.v1.data.CubeData;
import net.fabricmc.fabric.api.client.modelevents.v1.data.FaceData;
import net.fabricmc.fabric.api.client.modelevents.v1.data.PartView;
import net.fabricmc.fabric.api.client.modelevents.v1.traversal.ModelVisitor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

@ApiStatus.Internal
public final class ModelVisitorImpl implements ModelVisitor {
    private static final ModelVisitor EMPTY = new ModelVisitor() {
        @Override
        public boolean visitPart(MatrixStack matrixStack, PartView part) {  return false;}

        @Override
        public boolean visitCube(MatrixStack matrixStack, CubeData cube) { return false; }

        @Override
        public boolean visitFace(MatrixStack matrixStack, FaceData face) { return false; }
    };

    private final @Nullable BiPredicate<MatrixStack, PartView> partConsumer;
    private final @Nullable BiPredicate<MatrixStack, CubeData> cubeConsumer;
    private final @Nullable BiPredicate<MatrixStack, FaceData> faceConsumer;

    public static ModelVisitor create(
            @Nullable BiPredicate<MatrixStack, PartView> partConsumer,
            @Nullable BiPredicate<MatrixStack, CubeData> cubeConsumer,
            @Nullable BiPredicate<MatrixStack, FaceData> faceConsumer) {
        if (partConsumer == null && cubeConsumer == null && faceConsumer == null) {
            return EMPTY;
        }
        return new ModelVisitorImpl(partConsumer, cubeConsumer, faceConsumer);
    }

    private ModelVisitorImpl(
            @Nullable BiPredicate<MatrixStack, PartView> partConsumer,
            @Nullable BiPredicate<MatrixStack, CubeData> cubeConsumer,
            @Nullable BiPredicate<MatrixStack, FaceData> faceConsumer) {
        this.partConsumer = partConsumer;
        this.cubeConsumer = cubeConsumer;
        this.faceConsumer = faceConsumer;
    }

    @Override
    public boolean visitPart(MatrixStack matrixStack, PartView part) {
        boolean keepGoing = true;
        matrixStack.push();
        part.part().rotate(matrixStack);
        if (partConsumer != null) {
            keepGoing = partConsumer.test(matrixStack, part);
        }
        if (cubeConsumer != null || faceConsumer != null) {
            for (CubeData cube : part.cubes()) {
                if (!visitCube(matrixStack, cube)) {
                    break;
                }
            }
        }
        matrixStack.pop();
        return keepGoing;
    }

    @Override
    public boolean visitCube(MatrixStack matrixStack, CubeData cube) {
        boolean keepGoing = true;
        if (cubeConsumer != null) {
            matrixStack.push();
            cube.translate(matrixStack);
            keepGoing = cubeConsumer.test(matrixStack, cube);
            matrixStack.pop();
        }
        // faces should not be positioned relative to their cube
        if (faceConsumer != null) {
            for (Direction direction : Direction.values()) {
                for (FaceData face : cube.getFaces(direction)) {
                    if (!visitFace(matrixStack, face)) {
                        return keepGoing;
                    }
                }
            }
        }
        return keepGoing;
    }

    @Override
    public boolean visitFace(MatrixStack matrixStack, FaceData face) {
        boolean keepGoing = false;
        if (faceConsumer != null) {
            matrixStack.push();
            keepGoing = faceConsumer.test(matrixStack, face);
            matrixStack.pop();
        }
        return keepGoing;
    }
}
