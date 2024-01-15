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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.client.modelevents.ModelRenderContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

/**
 * We inject before and after rendering an entity via the dispatcher. This catches 99% of use cases
 * Things that will be missed by this:
 * <pre>
 * - player arm rendering (covered by {@link PlayerEntityRendererMixin}
 * - mods that render an entity by doing {@code dispatcher.getRenderer(entity).render(...)} (not supported)
 * </pre>
 * Without this injection (or rendering via other methods) normal events still work, but events that specifically
 * depend on the entity being rendered being known, will be skipped.
 *
 * @see PlayerEntityRendererMixin
 * @see BlockEntityRenderDispatcherMixin
 */
@ApiStatus.Internal
@Mixin(value = EntityRenderDispatcher.class, priority = 900000 /* Priority set to inject last so mods' injections are not affected */)
abstract class EntityRenderDispatcherMixin {
    // Descriptor in a string to avoid repeating ourselves
    private static final String RENDER = "render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V";

    @Inject(method = RENDER, at = @At("HEAD"))
    private <E extends Entity> void pushEntityContextForModelPartEventsBeforeRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        ModelRenderContext.pushEntityContext(entity);
    }

    @Inject(method = RENDER, at = @At("RETURN"))
    private <E extends Entity> void popEntityContextForModelPartEventsAfterRender(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        ModelRenderContext.popEntityContext();
    }
}
