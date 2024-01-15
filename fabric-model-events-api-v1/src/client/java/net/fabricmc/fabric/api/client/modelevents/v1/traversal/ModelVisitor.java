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

package net.fabricmc.fabric.api.client.modelevents.v1.traversal;

import java.util.function.BiPredicate;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.modelevents.v1.data.CubeData;
import net.fabricmc.fabric.api.client.modelevents.v1.data.FaceData;
import net.fabricmc.fabric.api.client.modelevents.v1.data.PartView;
import net.fabricmc.fabric.impl.client.modelevents.traversal.ModelVisitorImpl;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Represents a visitor used for walking through a model's tree structure.
 * <p>
 * The ModelVisitor implements what's needed in order for a mod to interate through the entire
 * structure of a model to render additions at specific points or onto individual faces.
 * <p>
 * This is provided to mods as an alternative to having to manually walk the trees both as a convenience
 * and because it gives API implementors the opportunity to make performance improvements to
 * model walking over time (as needed).
 * <p>
 * When visiting parts the matrix stack received is used to keep track of the position and rotation
 * when iterating through the tree, and will be adjusted such that when visiting the caller does not have
 * to worry about applying model transformations themselves.
 * <p>
 * To create one, use {@code ModelVisitor#builder()}.
 */
@ApiStatus.NonExtendable
public interface ModelVisitor {
    /**
     * Called when visiting a model part and its children.
     *
     * @param matrixStack Transformation matrix
     * @param part View of the part being visited
     * @return True to continue walking the tree, otherwise false to stop.
     */
    boolean visitPart(MatrixStack matrixStack, PartView part);

    /**
     * Called when visiting the cubes of a part.
     *
     * @param matrixStack Transformation matrix
     * @param cube Data for the cube being visited
     * @return True to continue walking the tree, otherwise false to stop.
     */
    boolean visitCube(MatrixStack matrixStack, CubeData cube);

    /**
     * Called when visiting the faces of a cube.
     *
     * @param matrixStack Transformation matrix
     * @param cube Data for the cube being visited
     * @return True to continue walking the tree, otherwise false to stop.
     */
    boolean visitFace(MatrixStack matrixStack, FaceData face);

    static Builder builder() {
        return new Builder();
    }

    public final class Builder {
        private @Nullable BiPredicate<MatrixStack, PartView> partConsumer;
        private @Nullable BiPredicate<MatrixStack, CubeData> cubeConsumer;
        private @Nullable BiPredicate<MatrixStack, FaceData> faceConsumer;

        private Builder() {}

        /**
         * Adds a receiver for visiting the parts of a model
         */
        public Builder visitParts(BiPredicate<MatrixStack, PartView> consumer) {
            this.partConsumer = consumer;
            return this;
        }

        /**
         * Adds a receiver for visiting the cubes of a model
         */
        public Builder visitCubes(BiPredicate<MatrixStack, CubeData> consumer) {
            this.cubeConsumer = consumer;
            return this;
        }

        /**
         * Adds a receiver for visiting the faces of a model's cubes
         */
        public Builder visitFaces(BiPredicate<MatrixStack, FaceData> consumer) {
            this.faceConsumer = consumer;
            return this;
        }

        public ModelVisitor build() {
            return ModelVisitorImpl.create(partConsumer, cubeConsumer, faceConsumer);
        }
    }
}
