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

package net.fabricmc.fabric.impl.modelevents;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import net.fabricmc.fabric.api.modelevents.data.FaceData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;

@ApiStatus.Internal
public class FaceDataImpl implements FaceData {
    private final Direction direction;
    private final ModelPart.Quad quad;
    public final Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    public final Vector3f max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
    public final Vector3f size = new Vector3f();
    public final Vector3f center = new Vector3f();

    public FaceDataImpl(Direction direction, ModelPart.Quad quad) {
        this.direction = direction;
        this.quad = quad;

        switch (quad.vertices.length) {
            case 0:
                min.set(0, 0, 0);
                max.set(0, 0, 0);
                break;
            case 1:
                min.set(quad.vertices[0].pos);
                max.set(quad.vertices[0].pos);
                break;
            default:
                for (var vertex : quad.vertices) {
                    min.x = Math.min(vertex.pos.x, min.x);
                    min.y = Math.min(vertex.pos.y, min.y);
                    min.z = Math.min(vertex.pos.z, min.z);

                    max.x = Math.max(vertex.pos.x, max.x);
                    max.y = Math.max(vertex.pos.y, max.y);
                    max.z = Math.max(vertex.pos.z, max.z);
                }
        }

        max.sub(min, size);
        max.mul(0.5F, center).add(min);
    }

    @Override
    public Direction direction() {
        return direction;
    }

    @Override
    public Vector3f lightingDirection() {
        return quad.direction;
    }

    @Override
    public Vector3f min() {
        return min;
    }

    @Override
    public Vector3f max() {
        return max;
    }

    @Override
    public Vector3f size() {
        return size;
    }

    @Override
    public Vector3f center() {
        return center;
    }

    public interface Container {
        FaceData getFabricFaceData();

        Direction getFabricDirection();
    }
}
