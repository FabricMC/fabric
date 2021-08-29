package net.fabricmc.fabric.mixin.client.rendering.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.CustomFluidRenderer;
import net.fabricmc.fabric.impl.client.rendering.fluid.DefaultFluidRenderer;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderRegistryImpl;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockRenderManager.class)
public class MixinBlockRenderManager {
	@Shadow
	@Final
	private FluidRenderer fluidRenderer;

	@Redirect(
		method = "renderFluid",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/block/FluidRenderer;" +
						 "render(" +
						 "Lnet/minecraft/world/BlockRenderView;" +
						 "Lnet/minecraft/util/math/BlockPos;" +
						 "Lnet/minecraft/client/render/VertexConsumer;" +
						 "Lnet/minecraft/fluid/FluidState;" +
						 ")Z")
	)
	private boolean redirectRenderFluid(FluidRenderer fluidRenderer, BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, FluidState state) {
		DefaultFluidRenderer.INSTANCE.setFluidRenderer(fluidRenderer);

		CustomFluidRenderer renderer = FluidRenderRegistryImpl.INSTANCE.getCustomRendererOrNull(state.getFluid());
		if (renderer != null) {
			return renderer.renderFluid(pos, world, vertexConsumer, state);
		}

		return fluidRenderer.render(world, pos, vertexConsumer, state);
	}
}
