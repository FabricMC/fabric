package net.fabricmc.fabric.test.client.rendering.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public class ColoredFluidRenderer extends SimpleFluidRenderHandler {
	private final int tint;

	public ColoredFluidRenderer(Identifier stillTexture, Identifier flowingTexture, Identifier overlayTexture, int tint) {
		super(stillTexture, flowingTexture, overlayTexture);
		this.tint = tint;
	}

	public ColoredFluidRenderer(Identifier stillTexture, Identifier flowingTexture, int tint) {
		super(stillTexture, flowingTexture);
		this.tint = tint;
	}

	@Override
	public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return tint;
	}
}
