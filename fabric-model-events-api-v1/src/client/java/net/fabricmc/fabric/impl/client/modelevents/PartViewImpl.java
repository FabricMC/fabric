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

import java.util.Optional;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.modelevents.v1.ModelPartListener;
import net.fabricmc.fabric.api.client.modelevents.v1.PartTreePath;
import net.fabricmc.fabric.api.client.modelevents.v1.data.CubeData;
import net.fabricmc.fabric.api.client.modelevents.v1.data.DataCollection;
import net.fabricmc.fabric.api.client.modelevents.v1.data.PartView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@ApiStatus.Internal
final class PartViewImpl implements PartView {
    private final FabricPartHooks.Container part;

    private final PartTreePath path;
    private final ModelPartListener eventListener;

    @Nullable
    private DataCollection<CubeData> cubes;

    PartViewImpl(FabricPartHooks.Container part, PartTreePathImpl path) {
        this.part = part;
        this.path = path;
        this.eventListener = ModelPartCallbacksImpl.createInvoker(path);
    }

    public void dispatchEvents(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        float tickDelta = MinecraftClient.getInstance().getTickDelta();
        eventListener.onModelPartRendered(this, matrices, vertices, tickDelta, light, overlay, red, green, blue, alpha);
    }

    @Override
    public PartView root() {
        // getView may be null if the element hasn't yet been initialised.
        // This shouldn't ever happen unless a part view is being accessed
        // outside of the normal event loop.
        @Nullable
        PartView rootView = part.getHooks().getRoot().getView();
        return rootView == null ? this : rootView;
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
        // Cube data is only constructed upon request to try and avoid overhead when rendering.
        // Implemented as an if-null check rather than Suppliers.memoize(() -> {}) for similar reasons.
        if (cubes == null) cubes = new ListDataCollection<>(part.getCuboids(), cuboid -> (CubeData)cuboid);
        return cubes;
    }

    @Override
    public Optional<PartView> getChild(String name) {
        if (!part().hasChild(name)) {
            return Optional.empty();
        }

        return Optional.ofNullable(FabricPartHooks.Container.of(part().getChild(name)).getHooks().getView());
    }

    @Override
    public Optional<PartView> getChild(PartTreePath path) {
        // Simple tree traversal following the path's nodes.
        ModelPart part = part();
        for (String name : path) {
            if (!part.hasChild(name)) {
                return Optional.empty();
            }
            part = part.getChild(name);
        }
        return PartView.of(part);
    }

    @Override
    public void forEachChild(Consumer<PartView> partConsumer) {
        part.getChildren().values().forEach(child -> {
            PartView.of(child).ifPresent(partConsumer);
        });
    }
}
