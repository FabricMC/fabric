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
