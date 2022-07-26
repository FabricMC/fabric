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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.math.MatrixStack;

public final class CrosshairRenderHandler {
	private final List<CrosshairRender> toRender = new ArrayList<>();
	private boolean cancelVanilla = false;
	private boolean cancelAll = false;
	private CrosshairRender cancelOthers;

	/**
	 * Adds another {@link CrosshairRender} to render.
	 * Only one {@link CrosshairRender} can cancel vanilla crosshair.
	 * If there is already one canceling it, new one won't be added to the list.
	 * @param cancelsVanilla Whether this is canceling the vanilla crosshair or not.
	 * @param crosshairRender See {@link CrosshairRender}.
	 */
	public void render(boolean cancelsVanilla, CrosshairRender crosshairRender) {
		if (cancelOthers != null) {
			return;
		}

		if (!cancelVanilla && cancelsVanilla) {
			cancelVanilla = true;
		} else if (cancelsVanilla) {
			return;
		}

		toRender.add(crosshairRender);
	}

	/**
	 * Cancels all listeners and only renders the provided {@link CrosshairRender}.
	 * @param cancelsVanilla Whether this is canceling the vanilla crosshair or not.
	 * @param crosshairRender See {@link CrosshairRender}.
	 */
	public void renderCancelOthers(boolean cancelsVanilla, CrosshairRender crosshairRender) {
		if (cancelOthers == null) {
			cancelVanilla = cancelsVanilla;
			cancelOthers = crosshairRender;
		}
	}

	/**
	 * Cancels all rendering.
	 */
	public void cancelAll() {
		cancelAll = true;
	}

	public boolean isVanillaCanceled() {
		return cancelVanilla || cancelAll;
	}

	public void render(MatrixStack matrices, int scaledWidth, int scaledHeight, int zOffset) {
		if (cancelAll) {
			return;
		}

		if (cancelOthers != null) {
			cancelOthers.render(matrices, scaledWidth, scaledHeight, zOffset);
			return;
		}

		toRender.forEach(crosshairRender -> crosshairRender.render(matrices, scaledWidth, scaledHeight, zOffset));
	}

	public interface CrosshairRender {
		/**
		 * @param matrices The MatrixStack
		 * @param scaledWidth Scaled width of the game window.
		 * @param scaledHeight Scaled height of the game window.
		 * @param zOffset The z offset for drawing textures.
		 */
		void render(MatrixStack matrices, int scaledWidth, int scaledHeight, int zOffset);
	}
}
