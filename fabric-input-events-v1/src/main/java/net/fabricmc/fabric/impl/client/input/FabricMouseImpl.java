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

import java.nio.DoubleBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

import net.fabricmc.fabric.api.client.input.v1.FabricKeyboard;

public final class FabricMouseImpl {
	private static double x = 0.0;
	private static double y = 0.0;
	private static int buttons = 0;
	private static int modKeys = 0;

	private FabricMouseImpl() {
	}

	public static double getX() {
		return FabricMouseImpl.x;
	}

	public static double getY() {
		return FabricMouseImpl.y;
	}

	public static int getPressedButtons() {
		return FabricMouseImpl.buttons;
	}

	public static boolean isButtonPressed(int button) {
		return (FabricMouseImpl.buttons & (1 << button)) != 0;
	}

	public static int getModKeys() {
		return FabricMouseImpl.modKeys;
	}

	public static void update() {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client == null) {
			return;
		}

		Window window = client.getWindow();
		long handle = window.getHandle();

		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer current_x = stack.callocDouble(1);
			DoubleBuffer current_y = stack.callocDouble(1);
			GLFW.glfwGetCursorPos(handle, current_x, current_y);
			FabricMouseImpl.x = current_x.get();
			FabricMouseImpl.y = current_y.get();
		}

		int buttons = 0;
		buttons = checkAndAddButton(handle, buttons, GLFW.GLFW_MOUSE_BUTTON_1);
		buttons = checkAndAddButton(handle, buttons, GLFW.GLFW_MOUSE_BUTTON_2);
		buttons = checkAndAddButton(handle, buttons, GLFW.GLFW_MOUSE_BUTTON_3);
		buttons = checkAndAddButton(handle, buttons, GLFW.GLFW_MOUSE_BUTTON_4);
		buttons = checkAndAddButton(handle, buttons, GLFW.GLFW_MOUSE_BUTTON_5);
		buttons = checkAndAddButton(handle, buttons, GLFW.GLFW_MOUSE_BUTTON_6);
		buttons = checkAndAddButton(handle, buttons, GLFW.GLFW_MOUSE_BUTTON_7);
		buttons = checkAndAddButton(handle, buttons, GLFW.GLFW_MOUSE_BUTTON_8);
		FabricMouseImpl.buttons = buttons;
		FabricMouseImpl.modKeys = FabricKeyboard.getModKeys();
	}

	private static int checkAndAddButton(long handle, int buttons, int button) {
		if (GLFW.glfwGetMouseButton(handle, button) == GLFW.GLFW_PRESS) {
			return buttons | (1 << button);
		}

		return buttons;
	}
}
