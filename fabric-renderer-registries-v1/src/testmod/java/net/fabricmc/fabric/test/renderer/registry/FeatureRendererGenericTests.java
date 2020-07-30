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

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityFeatureRendererRegistrationCallback;

/**
 * This test exists solely for testing generics.
 * As such it is not in the mod json
 */
public class FeatureRendererGenericTests implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// These aren't tests in the normal sense. These exist to test that generics are sane.
		EntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer) -> {
			final ImmutableList.Builder<FeatureRenderer<?, ?>> builder = ImmutableList.builder();

			if (entityRenderer instanceof PlayerEntityRenderer) {
				builder.add(new TestPlayerFeature((PlayerEntityRenderer) entityRenderer));

				// This is T extends AbstractClientPlayerEntity
				builder.add(new GenericTestPlayerFeature<>((PlayerEntityRenderer) entityRenderer));
			}

			if (entityRenderer instanceof ArmorStandEntityRenderer) {
				builder.add(new TestArmorStandFeature((ArmorStandEntityRenderer) entityRenderer));
			}

			// Obviously not recommended, just used for testing generics
			builder.add(new ElytraFeatureRenderer<>(entityRenderer));

			if (entityRenderer instanceof BipedEntityRenderer) {
				// It works, method ref is encouraged
				builder.add(new HeldItemFeatureRenderer<>((BipedEntityRenderer<?, ?>) entityRenderer));
			}

			return builder.build();
		});

		EntityFeatureRendererRegistrationCallback.EVENT.register(this::registerFeatures);
	}

	private List<FeatureRenderer<?, ?>> registerFeatures(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?> entityRenderer) {
		final ImmutableList.Builder<FeatureRenderer<?, ?>> builder = ImmutableList.builder();

		if (entityRenderer instanceof PlayerEntityRenderer) {
			builder.add(new TestPlayerFeature((PlayerEntityRenderer) entityRenderer));

			// This is T extends AbstractClientPlayerEntity
			builder.add(new GenericTestPlayerFeature<>((PlayerEntityRenderer) entityRenderer));
		}

		if (entityRenderer instanceof ArmorStandEntityRenderer) {
			builder.add(new TestArmorStandFeature((ArmorStandEntityRenderer) entityRenderer));
		}

		// Obviously not recommended, just used for testing generics.
		builder.add(new ElytraFeatureRenderer<>(entityRenderer));

		if (entityRenderer instanceof BipedEntityRenderer) {
			// It works, method ref is encouraged
			builder.add(new HeldItemFeatureRenderer<>((BipedEntityRenderer<?, ?>) entityRenderer));
		}

		return builder.build();
	}

	static class TestPlayerFeature extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
		TestPlayerFeature(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
			super(context);
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		}
	}

	static class GenericTestPlayerFeature<T extends AbstractClientPlayerEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M> {
		GenericTestPlayerFeature(FeatureRendererContext<T, M> context) {
			super(context);
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		}
	}

	static class TestArmorStandFeature extends FeatureRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> {
		TestArmorStandFeature(FeatureRendererContext<ArmorStandEntity, ArmorStandArmorEntityModel> context) {
			super(context);
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorStandEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		}
	}
}
