package net.fabricmc.fabric.mixin.client.renderer.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

@Mixin(BlockEntityRenderDispatcher.class)
public interface MixinBlockEntityRenderDispatcherInvoker {
	@Invoker(value = "register")
	@SuppressWarnings("rawtypes")
	void invoke_register(BlockEntityType blockEntityType, BlockEntityRenderer blockEntityRenderer);
}
