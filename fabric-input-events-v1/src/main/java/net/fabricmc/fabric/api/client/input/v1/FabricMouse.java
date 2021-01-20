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

package net.fabricmc.fabric.api.client.input.v1;

import net.fabricmc.fabric.impl.client.input.FabricKeyboardImpl;
import net.fabricmc.fabric.impl.client.input.FabricMouseImpl;

public final class FabricMouse {
	private FabricMouse() {
	}

	/**
	 * Get the current X position of the mouse
	 */
	public static double getX() {
		return FabricMouseImpl.getX();
	}

	/**
	 * Get the current Y position of the mouse
	 */
	public static double getY() {
		return FabricMouseImpl.getY();
	}

	/**
	 * Get a bitmask of the mouse buttons currently being pressed
	 */
	public static int getPressedButtons() {
		return FabricMouseImpl.getPressedButtons();
	}

	/**
	 * Check if the given mosue button is currently being pressed
	 *
	 * @param button the GLFW.GLFW_MOUSE_BUTTON to check
	 */
	public static boolean isButtonPressed(int button) {
		return FabricMouseImpl.isButtonPressed(button);
	}

	/**
	 * Get the GLFW.GLFW_MOD modifier keys currently being pressed
	 */
	public static int getModKeys() {
		return FabricKeyboardImpl.getModKeys();
	}
}
