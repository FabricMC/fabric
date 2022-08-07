package net.fabricmc.fabric.api.datagen.v1.provider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Used in {@link FabricTextureProvider#generateItemTextures(ItemTextureConsumer)}.
 */
public interface ItemTextureConsumer extends TextureConsumer {
	/**
	 * Add an item texture.
	 * @param itemID The identifier of the item
	 * @param data The image data in PNG format.
	 */
	default void addItemTexture(Identifier itemID, byte[] data) {
		this.addTexture(getTexturePath(itemID), data);
	}

	/**
	 * Add an item texture.
	 * @param item The item, must be registered.
	 * @param data The image data in PNG format.
	 */
	default void addItemTexture(Item item, byte[] data) {
		this.addTexture(getTexturePath(item), data);
	}

	/**
	 * Add an item texture from a {@link BufferedImage}.
	 * @param itemID The identifier of the item
	 * @param image The image.
	 * @throws IOException If the buffered image can't be converted to a PNG file, this is thrown.
	 */
	default void addItemTexture(Identifier itemID, BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		this.addItemTexture(itemID, baos.toByteArray());
	}

	/**
	 * Add an item texture from a {@link BufferedImage}.
	 * @param item The item, must be registered.
	 * @param image The image.
	 * @throws IOException If the buffered image can't be converted to a PNG file, this is thrown.
	 */
	default void addItemTexture(Item item, BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		this.addItemTexture(item, baos.toByteArray());
	}

	private Identifier getTexturePath(Item item) {
		return getTexturePath(Registry.ITEM.getId(item));
	}

	private Identifier getTexturePath(Identifier itemID) {
		return new Identifier(itemID.getNamespace(), "textures/item/" + itemID.getPath() + ".png");
	}
}
