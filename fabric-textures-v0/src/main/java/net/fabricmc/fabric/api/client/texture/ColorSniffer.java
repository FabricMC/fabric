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

package net.fabricmc.fabric.api.client.texture;

import net.fabricmc.fabric.mixin.client.texture.SpriteAccessor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;

import java.awt.*;

/**
 * Utility class for averaging Sprite colors, or using the provided Accessor
 * through {@linkplain ColorSniffer#getImagesForSprite(Sprite)}.
 */
public final class ColorSniffer {
	private static final int errorColor = 0xFFFF00FF;

	private ColorSniffer() { }

	/**
	 * Routing method for calculating the average color of a Sprite.
	 *
	 * <p>Assumes the provided Sprite only has one frame.
	 * Otherwise, {@see ColorSniffer#getAverageSpriteColorForFrame(Sprite, int, AveragingMethod)}
	 *
	 * @param sprite The Sprite you want to get the average color from.
	 * @param averagingMethod The method used to calculate the returned color.
	 * @return The average ARGB color of the provided Sprite. Returns {@linkplain ColorSniffer#errorColor} if something goes wrong.
	 */
	public static int getAverageSpriteColor(Sprite sprite, AveragingMethod averagingMethod) {
		if (sprite == null){
			return errorColor;
		}
		switch (averagingMethod){
			case RGB:
				return calculateAverageRGBSpriteColor(sprite, 0);
			case HSV:
				return calculateAverageHSBSpriteColor(sprite, 0);
			default:
				return errorColor;
		}
	}

	/**
	 * Routing method for calculating the average color of a Sprite.
	 *
	 * <p>{@see Sprite#getFrameCount()}
	 *
	 * @param sprite The Sprite you want to get the average color from.
	 * @param frame The specific frame of the sprite, in case it has more than one.
	 * @param averagingMethod The method used to calculate the returned color.
	 * @return The average ARGB color of the provided Sprite. Returns {@linkplain ColorSniffer#errorColor} if something goes wrong.
	 */
	public static int getAverageSpriteColorForFrame(Sprite sprite, int frame, AveragingMethod averagingMethod){
		if (sprite == null){
			return errorColor;
		}
		switch (averagingMethod){
			case RGB:
				return calculateAverageRGBSpriteColor(sprite, frame);
			case HSV:
				return calculateAverageHSBSpriteColor(sprite, frame);
			default:
				return errorColor;
		}
	}

	/**
	 * Attempts to calculate the average color of a Sprite in RGB.
	 *
	 * <p>It is generally unadvisable to use this for sprites with a lot of
	 * colors, as RGB does not care for our expectations of what the color
	 * *should* be. Use {@linkplain ColorSniffer#calculateAverageHSBSpriteColor(Sprite, int)}
	 * if you want visual accuracy.
	 *
	 * @param sprite The Sprite you want to get the average color from.
	 * @param frame The specific frame of the sprite, in case it has more than one.
	 * @return The average ARGB color of the provided Sprite, during the provided frame.
	 */
	private static int calculateAverageRGBSpriteColor(Sprite sprite, int frame) {

		NativeImage image = getImagesForSprite(sprite)[frame];
		int pixelCount = 0;
		long avgR = 0, avgG = 0, avgB = 0;
		for (int y = 0; y < sprite.getHeight(); y++) {
			for (int x = 0; x < sprite.getWidth(); x++) {
				int c = image.getPixelRgba(x, y);
				if (((c >> 24) & 0xFF) != 0x00) {
					avgB += ((c >> 16) & 0xFF) * ((c >> 16) & 0xFF);
					avgG += ((c >> 8) & 0xFF) * ((c >> 8) & 0xFF);
					avgR += (c & 0xFF) * (c & 0xFF);
					pixelCount++;
				}
			}
		}
		if (pixelCount > 0) {
			return 0xFF000000
				| ((Math.min(255, (int) (Math.sqrt(avgR / pixelCount))) & 0xFF) << 16)
				| ((Math.min(255, (int) (Math.sqrt(avgG / pixelCount))) & 0xFF) << 8)
				| ((Math.min(255, (int) (Math.sqrt(avgB / pixelCount))) & 0xFF));
		} else {
			return errorColor;
		}
	}

