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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.fabric.api.client.screen.v1.ScreenContext;
import net.fabricmc.fabric.api.client.screen.v1.ScreenRenderCallback;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	// This injection should end up in the try block so exceptions are caught
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", shift = At.Shift.AFTER), locals = LocalCapture.PRINT)
	private void onRenderScreen(float tickDelta, long startTime, boolean tick, CallbackInfo ci, int mouseX, int mouseY, MatrixStack matrices) {
		ScreenRenderCallback.EVENT.invoker().onRender(this.client, matrices, this.client.currentScreen, (ScreenContext) this.client.currentScreen, mouseX, mouseY, this.client.getLastFrameDuration());
	}
}
