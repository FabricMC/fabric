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

package net.fabricmc.fabric.impl.client.indigo.renderer.helper;

import java.nio.ByteOrder;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

/**
 * Static routines of general utility for renderer implementations.
 * Renderers are not required to use these helpers, but they were
 * designed to be usable without the default renderer.
 */
public abstract class ColorHelper {
	private ColorHelper() { }

	private static final Int2IntFunction colorSwapper = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? color -> ((color & 0xFF00FF00) | ((color & 0x00FF0000) >> 16) | ((color & 0xFF) << 16)) : color -> color;

	/**
	 * Swaps red blue order if needed to match GPU expectations for color component order.
	 */
	public static int swapRedBlueIfNeeded(int color) {
		return colorSwapper.applyAsInt(color);
	}

	/** Component-wise multiply. Components need to be in same order in both inputs! */
	public static int multiplyColor(int color1, int color2) {
		if (color1 == -1) {
			return color2;
		} else if (color2 == -1) {
			return color1;
		}

		final int alpha = ((color1 >> 24) & 0xFF) * ((color2 >> 24) & 0xFF) / 0xFF;
		final int red = ((color1 >> 16) & 0xFF) * ((color2 >> 16) & 0xFF) / 0xFF;
		final int green = ((color1 >> 8) & 0xFF) * ((color2 >> 8) & 0xFF) / 0xFF;
		final int blue = (color1 & 0xFF) * (color2 & 0xFF) / 0xFF;

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/** Multiplies three lowest components by shade. High byte (usually alpha) unchanged. */
	public static int multiplyRGB(int color, float shade) {
		final int alpha = ((color >> 24) & 0xFF);
		final int red = (int) (((color >> 16) & 0xFF) * shade);
		final int green = (int) (((color >> 8) & 0xFF) * shade);
		final int blue = (int) ((color & 0xFF) * shade);

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
	 * Component-wise max.
	 */
	public static int maxBrightness(int b0, int b1) {
		if (b0 == 0) return b1;
		if (b1 == 0) return b0;

		return Math.max(b0 & 0xFFFF, b1 & 0xFFFF) | Math.max(b0 & 0xFFFF0000, b1 & 0xFFFF0000);
	}
}
