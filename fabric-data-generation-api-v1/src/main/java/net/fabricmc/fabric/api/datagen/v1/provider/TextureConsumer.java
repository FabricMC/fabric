package net.fabricmc.fabric.api.datagen.v1.provider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.util.Identifier;

/**
 * A consumer used by various methods in {@link FabricTextureProvider}.
 */
public interface TextureConsumer {
	/**
	 * Add a texture.
	 * @param texturePath The location of the texture.
	 * @param image The image data in PNG format.
	 */
	void addTexture(Identifier texturePath, byte[] image);

	/**
	 * Add a texture from a {@link BufferedImage}.
	 * @param texturePath The location of the texture.
	 * @param image The image.
	 * @throws IOException If the buffered image can't be converted to a PNG file, this is thrown.
	 */
	default void addTexture(Identifier texturePath, BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		addTexture(texturePath, baos.toByteArray());
	}
}
