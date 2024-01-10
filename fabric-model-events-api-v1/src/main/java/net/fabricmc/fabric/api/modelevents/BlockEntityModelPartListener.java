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

package net.fabricmc.fabric.api.modelevents;

import net.fabricmc.fabric.api.modelevents.data.PartView;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Listener triggered when a model part is being rendered in the context of an active block entity.
 * <p>
 * This listener is only triggered when a block entity is being rendered at the time.
 * It won't be triggered if the model is being used in other situations. If your intention is to
 * listen for all instances of a model being rendered, it's recommended to use {@code ModelPartListener} instead.
 *
 * @param <T> block entity type
 *
 * @see EntityModelPartListener
 * @see ModelPartListener
 * @see ModelPartCallbacks#register(BlockEntityModelPartListener)
 */
@FunctionalInterface
public interface BlockEntityModelPartListener<T extends BlockEntity> {
    void onModelPartRendered(T blockEntity, BlockEntityRenderer<T> renderer, PartView part,
            MatrixStack matrices, VertexConsumer vertexConsumer,
            float tickDelta,
            int light, int overlay,
            float red, float green, float blue, float alpha);
}
