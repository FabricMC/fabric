package net.fabricmc.fabric.impl.client.texture;

import net.fabricmc.fabric.mixin.client.texture.SpriteAccessor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;

/**
 * Utility class for averaging Sprite colors.
 * You should call one of these methods!
 */
public class ColorSniffer {

	/**
	 * @param sprite The Sprite you want to get the average color from.
	 * @return The average ABGR color of the provided Sprite.
	 */
	public static int getAverageSpriteColor(Sprite sprite) { return getAverageSpriteColor(sprite, 0); }

	/**
	 * @param sprite The Sprite you want to get the average color from.
	 * @param frame The specific frame of the sprite, in case it has more than one.
	 * @return The average ABGR color of the provided Sprite, during the provided frame.
	 */
	public static int getAverageSpriteColor(Sprite sprite, int frame) {
		if (sprite == null) {
			return 0xFFFF00FF;
		}

		NativeImage image = ((SpriteAccessor) sprite).getImages()[frame];
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
			return 0xFFFF00FF;
		}
	}
}
