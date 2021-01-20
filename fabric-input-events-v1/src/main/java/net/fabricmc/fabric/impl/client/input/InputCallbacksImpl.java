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

package net.fabricmc.fabric.impl.client.input;

import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import net.fabricmc.fabric.api.event.client.input.CharEvent;
import net.fabricmc.fabric.api.event.client.input.ClientInputEvents;
import net.fabricmc.fabric.api.event.client.input.KeyEvent;
import net.fabricmc.fabric.api.event.client.input.KeybindEvent;
import net.fabricmc.fabric.api.event.client.input.MouseButtonEvent;
import net.fabricmc.fabric.api.event.client.input.MouseMoveEvent;
import net.fabricmc.fabric.api.event.client.input.MouseScrollEvent;
import net.fabricmc.fabric.mixin.event.input.client.KeyBindingMixin;

public final class InputCallbacksImpl {
	public static void onKey(long window, int code, int scancode, int action, int modKeys) {
		FabricKeyboardImpl.INSTANCE.updateModKeys(modKeys);
		FabricMouseImpl.INSTANCE.update();
		KeyEvent key = new KeyEvent(code, scancode, action, modKeys);
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
			KeybindEvent keybind = new KeybindEvent(code, scancode, action, modKeys, binding);
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
	}

	public static void onChar(long window, int codepoint, int modKeys) {
		FabricKeyboardImpl.INSTANCE.updateModKeys(modKeys);
		FabricMouseImpl.INSTANCE.update();
		ClientInputEvents.CHAR_TYPED.invoker().onChar(new CharEvent(codepoint, modKeys));
	}

	private static boolean hasMoved = false;
	private static double lastX = 0.0;
	private static double lastY = 0.0;

	public static void onMouseMoved(long window, double x, double y) {
		FabricMouseImpl.INSTANCE.update();
		double dx = hasMoved ? x - lastX : 0.0;
		double dy = hasMoved ? y - lastY : 0.0;
		ClientInputEvents.MOUSE_MOVED.invoker().onMouseMoved(new MouseMoveEvent(x, y, dx, dy));
		lastX = x;
		lastY = y;
		hasMoved = true;
	}

	public static void onMouseButton(long window, int button, int action, int modKeys) {
		FabricKeyboardImpl.INSTANCE.updateModKeys(modKeys);
		FabricMouseImpl.INSTANCE.update();
		MouseButtonEvent mouse = new MouseButtonEvent(button, action, modKeys);

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
			KeybindEvent keybind = new KeybindEvent(button, -1, action, modKeys, binding);
			switch (action) {
			case GLFW.GLFW_PRESS:
				ClientInputEvents.KEYBIND_PRESSED.invoker().onKeybind(keybind);
				break;
			case GLFW.GLFW_RELEASE:
				ClientInputEvents.KEYBIND_RELEASED.invoker().onKeybind(keybind);
				break;
			}
		}
	}

	public static void onMouseScrolled(long window, double dx, double dy) {
		MouseScrollEvent mouse = new MouseScrollEvent(dx, dy);
		ClientInputEvents.MOUSE_WHEEL_SCROLLED.invoker().onMouseScrolled(mouse);
	}

	public static void onFilesDropped(long window, int count, long names) {
		String[] paths = new String[count];

		for (int i = 0; i < count; ++i) {
			paths[i] = GLFWDropCallback.getName(names, i);
		}

		ClientInputEvents.FILE_DROPPED.invoker().onFilesDropped(paths);
	}
}
