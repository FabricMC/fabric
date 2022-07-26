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

package net.fabricmc.fabric.test.rendering.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CrosshairRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.CrosshairRenderHandler;

public class CrosshairRenderingTests implements ClientModInitializer {
	private static final MinecraftClient client = MinecraftClient.getInstance();

	/**
	 * Render a heart instead of the vanilla crosshair if the hit result block is poppy.
	 */
	private static void onCrosshairRender1(CrosshairRenderHandler renderHandler) {
		if (client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
			if (client.world.getBlockState(((BlockHitResult) client.crosshairTarget).getBlockPos()).getBlock() == Blocks.POPPY && Screen.hasControlDown()) {
				renderHandler.render(true, (matrices, scaledWidth, scaledHeight, zOffset) -> {
					DrawableHelper.drawTexture(matrices, (scaledWidth - 7) / 2, (scaledHeight - 7) / 2, zOffset, 53, 1, 7, 7, 256, 256);
				});
			}
		}
	}

	/**
	 * Render hit result block as an item instead of the vanilla crosshair if the block is a flower.
	 */
	private static void onCrosshairRender2(CrosshairRenderHandler renderHandler) {
		if (client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
			BlockHitResult hit = (BlockHitResult) client.crosshairTarget;
			BlockState state = client.world.getBlockState(hit.getBlockPos());

			if (!state.isIn(BlockTags.FLOWERS)) {
				return;
			}

			Item item = state.getBlock().asItem();
			renderHandler.render(true, (matrices, scaledWidth, scaledHeight, zOffset) -> {
				client.getItemRenderer().renderGuiItemIcon(item.getDefaultStack(), (scaledWidth - 16) / 2, (scaledHeight - 16) / 2);
			});
		}
	}

	@Override
	public void onInitializeClient() {
		CrosshairRenderCallback.EVENT.register(CrosshairRenderingTests::onCrosshairRender1);
		CrosshairRenderCallback.EVENT.register(CrosshairRenderingTests::onCrosshairRender2);
	}
}
