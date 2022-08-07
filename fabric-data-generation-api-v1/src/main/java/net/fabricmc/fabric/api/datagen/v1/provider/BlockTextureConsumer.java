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

package net.fabricmc.fabric.api.datagen.v1.provider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Used in {@link FabricTextureProvider#generateBlockTextures(BlockTextureConsumer)}.
 */
public interface BlockTextureConsumer extends TextureConsumer {
	/**
	 * Add a block texture.
	 * @param blockID The identifier of the block
	 * @param data The image data in PNG format.
	 */
	default void addblockTexture(Identifier blockID, byte[] data) {
		this.addTexture(getTexturePath(blockID), data);
	}

	/**
	 * Add a block texture.
	 * @param block The block, must be registered.
	 * @param data The image data in PNG format.
	 */
	default void addblockTexture(Block block, byte[] data) {
		this.addTexture(getTexturePath(block), data);
	}

	/**
	 * Add a block texture from a {@link BufferedImage}.
	 * @param blockID The identifier of the block
	 * @param image The image.
	 * @throws IOException If the buffered image can't be converted to a PNG file, this is thrown.
	 */
	default void addblockTexture(Identifier blockID, BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		this.addblockTexture(blockID, baos.toByteArray());
	}

	/**
	 * Add a block texture from a {@link BufferedImage}.
	 * @param block The block, must be registered.
	 * @param image The image.
	 * @throws IOException If the buffered image can't be converted to a PNG file, this is thrown.
	 */
	default void addblockTexture(Block block, BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		this.addblockTexture(block, baos.toByteArray());
	}

	private Identifier getTexturePath(Block block) {
		return getTexturePath(Registry.BLOCK.getId(block));
	}

	private Identifier getTexturePath(Identifier blockID) {
		return new Identifier(blockID.getNamespace(), "textures/block/" + blockID.getPath() + ".png");
	}
}
