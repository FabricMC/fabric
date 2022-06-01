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

package net.fabricmc.fabric.mixin.event.input.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;

import net.fabricmc.fabric.impl.client.input.InputCallbacksImpl;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	// this is the lambda in setup(long) that calls this.onKey(...)
	// this lambda is the actual callback registered with GLFW.glfwSetKeyCallback
	@Inject(method = "method_22678(JIIII)V", at = @At("HEAD"))
	void onKey(long handle, int code, int scancode, int action, int mods, CallbackInfo ci) {
		InputCallbacksImpl.onKey(handle, code, scancode, action, mods);
	}

	// this is the lambda in setup(long) that calls this.onChar(...)
	// this lambda is the actual callback registered with GLFW.glfwSetCharModsCallback
	@Inject(method = "method_22677(JII)V", at = @At("HEAD"))
	void onChar(long handle, int codepoint, int mods, CallbackInfo ci) {
		InputCallbacksImpl.onChar(handle, codepoint, mods);
	}
}
