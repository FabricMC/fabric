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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

import net.fabricmc.fabric.api.client.input.v1.FabricKeyboard;

public class FabricMouseImpl {
	public static FabricMouseImpl INSTANCE = new FabricMouseImpl();

	private double x = 0.0;
	private double y = 0.0;
	private int buttons = 0;
	private int mods = 0;
	private final DoubleBuffer current_x = DoubleBuffer.allocate(1);
	private final DoubleBuffer current_y = DoubleBuffer.allocate(1);

	private FabricMouseImpl() {
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public int getPressedButtons() {
		return this.buttons;
	}

	public boolean isButtonPressed(int button) {
		return (this.buttons & (1 << button)) != 0;
	}

	public int getMods() {
		return this.mods;
	}

	public void update() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null) {
			return;
		}
		Window window = client.getWindow();
		long handle = window.getHandle();
		GLFW.glfwGetCursorPos(handle, current_x, current_y);
		this.x = current_x.get();
		this.y = current_y.get();
		this.buttons = 0;
		this.buttons = checkAndAddButton(handle, this.buttons, GLFW.GLFW_MOUSE_BUTTON_1);
		this.buttons = checkAndAddButton(handle, this.buttons, GLFW.GLFW_MOUSE_BUTTON_2);
		this.buttons = checkAndAddButton(handle, this.buttons, GLFW.GLFW_MOUSE_BUTTON_3);
		this.buttons = checkAndAddButton(handle, this.buttons, GLFW.GLFW_MOUSE_BUTTON_4);
		this.buttons = checkAndAddButton(handle, this.buttons, GLFW.GLFW_MOUSE_BUTTON_5);
		this.buttons = checkAndAddButton(handle, this.buttons, GLFW.GLFW_MOUSE_BUTTON_6);
		this.buttons = checkAndAddButton(handle, this.buttons, GLFW.GLFW_MOUSE_BUTTON_7);
		this.buttons = checkAndAddButton(handle, this.buttons, GLFW.GLFW_MOUSE_BUTTON_8);
		this.mods = FabricKeyboard.getMods();
	}

	private int checkAndAddButton(long handle, int buttons, int button) {
		if (GLFW.glfwGetMouseButton(handle, button) == GLFW.GLFW_PRESS) {
			return this.buttons | (1 << button);
		}
		return this.buttons;
	}
}
