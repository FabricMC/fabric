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
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.modelevents.ModelPartListener;
import net.fabricmc.fabric.api.modelevents.PartTreePath;
import net.fabricmc.fabric.api.modelevents.data.CubeData;
import net.fabricmc.fabric.api.modelevents.data.DataCollection;
import net.fabricmc.fabric.api.modelevents.data.PartView;
import net.fabricmc.fabric.impl.modelevents.FabricPartHooks.Container;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@ApiStatus.Internal
class PartViewImpl implements PartView {
    private final FabricPartHooks.Container part;
    @Nullable
    private PartTreePath path;
    @Nullable
    private final ModelPartListener eventListener;

    @Nullable
    private DataCollection<CubeData> cubeCollection;

    public PartViewImpl(FabricPartHooks.Container part, PartTreePathImpl path) {
        this.part = part;
        this.path = path;
        part.getChildren().forEach((name, child) -> {
            Container.of(child).getHooks().initializePaths(path.append(name));
        });
        this.eventListener = ModelPartCallbacksImpl.getInvoker(this.path);
    }

    public void dispatchEvents(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        float tickDelta = MinecraftClient.getInstance().getTickDelta();
        eventListener.onModelPartRendered(this, matrices, vertices, tickDelta, light, overlay, red, green, blue, alpha);
    }

    @Override
    public PartTreePath path() {
        return path;
    }

    @Override
    public ModelPart part() {
        return part.asPart();
    }

    @Override
    public DataCollection<CubeData> cubes() {
        if (cubeCollection == null) {
            cubeCollection = new ListDataCollection<>(part.getCuboids(), cuboid -> (CubeData)cuboid);
        }
        return cubeCollection;
    }
}
