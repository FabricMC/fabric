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

package net.fabricmc.fabric.test.object.builder.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.test.object.builder.TrackStackEntity;

public class TrackStackEntityRenderer extends MobEntityRenderer<TrackStackEntity, ChickenEntityModel<TrackStackEntity>> {
	private static final Identifier TEXTURE = new Identifier("missingno");

	public TrackStackEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ChickenEntityModel<>(context.getPart(EntityModelLayers.CHICKEN)), 0.3f);
	}

	@Override
	public void render(TrackStackEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

		matrices.push();
		matrices.translate(0, -2, 0);

		for (Text line : entity.getLabelLines()) {
			this.renderLabelIfPresent(entity, line, matrices, vertexConsumers, light, tickDelta);
			matrices.translate(0, 0.25875f, 0);
		}

		matrices.pop();
	}

	@Override
	public Identifier getTexture(TrackStackEntity entity) {
		return TEXTURE;
	}
}
