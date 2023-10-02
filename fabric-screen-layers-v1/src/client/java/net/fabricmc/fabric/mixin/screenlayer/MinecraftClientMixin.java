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

package net.fabricmc.fabric.mixin.screenlayer;

import static net.fabricmc.fabric.impl.client.screenlayer.ScreenLayerManager.SCREENS;
import static org.objectweb.asm.Opcodes.PUTFIELD;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.fabric.impl.client.screenlayer.ScreenLayerManager;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(method = "onResolutionChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;resize(IIZ)V", shift = At.Shift.AFTER))
	public void resizeDisplay(CallbackInfo info) {
		MinecraftClient minecraft = MinecraftClient.getInstance();

		resizeLayers(minecraft.getWindow().getScaledWidth(), minecraft.getWindow().getScaledHeight());
	}

	@Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = PUTFIELD, shift = At.Shift.BEFORE))
	public void setScreen(Screen screen, CallbackInfo info) {
		ScreenLayerManager.clearLayers();
	}

	void resizeLayers(int width, int height) {
		MinecraftClient minecraft = MinecraftClient.getInstance();
		SCREENS.forEach(screen -> screen.resize(minecraft, width, height));
	}
}
