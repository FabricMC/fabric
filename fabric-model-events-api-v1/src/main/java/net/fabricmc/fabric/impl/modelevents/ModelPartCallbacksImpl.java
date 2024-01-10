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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.modelevents.BlockEntityModelPartListener;
import net.fabricmc.fabric.api.modelevents.EntityModelPartListener;
import net.fabricmc.fabric.api.modelevents.ModelPartCallbacks;
import net.fabricmc.fabric.api.modelevents.ModelPartListener;
import net.fabricmc.fabric.api.modelevents.PartTreePath;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

@ApiStatus.Internal
public class ModelPartCallbacksImpl implements ModelPartCallbacks {
    private static final PathTree<ModelPartCallbacksImpl> INSTANCES = new PathTree<>();

    public static ModelPartCallbacks get(MatchingStrategy matchingStrategy, PartTreePath path) {
        return INSTANCES.getOrCreate(matchingStrategy, path, ModelPartCallbacksImpl::new);
    }

    static ModelPartListener getInvoker(PartTreePath path) {
        var eventListeners = new ObjectArrayList<>();
        INSTANCES.findMatchingLeafNodes(path, c -> eventListeners.add(c.event.invoker()));
        return createInvoker(eventListeners.toArray(ModelPartListener[]::new));
    }

    private static final ModelPartListener createInvoker(ModelPartListener[] listeners) {
        return (part, matrices, vertices, delta, light, overlay, r, g, b, a) -> {
            for (var listener : listeners) {
                listener.onModelPartRendered(part, matrices, vertices, delta, light, overlay, r, g, b, a);
            }
        };
    }

    private final Event<ModelPartListener> event = EventFactory.createArrayBacked(ModelPartListener.class, ModelPartCallbacksImpl::createInvoker);

    @Override
    public void register(ModelPartListener listener) {
        event.register(listener);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T extends Entity> void register(EntityType<T> entityType, EntityModelPartListener<T> listener) {
        register((part, matrices, vertices, delta, light, overlay, r, g, b, a) -> {
            if (ModelRenderContext.currentEntity != null
                    && ModelRenderContext.currentEntityRenderer != null
                    && ModelRenderContext.currentEntity.getType() == entityType) {
                ((EntityModelPartListener)listener).onModelPartRendered(ModelRenderContext.currentEntity, ModelRenderContext.currentEntityRenderer, part, matrices, vertices, delta, light, overlay, r, g, b, a);
            }
        });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T extends BlockEntity> void register(BlockEntityType<T> entityType, BlockEntityModelPartListener<T> listener) {
        register((part, matrices, vertices, delta, light, overlay, r, g, b, a) -> {
            if (ModelRenderContext.currentBlockEntity != null
                    && ModelRenderContext.currentBlockEntityRenderer != null
                    && ModelRenderContext.currentBlockEntity.getType() == entityType) {
                ((BlockEntityModelPartListener)listener).onModelPartRendered(ModelRenderContext.currentBlockEntity, ModelRenderContext.currentBlockEntityRenderer, part, matrices, vertices, delta, light, overlay, r, g, b, a);
            }
        });
    }
}
