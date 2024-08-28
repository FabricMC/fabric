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
