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

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;

/**
 * Data pertaining to an individual cube.
 */
public interface CubeData {
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
     * The midpoint of the cube.
     */
    default Vector3f getCenter() {
        return new Vector3f(
            cuboid().minX + (sizeX() / 2F),
            cuboid().minY + (sizeY() / 2F),
            cuboid().minZ + (sizeZ() / 2F)
        );
    }
}
