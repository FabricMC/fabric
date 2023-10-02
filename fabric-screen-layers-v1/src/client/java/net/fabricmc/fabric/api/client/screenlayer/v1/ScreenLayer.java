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

package net.fabricmc.fabric.api.client.screenlayer.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.fabric.impl.client.screenlayer.ScreenLayerManager;

public class ScreenLayer {
	/**
	 * Adds a layered screen on top of the current screen.
	 *
	 * @param screen - the screen.
	 */
	public static void push(Screen screen) {
		ScreenLayerManager.pushLayer(screen);
	}

	/**
	 * Removes the top most screen.
	 */
	public static void pop() {
		ScreenLayerManager.popLayer();
	}

	/**
	 * Clears all screen layers.
	 * Called automatically on {@link MinecraftClient#setScreen(Screen)}
	 */
	public static void clear() {
		ScreenLayerManager.clearLayers();
	}

	/**
	 * The Z coordinate for drawing a new screen layer.
	 *
	 * @return - The Z value.
	 */
	public static float getFarPlane() {
		return ScreenLayerManager.getFarPlane();
	}

	/**
	 * Returns the current layers on top of the bottom screen. The first screen is not a layer.
	 * This means, if only one screen is displayed it will return 0.
	 *
	 * @return - The count.
	 */
	public static int getScreenLayerCount() {
		return ScreenLayerManager.getScreenLayerCount();
	}
}
