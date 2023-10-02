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

package net.fabricmc.fabric.impl.client.screenlayer;

import java.util.Objects;
import java.util.Stack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ScreenLayerManager {
	public static final Stack<Screen> SCREENS = new Stack<>();

	/**
	 * Adds a layered screen on top of the current screen.
	 *
	 * @param screen - the screen.
	 */
	public static void pushLayer(Screen screen) {
		MinecraftClient minecraft = MinecraftClient.getInstance();

		if (minecraft.currentScreen != null) {
			SCREENS.push(minecraft.currentScreen);
			minecraft.currentScreen = Objects.requireNonNull(screen);
			screen.init(minecraft, minecraft.getWindow().getScaledWidth(), minecraft.getWindow().getScaledHeight());
			minecraft.getNarratorManager().narrate(screen.getNarratedTitle());
		} else {
			minecraft.setScreen(screen);
		}
	}

	/**
	 * Clears all screen layers.
	 * Called automatically on {@link MinecraftClient#setScreen(Screen)}
	 */
	public static void clearLayers() {
		MinecraftClient minecraft = MinecraftClient.getInstance();

		while (SCREENS.size() > 0) {
			popLayer(minecraft);
		}
	}

	/**
	 * Removes the top most screen.
	 */
	public static void popLayer() {
		MinecraftClient minecraft = MinecraftClient.getInstance();

		if (SCREENS.size() == 0) {
			minecraft.setScreen(null);
			return;
		}

		popLayer(minecraft);

		if (minecraft.currentScreen != null) {
			minecraft.getNarratorManager().narrate(minecraft.currentScreen.getNarratedTitle());
		}
	}

	/**
	 * The Z coordinate for drawing a new screen layer.
	 *
	 * @return - The Z value.
	 */
	public static float getFarPlane() {
		return 1000.0F + 10000.0F * (1 + getScreenLayerCount());
	}

	/**
	 * Returns the current layers on top of the bottom screen. The first screen is not a layer.
	 * This means, if only one screen is displayed it will return 0.
	 *
	 * @return - The count.
	 */
	public static int getScreenLayerCount() {
		return SCREENS.size();
	}

	private static void popLayer(MinecraftClient minecraft) {
		if (minecraft.currentScreen != null) {
			minecraft.currentScreen.removed();
		}

		minecraft.currentScreen = SCREENS.pop();
	}
}
