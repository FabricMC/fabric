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

package net.fabricmc.fabric.api.client.modelevents.v1;

import net.fabricmc.fabric.api.client.modelevents.v1.data.PartView;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;


/**
 * Listener triggered when a model part is being rendered.
 * <p>
 * Unlike {@code EntityModelPartListener} and {@code BlockEntityModelPartListener} this
 * listener will always be triggered regardless of the context in which a model is being rendered.
 * <p>
 * If your intention is to apply effects to a model when the model is being used for a particular entity or block
 * entity, it's recommended to use either of the other listener types instead of this one.
 *
 * @see EntityModelPartListener
 * @see BlockEntityModelPartListener
 * @see ModelPartCallbacks#register(ModelPartListener)
 */
@FunctionalInterface
public interface ModelPartListener {
    void onModelPartRendered(PartView part, MatrixStack matrices, VertexConsumer vertexConsumer,
            float tickDelta,
            int light, int overlay,
            float red, float green, float blue, float alpha);
}
