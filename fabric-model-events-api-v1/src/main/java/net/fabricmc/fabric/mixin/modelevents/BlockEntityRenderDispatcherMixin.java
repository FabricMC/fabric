package net.fabricmc.fabric.mixin.modelevents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.modelevents.ModelRenderContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(BlockEntityRenderDispatcher.class)
abstract class BlockEntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private static <T extends BlockEntity> void before_render(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        ModelRenderContext.CURRENT_BLOCK_ENTITY.push(blockEntity);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private static <T extends BlockEntity> void after_render(BlockEntityRenderer<T> renderer, T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        if (!ModelRenderContext.CURRENT_BLOCK_ENTITY.isEmpty()) {
            ModelRenderContext.CURRENT_BLOCK_ENTITY.pop();
        }
    }
}
