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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.fabricmc.fabric.impl.client.modelevents.ModelRenderContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;

/**
 * Common injection point for rendering a block entity
 */
@ApiStatus.Internal
@Mixin(value = BlockEntityRenderDispatcher.class, priority = 900000 /* Priority set to inject last so mods' injections are not affected */)
abstract class BlockEntityRenderDispatcherMixin {
    @ModifyVariable(
            method = "runReported(Lnet/minecraft/block/entity/BlockEntity;Ljava/lang/Runnable;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static Runnable captureBlockEntityRendererForModelPartEvents(Runnable runnable, BlockEntity blockEntity) {
        return ModelRenderContext.captureBlockEntity(blockEntity, runnable);
    }
}
