package net.fabricmc.fabric.test.rendering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EnvironmentRenderers;
import net.fabricmc.fabric.api.client.rendering.v1.FabricSkyPropertyBuilder;

public class EnvironementalRenderingTest implements ClientModInitializer {
	private static final Identifier END_SKY = new Identifier("textures/environment/end_sky.png");

	@Override
	public void onInitializeClient() {
		EnvironmentRenderers.registerSkyProperty(DimensionType.THE_NETHER_REGISTRY_KEY, FabricSkyPropertyBuilder.create().skyType(SkyProperties.SkyType.END).build());
		EnvironmentRenderers.registerSkyProperty(DimensionType.OVERWORLD_REGISTRY_KEY, FabricSkyPropertyBuilder.create().cloudsHeight(64.0f).build());
		EnvironmentRenderers.registerSkyRenderer(World.OVERWORLD, (world, matrices, tickDelta) -> {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.depthMask(false);
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderTexture(0, END_SKY);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();

			for(int i = 0; i < 6; ++i) {
				matrices.push();
				if (i == 1) {
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
				}

				if (i == 2) {
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
				}

				if (i == 3) {
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
				}

				if (i == 4) {
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
				}

				if (i == 5) {
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
				}

				Matrix4f matrix4f = matrices.peek().getModel();
				bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
				bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(40, 40, 40, 255).next();
				bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(40, 40, 40, 255).next();
				bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(40, 40, 40, 255).next();
				bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(40, 40, 40, 255).next();
				tessellator.draw();
				matrices.pop();
			}

			RenderSystem.depthMask(true);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
		});
	}
}
