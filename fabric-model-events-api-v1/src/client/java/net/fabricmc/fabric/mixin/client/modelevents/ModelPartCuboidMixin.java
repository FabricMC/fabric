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

package net.fabricmc.fabric.mixin.client.modelevents;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.client.modelevents.v1.data.CubeData;
import net.fabricmc.fabric.api.client.modelevents.v1.data.DataCollection;
import net.fabricmc.fabric.api.client.modelevents.v1.data.FaceData;
import net.fabricmc.fabric.impl.client.modelevents.FaceDataImpl;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.model.ModelPart.Quad;
import net.minecraft.util.math.Direction;

@ApiStatus.Internal
@Mixin(value = ModelPart.Cuboid.class, priority = Integer.MAX_VALUE)
abstract class ModelPartCuboidMixin implements CubeData {
    @Shadow
    private @Final Quad[] sides;

    @Nullable
    private Map<Direction, DataCollection<FaceData>> fabric_cube_faces;

    private Dilation fabric_dilation;

    @Dynamic("Compiler-generated class constructor method")
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init_Cuboid(
            int u, int v,
            float x, float y, float z,
            float sizeX, float sizeY, float sizeZ,
            float extraX, float extraY, float extraZ,
            boolean mirror,
            float textureWidth, float textureHeight,
            Set<Direction> enabledFaces,
            CallbackInfo info) {
        fabric_dilation = new Dilation(extraX, extraY, extraZ);
    }

    @Unique
    @Override
    public Cuboid cuboid() {
        return (ModelPart.Cuboid)(Object)this;
    }

    @Unique
    @Override
    public Dilation dilation() {
        return fabric_dilation;
    }

    @Override
    public DataCollection<FaceData> getFaces(Direction direction) {
        if (sides.length == 0) return DataCollection.of();
        if (fabric_cube_faces == null) fabric_cube_faces = new EnumMap<>(Direction.class);
        return FaceDataImpl.computeFromFaces(fabric_cube_faces, direction, sides);
    }
}
