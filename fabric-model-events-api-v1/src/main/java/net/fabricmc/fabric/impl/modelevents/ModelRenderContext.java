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

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;

@ApiStatus.Internal
public final class ModelRenderContext {
    public static final Stack<Entity> CURRENT_ENTITY = new ObjectArrayList<>();
    public static final Stack<BlockEntity> CURRENT_BLOCK_ENTITY = new ObjectArrayList<>();

    private ModelRenderContext() { }

    public static Runnable captureBlockEntity(BlockEntity entity, Runnable renderAction) {
        return () -> {
            try {
                ModelRenderContext.CURRENT_BLOCK_ENTITY.push(entity);
                renderAction.run();
            } finally {
                if (!ModelRenderContext.CURRENT_BLOCK_ENTITY.isEmpty()) {
                    ModelRenderContext.CURRENT_BLOCK_ENTITY.pop();
                }
            }
        };
    }
}
