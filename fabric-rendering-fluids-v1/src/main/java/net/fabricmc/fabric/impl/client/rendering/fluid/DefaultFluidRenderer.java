package net.fabricmc.fabric.impl.client.rendering.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.CustomFluidRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import java.util.concurrent.locks.ReentrantLock;

public class DefaultFluidRenderer implements CustomFluidRenderer {
	public static final DefaultFluidRenderer INSTANCE = new DefaultFluidRenderer();

	// Lazily loaded because FluidRenderer might not yet exist at instantiation time
	private FluidRenderer defaultRenderer;

	// Rendering is multithreaded, we lock this so we have only one thread attempting to load the fluid renderer at a time
	// Only used when lazily loading the default renderer, after that no blocking is performed
	private final ReentrantLock lazyLoadLock = new ReentrantLock();

	// Called from Mixin
	public void setFluidRenderer(FluidRenderer renderer) {
		if (defaultRenderer == null) {
			try {
				lazyLoadLock.lock();
				if (defaultRenderer == null) { // Must check again, other thread might have set it already
					defaultRenderer = renderer;
				}
			} finally {
				lazyLoadLock.unlock();
			}
		}
	}

	@Override
	public boolean renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, FluidState state) {
		return defaultRenderer.render(world, pos, vertexConsumer, state);
	}
}
