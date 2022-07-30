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
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;

@Mixin(Mouse.class)
abstract class MouseMixin {
	@Shadow
	@Final
	private MinecraftClient client;
	@Unique
	private Screen currentScreen;
	@Unique
	private Double horizontalScrollAmount;

	// private synthetic method_1611([ZDDI)V
	@Inject(method = "method_1611([ZLnet/minecraft/client/gui/screen/Screen;DDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(DDI)Z"), cancellable = true)
	private static void beforeMouseClickedEvent(boolean[] resultHack, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
		@SuppressWarnings("resource")
		MouseMixin thisRef = (MouseMixin) (Object) MinecraftClient.getInstance().mouse;
		// Store the screen in a variable in case someone tries to change the screen during this before event.
		// If someone changes the screen, the after event will likely have class cast exceptions or throw a NPE.
		thisRef.currentScreen = thisRef.client.currentScreen;

		if (thisRef.currentScreen == null) {
			return;
		}

		if (!ScreenMouseEvents.allowMouseClick(thisRef.currentScreen).invoker().allowMouseClick(thisRef.currentScreen, mouseX, mouseY, button)) {
			resultHack[0] = true; // Set this press action as handled.
			thisRef.currentScreen = null;
			ci.cancel(); // Exit the lambda
			return;
		}

		ScreenMouseEvents.beforeMouseClick(thisRef.currentScreen).invoker().beforeMouseClick(thisRef.currentScreen, mouseX, mouseY, button);
	}

	// private synthetic method_1611([ZDDI)V
	@Inject(method = "method_1611([ZLnet/minecraft/client/gui/screen/Screen;DDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(DDI)Z", shift = At.Shift.AFTER))
	private static void afterMouseClickedEvent(boolean[] resultHack, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
		@SuppressWarnings("resource")
		MouseMixin thisRef = (MouseMixin) (Object) MinecraftClient.getInstance().mouse;

		if (thisRef.currentScreen == null) {
			return;
		}

		ScreenMouseEvents.afterMouseClick(thisRef.currentScreen).invoker().afterMouseClick(thisRef.currentScreen, mouseX, mouseY, button);
		thisRef.currentScreen = null;
	}

	// private synthetic method_1605([ZDDI)V
	@Inject(method = "method_1605([ZLnet/minecraft/client/gui/screen/Screen;DDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z"), cancellable = true)
	private static void beforeMouseReleasedEvent(boolean[] resultHack, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
		@SuppressWarnings("resource")
		MouseMixin thisRef = (MouseMixin) (Object) MinecraftClient.getInstance().mouse;

		// Store the screen in a variable in case someone tries to change the screen during this before event.
		// If someone changes the screen, the after event will likely have class cast exceptions or throw a NPE.
		thisRef.currentScreen = thisRef.client.currentScreen;

		if (thisRef.currentScreen == null) {
			return;
		}

		if (!ScreenMouseEvents.allowMouseRelease(thisRef.currentScreen).invoker().allowMouseRelease(thisRef.currentScreen, mouseX, mouseY, button)) {
			resultHack[0] = true; // Set this press action as handled.
			thisRef.currentScreen = null;
			ci.cancel(); // Exit the lambda
			return;
		}

		ScreenMouseEvents.beforeMouseRelease(thisRef.currentScreen).invoker().beforeMouseRelease(thisRef.currentScreen, mouseX, mouseY, button);
	}

	// private synthetic method_1605([ZDDI)V
	@Inject(method = "method_1605([ZLnet/minecraft/client/gui/screen/Screen;DDI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseReleased(DDI)Z", shift = At.Shift.AFTER))
	private static void afterMouseReleasedEvent(boolean[] resultHack, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
		@SuppressWarnings("resource")
		MouseMixin thisRef = (MouseMixin) (Object) MinecraftClient.getInstance().mouse;

		if (thisRef.currentScreen == null) {
			return;
		}

		ScreenMouseEvents.afterMouseRelease(thisRef.currentScreen).invoker().afterMouseRelease(thisRef.currentScreen, mouseX, mouseY, button);
		thisRef.currentScreen = null;
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
	private void beforeMouseScrollEvent(long window, double horizontal, double vertical, CallbackInfo ci, double verticalAmount, double mouseX, double mouseY) {
		// Store the screen in a variable in case someone tries to change the screen during this before event.
		// If someone changes the screen, the after event will likely have class cast exceptions or throw a NPE.
		this.currentScreen = this.client.currentScreen;

		if (this.currentScreen == null) {
			return;
		}

		// Apply same calculations to horizontal scroll as vertical scroll amount has
		this.horizontalScrollAmount = this.client.options.getDiscreteMouseScroll().getValue() ? Math.signum(horizontal) : horizontal * this.client.options.getMouseWheelSensitivity().getValue();

		if (!ScreenMouseEvents.allowMouseScroll(this.currentScreen).invoker().allowMouseScroll(this.currentScreen, mouseX, mouseY, this.horizontalScrollAmount, verticalAmount)) {
			this.currentScreen = null;
			this.horizontalScrollAmount = null;
			ci.cancel();
			return;
		}

		ScreenMouseEvents.beforeMouseScroll(this.currentScreen).invoker().beforeMouseScroll(this.currentScreen, mouseX, mouseY, this.horizontalScrollAmount, verticalAmount);
	}

	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void afterMouseScrollEvent(long window, double horizontal, double vertical, CallbackInfo ci, double verticalAmount, double mouseX, double mouseY) {
		if (this.currentScreen == null) {
			return;
		}

		ScreenMouseEvents.afterMouseScroll(this.currentScreen).invoker().afterMouseScroll(this.currentScreen, mouseX, mouseY, this.horizontalScrollAmount, verticalAmount);
		this.currentScreen = null;
		this.horizontalScrollAmount = null;
	}
}
