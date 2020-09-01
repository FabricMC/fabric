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

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ParentElement;

import net.fabricmc.fabric.api.client.screen.v1.ScreenExtensions;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "method_1454(I[ZLnet/minecraft/client/gui/ParentElement;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyPressed(III)Z"), cancellable = true)
	private void beforeKeyPressedEvent(int code, boolean[] resultHack, ParentElement parentElement, int key, int scancode, int modifiers, CallbackInfo ci) {
		final ScreenExtensions screen = (ScreenExtensions) parentElement;

		if (screen.getKeyboardEvents().getBeforeKeyPressedEvent().invoker().beforeKeyPress(this.client, screen.getScreen(), screen, key, scancode, modifiers)) {
			resultHack[0] = true; // Set this press action as handled.
			ci.cancel(); // Exit the lambda
		}
	}

	@Inject(method = "method_1454(I[ZLnet/minecraft/client/gui/ParentElement;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyPressed(III)Z", shift = At.Shift.AFTER))
	private void afterKeyPressedEvent(int code, boolean[] resultHack, ParentElement parentElement, int key, int scancode, int modifiers, CallbackInfo ci) {
		final ScreenExtensions screen = (ScreenExtensions) parentElement;
		screen.getKeyboardEvents().getAfterKeyPressedEvent().invoker().afterKeyPress(this.client, screen.getScreen(), screen, key, scancode, modifiers);
	}

	@Inject(method = "method_1454(I[ZLnet/minecraft/client/gui/ParentElement;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyReleased(III)Z"), cancellable = true)
	private void beforeKeyReleasedEvent(int code, boolean[] resultHack, ParentElement parentElement, int key, int scancode, int modifiers, CallbackInfo ci) {
		final ScreenExtensions screen = (ScreenExtensions) parentElement;

		if (screen.getKeyboardEvents().getBeforeKeyReleasedEvent().invoker().beforeKeyReleased(this.client, screen.getScreen(), screen, key, scancode, modifiers)) {
			resultHack[0] = true; // Set this press action as handled.
			ci.cancel(); // Exit the lambda
		}
	}

	@Inject(method = "method_1454(I[ZLnet/minecraft/client/gui/ParentElement;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ParentElement;keyReleased(III)Z", shift = At.Shift.AFTER))
	private void afterKeyReleasedEvent(int code, boolean[] resultHack, ParentElement parentElement, int key, int scancode, int modifiers, CallbackInfo ci) {
		final ScreenExtensions screen = (ScreenExtensions) parentElement;
		screen.getKeyboardEvents().getAfterKeyReleasedEvent().invoker().afterKeyReleased(this.client, screen.getScreen(), screen, key, scancode, modifiers);
	}
}
