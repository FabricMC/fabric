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

package net.fabricmc.fabric.test.model.loading;

import java.util.function.Supplier;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class BakedModelFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final Supplier<BakedModel> modelSupplier;

	public BakedModelFeatureRenderer(FeatureRendererContext<T, M> context, Supplier<BakedModel> modelSupplier) {
		super(context);
		this.modelSupplier = modelSupplier;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		BakedModel model = modelSupplier.get();
		VertexConsumer vertices = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());
		matrices.push();
		//matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(headYaw));
		//matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(headPitch));
		matrices.multiply(new Quaternionf(new AxisAngle4f(animationProgress * 0.07F, 0, 1, 0)));
		matrices.scale(-0.75F, -0.75F, 0.75F);
		float aboveHead = (float) (Math.sin(animationProgress * 0.08F)) * 0.5F + 0.5F;
		matrices.translate(-0.5F, 0.75F + aboveHead, -0.5F);
		MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(matrices.peek(), vertices, null, model, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
		matrices.pop();
	}
}
