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

package net.fabricmc.fabric.api.modelevents.data;

import org.joml.Vector3f;

import net.minecraft.util.math.Direction;

/**
 * Provides data pertaining to a cubes individual face.
 */
public interface FaceData {
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
     *
     * If the face is mirrored, will be the opposite of {@see direction}.
     *
     * @return The lighting normal vector.
     */
    Vector3f lightingDirection();

    /**
     * The minimum axis vector of this face.
     */
    Vector3f min();

    /**
     * The maximum axis vector of this face.
     */
    Vector3f max();

    /**
     * The 3D dimensions of this face.
     */
    Vector3f size();

    /**
     * The 3D center point of this face.
     */
    Vector3f center();
}
