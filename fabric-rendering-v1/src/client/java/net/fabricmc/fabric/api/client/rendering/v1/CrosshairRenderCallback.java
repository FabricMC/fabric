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

package net.fabricmc.fabric.api.client.rendering.v1;

import net.fabricmc.fabric.api.event.Event;

import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public interface CrosshairRenderCallback {

	Event<CrosshairRenderCallback> EVENT = EventFactory.createArrayBacked(CrosshairRenderCallback.class, listeners -> ((matrices, scaledWidth, scaledHeight, zOffset) -> {
		boolean result = true;
		for (CrosshairRenderCallback callback : listeners) {
			if (!callback.onCrosshairRender(matrices, scaledWidth, scaledHeight, zOffset)) {
				result = false;
			}
		}
		return result;
	}));

	/**
	 * Called before crosshair is rendered.
	 * Crosshair rendering can be canceled if returned {@code false}.
	 *
	 * @param matrices The MatrixStack
	 * @param scaledWidth Scaled width of the game window.
	 * @param scaledHeight Scaled height of the game window.
	 * @param zOffset The z offset
	 * @return {@code false} if crosshair should be disabled, otherwise {@code true}.
	 */
	boolean onCrosshairRender(MatrixStack matrices, int scaledWidth, int scaledHeight, int zOffset);

	default void drawTexture(MatrixStack matrices, int x, int y, int zOffset, int u, int v, int width, int height) {
		DrawableHelper.drawTexture(matrices, x, y, zOffset, u, v, width, height, 256, 256);
	}
}
