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

package net.fabricmc.fabric.api.client.modelevents.v1.data;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import net.fabricmc.fabric.api.client.modelevents.v1.traversal.ModelVisitor;
import net.fabricmc.fabric.api.client.modelevents.v1.traversal.Traversable;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

/**
 * Data pertaining to an individual cube.
 */
@ApiStatus.NonExtendable
public interface CubeData extends Traversable {
    /**
     * Gets a direct reference to the cube this data is describing.
     */
    ModelPart.Cuboid cuboid();

    /**
     * Gets all of the faces of this cube matching a particular direction.
     * @param direction The direction of the faces to return.

     * @return A {@see DataCollection} containing the matching faces.
     */
    DataCollection<FaceData> getFaces(Direction direction);

    /**
     * Gets the dilation that was applied to this cube during creation.
     */
    Dilation dilation();

    /**
     * The length of the cube along the X axis.
     */
    default float sizeX() {
        return cuboid().maxX - cuboid().minX;
    }

    /**
     * The length of the cube along the Y axis.
     */
    default float sizeY() {
        return cuboid().maxY - cuboid().minY;
    }

    /**
     * The length of the cube along the Z axis.
     */
    default float sizeZ() {
        return cuboid().maxZ - cuboid().minZ;
    }

    /**
     * The 3D dimensions of this cube
     */
    default Vector3f size() {
        return new Vector3f(sizeX(), sizeY(), sizeZ());
    }

    /**
     * The origin point of this cube
     */
    default Vector3f min() {
        return new Vector3f(cuboid().minX, cuboid().minY, cuboid().minZ);
    }

    /**
     * The maximum of this cube
     */
    default Vector3f max() {
        return new Vector3f(cuboid().maxX, cuboid().maxY, cuboid().maxZ);
    }

    /**
     * The midpoint of the cube
     */
    default Vector3f center() {
        return new Vector3f(
            cuboid().minX + (sizeX() / 2F),
            cuboid().minY + (sizeY() / 2F),
            cuboid().minZ + (sizeZ() / 2F)
        );
    }

    /**
     * Translates a matrix stack to have the same origin as this cube.
     *
     * @param matrices The matrices to translate.
     */
    default void translate(MatrixStack matrices) {
        matrices.translate(cuboid().minX / 16F, cuboid().minY / 16F, cuboid().minZ / 16F);
    }

    @Override
    default void traverse(MatrixStack matrices, ModelVisitor visitor) {
        visitor.visitCube(matrices, this);
    }
}
