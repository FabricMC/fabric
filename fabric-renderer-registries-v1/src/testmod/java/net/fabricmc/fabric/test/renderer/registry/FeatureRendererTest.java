package net.fabricmc.fabric.test.renderer.registry;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityFeatureRendererRegistrationCallback;

public final class FeatureRendererTest implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(FeatureRendererTest.class);
	private int playerRegistrations = 0;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Registering test feature renderer");
		EntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer) -> {
			// minecraft:player SHOULD be printed twice
			LOGGER.info(String.format("Received registration for %s", Registry.ENTITY_TYPE.getId(entityType)));

			if (entityType == EntityType.PLAYER) {
				this.playerRegistrations++;
			}

			final ImmutableList.Builder<FeatureRenderer<?, ?>> builder = ImmutableList.builder();

			if (entityRenderer instanceof PlayerEntityRenderer) {
				builder.add(new TestPlayerFeatureRenderer((PlayerEntityRenderer) entityRenderer));
			}

			return builder.build();
		});

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			if (this.playerRegistrations != 2) {
				throw new AssertionError(String.format("Expected 2 entity feature renderer registration events for \"minecraft:player\" but received %s registrations", this.playerRegistrations));
			}

			LOGGER.info("Successfully called feature renderer registration events");
		});
	}

	private static class TestPlayerFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
		TestPlayerFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
			super(context);
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
			matrices.push();

			// Translate to center above the player's head
			matrices.translate(-0.5F, -entity.getHeight() + 0.25F, -0.5F);
			// Render a diamond block above the player's head
			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(Blocks.DIAMOND_BLOCK.getDefaultState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

			matrices.pop();
		}
	}
}
