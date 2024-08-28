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

package net.fabricmc.fabric.test.attachment.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;

import net.fabricmc.fabric.test.attachment.AttachmentTestMod;

public class AttachmentDebugFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
	public AttachmentDebugFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context) {
		super(context);
	}

	private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Position pos, int line, String string, int color) {
		BlockPos blockPos = BlockPos.ofFloored(pos);
		DebugRenderer.drawString(
				matrices,
				vertexConsumers,
				string,
				(double) blockPos.getX() + 0.5,
				pos.getY() + 2.4 + (double) line * 0.25,
				(double) blockPos.getZ() + 0.5,
				color,
				0.02F,
				false,
				0.5F,
				true
		);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		int i = 0;

		boolean attAll = player.getAttachedOrCreate(AttachmentTestMod.SYNCED_WITH_ALL);
		drawString(
				matrices,
				vertexConsumers,
				player.getPos(),
				i++,
				"Synced-with-all attachment: " + attAll,
				attAll ? Colors.GREEN : Colors.WHITE
		);

		boolean attTarget = player.getAttachedOrCreate(AttachmentTestMod.SYNCED_WITH_TARGET);
		drawString(
				matrices,
				vertexConsumers,
				player.getPos(),
				i++,
				"Synced-with-target attachment: " + attTarget,
				attTarget ? player == MinecraftClient.getInstance().player ? Colors.GREEN : Colors.RED : Colors.WHITE
		);

		boolean attOther = player.getAttachedOrCreate(AttachmentTestMod.SYNCED_EXCEPT_TARGET);
		drawString(
				matrices,
				vertexConsumers,
				player.getPos(),
				i++,
				"Synced-with-non-targets attachment: " + attOther,
				attOther ? player != MinecraftClient.getInstance().player ? Colors.GREEN : Colors.RED : Colors.WHITE
		);

		boolean attCustom = player.getAttachedOrCreate(AttachmentTestMod.SYNCED_CUSTOM_RULE);
		drawString(
				matrices,
				vertexConsumers,
				player.getPos(),
				i++,
				"Synced-with-creative attachment: " + attCustom,
				attCustom ? player.isCreative() ? Colors.GREEN : Colors.RED : Colors.WHITE
		);
	}
}
