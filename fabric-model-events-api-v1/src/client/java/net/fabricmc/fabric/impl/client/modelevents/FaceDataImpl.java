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

package net.fabricmc.fabric.impl.client.modelevents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import net.fabricmc.fabric.api.client.modelevents.v1.data.DataCollection;
import net.fabricmc.fabric.api.client.modelevents.v1.data.FaceData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;

@ApiStatus.Internal
public record FaceDataImpl (
        Direction direction,
        Vector3f lightingDirection,
        Vector3f min,
        Vector3f max,
        Vector3f size,
        Vector3f center
    ) implements FaceData {
    public static DataCollection<FaceData> computeFromFaces(Map<Direction, DataCollection<FaceData>> cache, Direction direction, ModelPart.Quad[] sides) {
        return cache.computeIfAbsent(direction, d -> {
            List<FaceDataImpl.Container> faces = null;
            for (var side : sides) {
                if (((Container)side).getFabricDirection() != d) {
                    continue;
                }
                if (faces == null) {
                    faces = new ArrayList<>();
                }
                faces.add((Container)side);
            }
            return ListDataCollection.of(faces, Container::getFabricFaceData);
        });
    }

    public FaceDataImpl(Container face) {
        this(face, (ModelPart.Quad)face);
    }

    public FaceDataImpl(Container face, ModelPart.Quad quad) {
        this(face.getFabricDirection(), quad.direction,
                new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE),
                new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE),
                new Vector3f(),
                new Vector3f()
        );

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
        size.mul(0.5F, center).add(min);
    }

    public interface Container {
        FaceData getFabricFaceData();

        Direction getFabricDirection();
    }
}
