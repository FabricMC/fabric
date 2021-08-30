package net.fabricmc.fabric.test.client.rendering.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class CustomizedFluidRenderer extends SimpleFluidRenderHandler {
	public CustomizedFluidRenderer(Identifier overlayTexture) {
		super(null, null, overlayTexture);
	}

	@Override
	public boolean renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, FluidState state) {
		int light = getLight(world, pos);
		float u1 = sprites[2].getFrameU(0);
		float v1 = sprites[2].getFrameV(0);
		float u2 = sprites[2].getFrameU(16);
		float v2 = sprites[2].getFrameV(16 * state.getHeight(world, pos));

		float x1 = (pos.getX() & 15) + 0.1f;
		float y1 = pos.getY() & 15;
		float z1 = (pos.getZ() & 15) + 0.1f;

		float x2 = (pos.getX() & 15) + 0.9f;
		float y2 = (pos.getY() & 15) + state.getHeight(world, pos);
		float z2 = (pos.getZ() & 15) + 0.9f;

		vertex(vertexConsumer, x1, y1, z1, 1, 1, 1, u1, v1, light);
		vertex(vertexConsumer, x2, y1, z2, 1, 1, 1, u2, v1, light);
		vertex(vertexConsumer, x2, y2, z2, 1, 1, 1, u2, v2, light);
		vertex(vertexConsumer, x1, y2, z1, 1, 1, 1, u1, v2, light);

		vertex(vertexConsumer, x1, y2, z1, 1, 1, 1, u1, v2, light);
		vertex(vertexConsumer, x2, y2, z2, 1, 1, 1, u2, v2, light);
		vertex(vertexConsumer, x2, y1, z2, 1, 1, 1, u2, v1, light);
		vertex(vertexConsumer, x1, y1, z1, 1, 1, 1, u1, v1, light);

		vertex(vertexConsumer, x1, y2, z2, 1, 1, 1, u1, v2, light);
		vertex(vertexConsumer, x2, y2, z1, 1, 1, 1, u2, v2, light);
		vertex(vertexConsumer, x2, y1, z1, 1, 1, 1, u2, v1, light);
		vertex(vertexConsumer, x1, y1, z2, 1, 1, 1, u1, v1, light);

		vertex(vertexConsumer, x1, y1, z2, 1, 1, 1, u1, v1, light);
		vertex(vertexConsumer, x2, y1, z1, 1, 1, 1, u2, v1, light);
		vertex(vertexConsumer, x2, y2, z1, 1, 1, 1, u2, v2, light);
		vertex(vertexConsumer, x1, y2, z2, 1, 1, 1, u1, v2, light);

		return true;
	}

	private void vertex(VertexConsumer vertexConsumer, double x, double y, double z, float red, float green, float blue, float u, float v, int light) {
		vertexConsumer.vertex(x, y, z).color(red, green, blue, 1.0F).texture(u, v).light(light).normal(0.0F, 1.0F, 0.0F).next();
	}

	private int getLight(BlockRenderView world, BlockPos pos) {
		int i = WorldRenderer.getLightmapCoordinates(world, pos);
		int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
		int k = i & 255;
		int l = j & 255;
		int m = i >> 16 & 255;
		int n = j >> 16 & 255;
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}
}
