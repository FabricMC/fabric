package net.fabricmc.fabric.mixin.client.modelevents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.fabricmc.fabric.impl.client.modelevents.ModelRenderContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;

@Mixin(BlockEntityRenderDispatcher.class)
abstract class BlockEntityRenderDispatcherMixin {
    private static final String RUN_REPORTED = "runReported(Lnet/minecraft/block/entity/BlockEntity;Ljava/lang/Runnable;)V";

    @ModifyVariable(method = RUN_REPORTED, at = @At("HEAD"))
    private static Runnable around_render(Runnable runnable, BlockEntity blockEntity) {
        return ModelRenderContext.captureBlockEntity(blockEntity, runnable);
    }
}
