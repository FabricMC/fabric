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

package net.fabricmc.fabric.mixin.screen;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Unique
	private Screen renderingScreen;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onBeforeRenderScreen(float tickDelta, long startTime, boolean tick, CallbackInfo ci, int mouseX, int mouseY, MatrixStack matrixStack, DrawContext drawContext) {
		// Store the screen in a variable in case someone tries to change the screen during this before render event.
		// If someone changes the screen, the after render event will likely have class cast exceptions or an NPE.
		this.renderingScreen = this.client.currentScreen;
		ScreenEvents.beforeRender(this.renderingScreen).invoker().beforeRender(this.renderingScreen, drawContext, mouseX, mouseY, tickDelta);
	}

	// This injection should end up in the try block so exceptions are caught
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onAfterRenderScreen(float tickDelta, long startTime, boolean tick, CallbackInfo ci, int mouseX, int mouseY, MatrixStack matrixStack, DrawContext drawContext) {
		ScreenEvents.afterRender(this.renderingScreen).invoker().afterRender(this.renderingScreen, drawContext, mouseX, mouseY, tickDelta);
		// Finally set the currently rendering screen to null
		this.renderingScreen = null;
	}
}
