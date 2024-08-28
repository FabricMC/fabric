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
import net.minecraft.client.render.entity.PlayerEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.test.attachment.AttachmentTestMod;

public class AttachmentTestModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Various test renderers to display attachments clientside
		HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
			if (MinecraftClient.getInstance().player.getAttachedOrCreate(AttachmentTestMod.SYNCED_WITH_TARGET)) {
				drawContext.fillGradient(10, 10, 60, 60, 0xFFFF0000, 0xFF0000FF);
			}
		});

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
				registrationHelper.register(new AttachmentDebugFeatureRenderer<>(playerRenderer));
			}
		});
	}
}
