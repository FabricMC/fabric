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

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.api.client.modelevents.v1.data.FaceData;
import net.fabricmc.fabric.impl.client.modelevents.FaceDataImpl;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Direction;

@ApiStatus.Internal
@Mixin(ModelPart.Quad.class)
abstract class ModelPart_QuadMixin implements FaceDataImpl.Container {
    private Direction fabric_direction;
    private FaceDataImpl fabric_face_data;

    @Dynamic("Compiler-generated class constructor method")
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init_Quad(ModelPart.Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction,
            CallbackInfo info) {
        this.fabric_direction = direction;
    }

    @Override
    public Direction getFabricDirection() {
        return fabric_direction;
    }

    @Override
    public FaceData getFabricFaceData() {
        if (fabric_face_data == null) fabric_face_data = new FaceDataImpl(this);
        return fabric_face_data;
    }
}
