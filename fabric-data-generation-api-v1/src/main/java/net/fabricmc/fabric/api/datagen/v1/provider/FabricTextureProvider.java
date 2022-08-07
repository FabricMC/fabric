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
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.common.hash.HashCode;
import com.mojang.authlib.minecraft.client.MinecraftClient;

import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Extend this class and implement the relevant methods.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}
 */
public abstract class FabricTextureProvider implements DataProvider {
	protected final FabricDataGenerator generator;

	protected FabricTextureProvider(FabricDataGenerator generator) {
		this.generator = generator;
	}

	/**
	 * Implement this method to add item textures.
	 */
	public abstract void generateItemTextures(ItemTextureConsumer itemTextureConsumer);

	/**
	 * Implement this method to add block textures.
	 */
	public abstract void generateBlockTextures(BlockTextureConsumer blockTextureConsumer);

	/**
	 * Implement this method to add textures anywhere in the "assets" folder.
	 */
	public abstract void generateMiscTextures(TextureConsumer textureConsumer);

	/**
	 * Get a texture from the "assets" folder of the Minecraft jar.
	 * @param texturePath The path of the texture.
	 * @return A buffered image containing the texture.
	 * @throws IOException If the texture doesn't exist, or is of an invalid file type, this is thrown.
	 */
	public BufferedImage getMinecraftTexture(Identifier texturePath) throws IOException {
		return ImageIO.read(MinecraftClient.class.getResourceAsStream("/assets/" + texturePath.getNamespace() + "/" + texturePath.getPath()));
	}

	@Override
	public void run(DataWriter writer) throws IOException {
		HashMap<Identifier, byte[]> textures = new HashMap<>();

		generateItemTextures(textures::put);
		generateBlockTextures(textures::put);
		generateMiscTextures(textures::put);

		for (Map.Entry<Identifier, byte[]> identifierEntry : textures.entrySet()) {
			writer.write(getAssetsPath(identifierEntry.getKey()), identifierEntry.getValue(), HashCode.fromBytes(identifierEntry.getValue()));
		}
	}

	private Path getAssetsPath(Identifier texturePath) {
		return generator.getOutput().resolve(Path.of("assets", texturePath.getNamespace(), texturePath.getPath()));
	}

	@Override
	public String getName() {
		return "Textures";
	}
}
