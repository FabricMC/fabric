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

import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.fabricmc.fabric.api.client.event.input.CharEvent;
import net.fabricmc.fabric.api.client.event.input.ClientInputEvents;
import net.fabricmc.fabric.api.client.event.input.KeyEvent;
import net.fabricmc.fabric.api.client.event.input.KeybindEvent;
import net.fabricmc.fabric.api.client.event.input.MouseButtonEvent;
import net.fabricmc.fabric.api.client.event.input.MouseMoveEvent;
import net.fabricmc.fabric.api.client.event.input.MouseScrollEvent;
import net.fabricmc.fabric.impl.client.FabricKeyboardImpl;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Mixin(InputUtil.class)
public class InputUtilMixin {
	@ModifyVariable(method="setKeyboardCallbacks(JLorg/lwjgl/glfw/GLFWKeyCallbackI;Lorg/lwjgl/glfw/GLFWCharModsCallbackI;)V", at=@At("HEAD"), index=2)
	private static GLFWKeyCallbackI changeKeyCb(GLFWKeyCallbackI keyCb) {
		return (window, code, scancode, action, mods) -> {
			FabricKeyboardImpl.INSTANCE.updateMods(mods);
			KeyEvent key = new KeyEvent(code, scancode, action, mods);
			switch (action) {
			case GLFW.GLFW_PRESS:
				ClientInputEvents.KEY_PRESSED.invoker().onKey(key);
				break;
			case GLFW.GLFW_RELEASE:
				ClientInputEvents.KEY_RELEASED.invoker().onKey(key);
				break;
			case GLFW.GLFW_REPEAT:
				ClientInputEvents.KEY_REPEATED.invoker().onKey(key);
				break;
			}
			Map<InputUtil.Key, KeyBinding> keyToBindings = KeyBindingMixin.getKeyToBindings();
			KeyBinding binding = keyToBindings.get(key.getKey());
			if (binding != null) {
				KeybindEvent keybind = new KeybindEvent(code, scancode, action, mods, binding);
				switch (action) {
				case GLFW.GLFW_PRESS:
					ClientInputEvents.KEYBIND_PRESSED.invoker().onKeybind(keybind);
					break;
				case GLFW.GLFW_RELEASE:
					ClientInputEvents.KEYBIND_RELEASED.invoker().onKeybind(keybind);
					break;
				case GLFW.GLFW_REPEAT:
					ClientInputEvents.KEYBIND_REPEATED.invoker().onKeybind(keybind);
					break;
				}
			}
			keyCb.invoke(window, code, scancode, action, mods);
		};
	}
	@ModifyVariable(method="setKeyboardCallbacks(JLorg/lwjgl/glfw/GLFWKeyCallbackI;Lorg/lwjgl/glfw/GLFWCharModsCallbackI;)V", at=@At("HEAD"), index=3)
	private static GLFWCharModsCallbackI changeCharModsCb(GLFWCharModsCallbackI charModsCb) {
		return (window, codepoint, mods) -> {
			FabricKeyboardImpl.INSTANCE.updateMods(mods);
			ClientInputEvents.CHAR_TYPED.invoker().onChar(new CharEvent(codepoint, mods));
			charModsCb.invoke(window, codepoint, mods);
		};
	}

	private static boolean hasMoved = false;
	private static double lastX = 0.0;
	private static double lastY = 0.0;

	@ModifyVariable(method="setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V", at=@At("HEAD"), index=2)
	private static GLFWCursorPosCallbackI changeCursorPosCb(GLFWCursorPosCallbackI cursorPosCb) {
		return (window, x, y) -> {
			double dx = hasMoved ? x - lastX : 0.0;
			double dy = hasMoved ? y - lastY : 0.0;
			ClientInputEvents.MOUSE_MOVED.invoker().onMouseMoved(new MouseMoveEvent(x, y, dx, dy));
			cursorPosCb.invoke(window, x, y);
			lastX = x;
			lastY = y;
			hasMoved = true;
		};
	}
	@ModifyVariable(method="setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V", at=@At("HEAD"), index=3)
	private static GLFWMouseButtonCallbackI changeMouseButtonCb(GLFWMouseButtonCallbackI mouseButtonCb) {
		return (window, button, action, mods) -> {
			FabricKeyboardImpl.INSTANCE.updateMods(mods);
			MouseButtonEvent mouse = new MouseButtonEvent(button, action, mods);
			switch (action) {
			case GLFW.GLFW_PRESS:
				ClientInputEvents.MOUSE_BUTTON_PRESSED.invoker().onMouseButton(mouse);
				break;
			case GLFW.GLFW_RELEASE:
				ClientInputEvents.MOUSE_BUTTON_RELEASED.invoker().onMouseButton(mouse);
				break;
			}
			Map<InputUtil.Key, KeyBinding> keyToBindings = KeyBindingMixin.getKeyToBindings();
			KeyBinding binding = keyToBindings.get(mouse.getKey());
			if (binding != null) {
				KeybindEvent keybind = new KeybindEvent(button, -1, action, mods, binding);
				switch (action) {
				case GLFW.GLFW_PRESS:
					ClientInputEvents.KEYBIND_PRESSED.invoker().onKeybind(keybind);
					break;
				case GLFW.GLFW_RELEASE:
					ClientInputEvents.KEYBIND_RELEASED.invoker().onKeybind(keybind);
					break;
				}
			}
			mouseButtonCb.invoke(window, button, action, mods);
		};
	}
	@ModifyVariable(method="setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V", at=@At("HEAD"), index=4)
	private static GLFWScrollCallbackI changeScrollCb(GLFWScrollCallbackI scrollCb) {
		return (window, dx, dy) -> {
			MouseScrollEvent mouse = new MouseScrollEvent(dx, dy);
			ClientInputEvents.MOUSE_WHEEL_SCROLLED.invoker().onMouseScrolled(mouse);
			scrollCb.invoke(window, dx, dy);
		};
	}
	@ModifyVariable(method="setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V", at=@At("HEAD"), index=5)
	private static GLFWDropCallbackI changeDropCb(GLFWDropCallbackI dropCb) {
		return (window, count, names) -> {
			String[] paths = new String[count];
            for (int i = 0; i < count; ++i) {
                paths[i] = GLFWDropCallback.getName(names, i);
			}
			ClientInputEvents.FILE_DROPPED.invoker().onFilesDropped(paths);
			dropCb.invoke(window, count, names);
		};
	}
}
