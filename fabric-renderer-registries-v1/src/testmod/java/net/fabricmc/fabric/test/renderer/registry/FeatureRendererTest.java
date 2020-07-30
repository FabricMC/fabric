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

package net.fabricmc.fabric.test.renderer.registry;

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
import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;

public final class FeatureRendererTest implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(FeatureRendererTest.class);
	private int playerRegistrations = 0;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Registering test feature renderer");
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper) -> {
			// minecraft:player SHOULD be printed twice
			LOGGER.info(String.format("Received registration for %s", Registry.ENTITY_TYPE.getId(entityType)));

			if (entityType == EntityType.PLAYER) {
				this.playerRegistrations++;
			}

			if (entityRenderer instanceof PlayerEntityRenderer) {
				registrationHelper.register(new TestPlayerFeatureRenderer((PlayerEntityRenderer) entityRenderer));
			}
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
