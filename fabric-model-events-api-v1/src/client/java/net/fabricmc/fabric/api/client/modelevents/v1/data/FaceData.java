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
import org.joml.Quaternionf;
import org.joml.Vector3fc;

import net.fabricmc.fabric.api.client.modelevents.v1.traversal.ModelVisitor;
import net.fabricmc.fabric.api.client.modelevents.v1.traversal.Traversable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

/**
 * Provides data pertaining to a cubes individual face.
 */
@ApiStatus.NonExtendable
public interface FaceData extends Traversable {
    /**
     * The physical orientation of this face.
     *
     * If the face is mirrored, will be the opposite of {@see lightingDirection}.
     *
     * @return The facing direction.
     */
    Direction direction();

    /**
     * The face direction vector for use when applying directional lighting.
     * <p>
     * If the face is mirrored, will be the opposite of {@see direction}.
     *
     * @return The lighting normal vector.
     */
    Vector3fc lightingDirection();

    /**
     * Gets the face orientation.
     * <p>
     * The returned quaternion represents a rotation perpendicular to the plane parallel to this that you can use to rotate
     * a model to render
     *
     * @return Quaternion rotation
     */
    default Quaternionf rotation() {
        return direction().getRotationQuaternion();
    }

    /**
     * The minimum axis vector of this face.
     */
    Vector3fc min();

    /**
     * The maximum axis vector of this face.
     */
    Vector3fc max();

    /**
     * The 3D dimensions of this face.
     */
    Vector3fc size();

    /**
     * The 3D center point of this face.
     */
    Vector3fc center();


    @Override
    default void traverse(MatrixStack matrices, ModelVisitor visitor) {
        visitor.visitFace(matrices, this);
    }
}
