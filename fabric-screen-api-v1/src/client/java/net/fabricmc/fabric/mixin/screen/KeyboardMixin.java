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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;

@Mixin(Keyboard.class)
abstract class KeyboardMixin {
	// private synthetic method_1454(ILnet/minecraft/client/gui/screen/Screen;[ZIII)V
	@Inject(method = "method_1454(ILnet/minecraft/client/gui/screen/Screen;[ZIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(III)Z"), cancellable = true)
	private void beforeKeyPressedEvent(int code, Screen screen, boolean[] resultHack, int key, int scancode, int modifiers, CallbackInfo ci) {
		if (!ScreenKeyboardEvents.allowKeyPress(screen).invoker().allowKeyPress(screen, key, scancode, modifiers)) {
			resultHack[0] = true; // Set this press action as handled.
			ci.cancel(); // Exit the lambda
			return;
		}

		ScreenKeyboardEvents.beforeKeyPress(screen).invoker().beforeKeyPress(screen, key, scancode, modifiers);
	}

	// private synthetic method_1454(ILnet/minecraft/client/gui/screen/Screen;[ZIII)V
	@Inject(method = "method_1454(ILnet/minecraft/client/gui/screen/Screen;[ZIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyPressed(III)Z", shift = At.Shift.AFTER))
	private void afterKeyPressedEvent(int code, Screen screen, boolean[] resultHack, int key, int scancode, int modifiers, CallbackInfo ci) {
		ScreenKeyboardEvents.afterKeyPress(screen).invoker().afterKeyPress(screen, key, scancode, modifiers);
	}

	// private synthetic method_1454(ILnet/minecraft/client/gui/screen/Screen;[ZIII)V
	@Inject(method = "method_1454(ILnet/minecraft/client/gui/screen/Screen;[ZIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyReleased(III)Z"), cancellable = true)
	private void beforeKeyReleasedEvent(int code, Screen screen, boolean[] resultHack, int key, int scancode, int modifiers, CallbackInfo ci) {
		if (!ScreenKeyboardEvents.allowKeyRelease(screen).invoker().allowKeyRelease(screen, key, scancode, modifiers)) {
			resultHack[0] = true; // Set this press action as handled.
			ci.cancel(); // Exit the lambda
			return;
		}

		ScreenKeyboardEvents.beforeKeyRelease(screen).invoker().beforeKeyRelease(screen, key, scancode, modifiers);
	}

	// private synthetic method_1454(ILnet/minecraft/client/gui/screen/Screen;[ZIII)V
	@Inject(method = "method_1454(ILnet/minecraft/client/gui/screen/Screen;[ZIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;keyReleased(III)Z", shift = At.Shift.AFTER))
	private void afterKeyReleasedEvent(int code, Screen screen, boolean[] resultHack, int key, int scancode, int modifiers, CallbackInfo ci) {
		ScreenKeyboardEvents.afterKeyRelease(screen).invoker().afterKeyRelease(screen, key, scancode, modifiers);
	}
}
