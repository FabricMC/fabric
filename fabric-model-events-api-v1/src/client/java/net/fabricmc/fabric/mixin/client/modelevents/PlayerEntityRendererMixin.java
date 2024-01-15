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
import net.fabricmc.fabric.impl.client.modelevents.ModelRenderContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Player arm rendering
 */
@ApiStatus.Internal
@Mixin(value = PlayerEntityRenderer.class, priority = 900000 /* Priority set to inject last so mods' injections are not affected */)
abstract class PlayerEntityRendererMixin {
    // descriptor in a string to avoid repeating outselves.
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
