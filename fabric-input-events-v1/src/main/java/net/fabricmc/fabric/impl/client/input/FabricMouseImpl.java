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

public final class FabricMouseImpl {
	private static double x = 0.0;
	private static double y = 0.0;
	private static int buttons = 0;

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

	public static void updatePosition(double x, double y) {
		FabricMouseImpl.x = x;
		FabricMouseImpl.y = y;
	}

	public static void updateButton(int button, boolean pressed) {
		if (pressed) {
			FabricMouseImpl.buttons |= (1 << button);
		} else {
			FabricMouseImpl.buttons &= ~(1 << button);
		}
	}

	public static void queryPosition() {
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
	}
}
