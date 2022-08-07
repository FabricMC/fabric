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