	/**
	 * Attempts to calculate the average color of a Sprite in HSB.
	 *
	 * <p>Marginally less performant than the RGB method, but results in
	 * a color actually legible by an average human person.
	 *
	 * <p>DO NOT use this on excessively large sprites.
	 *
	 * @param sprite The Sprite you want to get the average color from.
	 * @param frame The specific frame of the sprite, in case it has more than one.
	 * @return The average ARGB color of the provided Sprite, during the provided frame.
	 */
	private static int calculateAverageHSBSpriteColor(Sprite sprite, int frame) {
		NativeImage image = getImagesForSprite(sprite)[frame];
		double hX = 0.0;
		double hY = 0.0;
		float s = 0f;
		float b = 0f;
		int pixelCount = 0;
		if (sprite.getWidth() < 64) {
			for (int y = 0; y < sprite.getHeight(); y++) {
				for (int x = 0; x < sprite.getWidth(); x++) {
					int c = image.getPixelRgba(x, y);
					if (((c >> 24) & 0xFF) != 0x00) {
						int spriteB = ((c >> 16) & 0xFF);
						int spriteG = ((c >> 8) & 0xFF);
						int spriteR = (c & 0xFF);
						float[] hsb = Color.RGBtoHSB(spriteR, spriteG, spriteB, null);
						hX += Math.cos(hsb[0] / 180 * Math.PI);
						hY += Math.sin(hsb[0] / 180 * Math.PI);
						s += hsb[1];
						b += hsb[2];
						pixelCount++;
					}
				}
			}
		} else {
			//sprite too big for comfort, trying with a limited sample area
			int width = sprite.getWidth();
			int height = sprite.getHeight();
			int anchorX = 0;
			int anchorY = 0;
			boolean anchorFound = false;
			for (int y = 0; y < sprite.getHeight(); y++) {
				for (int x = 0; x < width; x++) {
					int c = image.getPixelRgba(x, y);
					if (((c >> 24) & 0xFF) != 0x00) {
						anchorX = x;
						anchorY = y;
						anchorFound = true;
						break;
					}
				}
				if (anchorFound) {
					break;
				}
			}
			while (width > 64) {
				width /= 2;
				height /= 2;
			}
			anchorFound=false;
			for (int y = anchorY; y < (anchorY+height); y++) {
				for (int x = anchorX; x < (anchorX+width); x++) {
					if (x > sprite.getWidth() || y > sprite.getHeight()){
						anchorFound = true;
						break;
					}
					int c = image.getPixelRgba(x, y);
					if (((c >> 24) & 0xFF) != 0x00) {
						int spriteB = ((c >> 16) & 0xFF);
						int spriteG = ((c >> 8) & 0xFF);
						int spriteR = (c & 0xFF);
						float[] hsb = Color.RGBtoHSB(spriteR, spriteG, spriteB, null);
						hX += Math.cos(hsb[0] / 180 * Math.PI);
						hY += Math.sin(hsb[0] / 180 * Math.PI);
						s += hsb[1];
						b += hsb[2];
						pixelCount++;
					}
				}
				if (anchorFound){
					break;
				}
			}
		}
		if (pixelCount > 0) {
			//Now spin the wheel and laugh at god.
			hX /= pixelCount;
			hY /= pixelCount;
			s /= pixelCount;
			b /= pixelCount;
			float avgHue = (float) (Math.atan2(hY, hX) * 180 / Math.PI);
			return Color.HSBtoRGB(avgHue, s, b);
		} else {
			return errorColor;
		}
	}

	/**
	 * Dedicated getter for the SpriteAccessor.
	 * Use this if you really need to mess with {@linkplain Sprite#images}.
	 *
	 * @param sprite The Sprite you wish to access the #images of.
	 * @return {@linkplain NativeImage[] images} within the provided Sprite.
	 */
	public static NativeImage[] getImagesForSprite(Sprite sprite){
		return ((SpriteAccessor) sprite).getImages();
	}

	/**
	 * Reference enum for {@linkplain ColorSniffer#getAverageSpriteColor(Sprite, AveragingMethod)}.
	 *
	 *I wouldn't worry about it.
	 */
	public enum AveragingMethod {
		RGB, HSV
	}
}
