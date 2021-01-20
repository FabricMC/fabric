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

import net.minecraft.client.Mouse;

import net.fabricmc.fabric.impl.client.input.InputCallbacksImpl;

@Mixin(Mouse.class)
public class MouseMixin {
	@Inject(method = "method_22689(JDD)V", at = @At("HEAD"))
	void onMouseMove(long window, double dx, double dy, CallbackInfo ci) {
		InputCallbacksImpl.onMouseMoved(window, dx, dy);
	}

	@Inject(method = "method_22686(JIII)V", at = @At("HEAD"))
	void onMouseButton(long window, int button, int action, int modKeys, CallbackInfo ci) {
		InputCallbacksImpl.onMouseButton(window, button, action, modKeys);
	}

	@Inject(method = "method_22687(JDD)V", at = @At("HEAD"))
	void onMouseScrolled(long window, double dx, double dy, CallbackInfo ci) {
		InputCallbacksImpl.onMouseScrolled(window, dx, dy);
	}

	@Inject(method = "method_29615(JIJ)V", at = @At("HEAD"))
	void onFilesDropped(long window, int count, long names, CallbackInfo ci) {
		InputCallbacksImpl.onFilesDropped(window, count, names);
	}
}
