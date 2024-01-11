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

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.modelevents.BlockEntityModelPartListener;
import net.fabricmc.fabric.api.modelevents.EntityModelPartListener;
import net.fabricmc.fabric.api.modelevents.ModelPartCallbacks;
import net.fabricmc.fabric.api.modelevents.ModelPartListener;
import net.fabricmc.fabric.api.modelevents.PartTreePath;
import net.fabricmc.fabric.api.modelevents.data.PartView;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

@ApiStatus.Internal
public final class ModelPartCallbacksImpl implements ModelPartCallbacks {
    private static volatile long UPDATE_TIME = 0;
    @VisibleForTesting
    public static final PathTree<ModelPartCallbacksImpl> INSTANCES = new PathTree<>();

    public static ModelPartCallbacks get(MatchingStrategy matchingStrategy, PartTreePath path) {
        return INSTANCES.getOrCreate(matchingStrategy, path, ModelPartCallbacksImpl::new);
    }

    static ModelPartListener createInvoker(PartTreePath path) {
        return new Invoker(path);
    }

    private final Event<ModelPartListener> event = EventFactory.createArrayBacked(ModelPartListener.class, ModelPartCallbacksImpl::createInvoker);

    private ModelPartCallbacksImpl() {}

    @Override
    public void register(ModelPartListener listener) {
        Objects.requireNonNull(listener);
        event.register(guardRecursion(listener));
        UPDATE_TIME = System.currentTimeMillis();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T extends Entity> void register(EntityType<T> entityType, EntityModelPartListener<T> listener) {
        Objects.requireNonNull(listener);
        register((part, matrices, vertices, delta, light, overlay, r, g, b, a) -> {
            if (!ModelRenderContext.CURRENT_ENTITY.isEmpty()
                    && ModelRenderContext.CURRENT_ENTITY.top().getType() == entityType) {
                ((EntityModelPartListener)listener).onModelPartRendered(ModelRenderContext.CURRENT_ENTITY.top(), part, matrices, vertices, delta, light, overlay, r, g, b, a);
            }
        });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T extends BlockEntity> void register(BlockEntityType<T> entityType, BlockEntityModelPartListener<T> listener) {
        Objects.requireNonNull(listener);
        register((part, matrices, vertices, delta, light, overlay, r, g, b, a) -> {
            if (!ModelRenderContext.CURRENT_BLOCK_ENTITY.isEmpty()
                    && ModelRenderContext.CURRENT_BLOCK_ENTITY.top().getType() == entityType) {
                ((BlockEntityModelPartListener)listener).onModelPartRendered(ModelRenderContext.CURRENT_BLOCK_ENTITY.top(), part, matrices, vertices, delta, light, overlay, r, g, b, a);
            }
        });
    }

    static ModelPartListener createInvoker(ModelPartListener[] events) {
        return (part, matrices, vertices, delta, light, overlay, r, g, b, a) -> {
            for (var event : events) {
                matrices.push();
                event.onModelPartRendered(part, matrices, vertices, delta, light, overlay, r, g, b, a);
                matrices.pop();
            }
        };
    }

    private static ModelPartListener guardRecursion(final ModelPartListener listener) {
        var guard = new Object() {
            boolean active;
        };
        return (part, matrices, vertices, delta, light, overlay, r, g, b, a) -> {
            // We don't want handlers recursively triggering themselves
            if (guard.active) return;
            guard.active = true;
            try {
                listener.onModelPartRendered(part, matrices, vertices, delta, light, overlay, r, g, b, a);
            } finally {
                guard.active = false;
            }
        };
    }

    private static final class Invoker implements ModelPartListener {
        @Nullable
        private volatile ModelPartListener listener;
        private final PartTreePath path;
        private volatile long compileTime = -1;

        Invoker(PartTreePath path) {
            this.path = path;
        }

        @Override
        public void onModelPartRendered(PartView part, MatrixStack matrices, VertexConsumer vertexConsumer, float tickDelta, int light, int overlay, float red, float green, float blue, float alpha) {
            if (listener == null || (UPDATE_TIME > compileTime)) {
                compileTime = System.currentTimeMillis();
                var eventListeners = new ObjectArrayList<>();
                INSTANCES.findMatchingLeafNodes(path, c -> eventListeners.add(c.event.invoker()));
                listener = createInvoker(eventListeners.toArray(ModelPartListener[]::new));
            }

            listener.onModelPartRendered(part, matrices, vertexConsumer, tickDelta, light, overlay, red, green, blue, alpha);
        }
    }
}








