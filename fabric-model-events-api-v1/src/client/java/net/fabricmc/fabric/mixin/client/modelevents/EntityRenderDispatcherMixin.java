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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.client.modelevents.ModelRenderContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

/**
 * We inject before and after rendering an entity via the dispatcher. This catches 99% of use cases
 * Things that will be missed by this:
 * - player arm rendering
 * - mods that render an entity by doing {@code dispatcher.getRenderer(entity).render(...)}
 *
 * Without this injection (or rendering via other methods) normal events still work, but events that specifically
 * depend on the entity being rendered being known, will be skipped.
 */
@ApiStatus.Internal
@Mixin(value = EntityRenderDispatcher.class, priority = Integer.MAX_VALUE)
abstract class EntityRenderDispatcherMixin {
    private static final String RENDER = "render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V";

    @Inject(method = RENDER, at = @At("HEAD"))
    private <E extends Entity> void before_render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        ModelRenderContext.pushEntityContext(entity);
    }

    @Inject(method = RENDER, at = @At("RETURN"))
    private <E extends Entity> void after_render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        ModelRenderContext.popEntityContext();
    }
}
/**
 * Player arm rendering
 */
@ApiStatus.Internal
@Mixin(value = PlayerEntityRenderer.class, priority = Integer.MAX_VALUE)
abstract class PlayerEntityRendererMixin {
    private static final String RENDER_ARM = "renderArm";

    @Inject(method = RENDER_ARM, at = @At("HEAD"))
    private void before_RenderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        ModelRenderContext.pushEntityContext(player);
    }
    @Inject(method = RENDER_ARM, at = @At("RETURN"))
    private void after_RenderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        ModelRenderContext.popEntityContext();
    }
}
/**
 * Common injection point for rendering a block entity
 */
@ApiStatus.Internal
@Mixin(value = BlockEntityRenderDispatcher.class, priority = Integer.MAX_VALUE)
abstract class BlockEntityRenderDispatcherMixin {
    private static final String RUN_REPORTED = "runReported(Lnet/minecraft/block/entity/BlockEntity;Ljava/lang/Runnable;)V";

    @ModifyVariable(method = RUN_REPORTED, at = @At("HEAD"))
    private static Runnable around_render(Runnable runnable, BlockEntity blockEntity) {
        return ModelRenderContext.captureBlockEntity(blockEntity, runnable);
    }
}
