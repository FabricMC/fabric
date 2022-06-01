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

package net.fabricmc.fabric.mixin.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.util.Identifier;

/**
 * Make the default resource pack use the MC jar directly instead of the full classpath.
 * This is a major speed improvement, as well as a bugfix (it prevents other mod jars from overriding MC's resources).
 */
@Mixin(DefaultResourcePack.class)
public abstract class DefaultResourcePackMixin {
	/**
	 * Redirect all resource access to the MC jar zip pack.
	 */
	final AbstractFileResourcePack fabric_mcJarPack = createJarZipPack();

	@Unique
	private AbstractFileResourcePack createJarZipPack() {
		ResourceType type;

		if (getClass().equals(DefaultResourcePack.class)) {
			// Server pack
			type = ResourceType.SERVER_DATA;
		} else {
			// Client pack
			type = ResourceType.CLIENT_RESOURCES;
		}

		// Locate MC jar by finding the URL that contains the assets root.
		try {
			URL assetsRootUrl = DefaultResourcePack.class.getResource("/" + type.getDirectory() + "/.mcassetsroot");
			URLConnection connection = assetsRootUrl.openConnection();

			if (connection instanceof JarURLConnection) {
				return new ZipResourcePack(Paths.get(((JarURLConnection) connection).getJarFileURL().toURI()).toFile());
			} else {
				// Not a jar, assume it's a regular directory.
				Path rootPath = Paths.get(assetsRootUrl.toURI()).resolve("../..").toAbsolutePath();
				return new DirectoryResourcePack(rootPath.toFile());
			}
		} catch (Exception exception) {
			throw new RuntimeException("Fabric: Failed to locate Minecraft assets root!", exception);
		}
	}

	/**
	 * @author FabricMC
	 * @reason Gets rid of classpath scanning.
	 */
	@Overwrite
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		return fabric_mcJarPack.findResources(type, namespace, prefix, maxDepth, pathFilter);
	}

	/**
	 * @author FabricMC
	 * @reason Gets rid of classpath scanning.
	 */
	@Overwrite
	public boolean contains(ResourceType type, Identifier id) {
		return fabric_mcJarPack.contains(type, id);
	}

	/**
	 * @author FabricMC
	 * @reason Close the resource pack we redirect resource access to.
	 */
	@Overwrite
	public void close() {
		fabric_mcJarPack.close();
	}

	/**
	 * @author FabricMC
	 * @reason Gets rid of classpath scanning.
	 */
	@Nullable
	@Overwrite
	public InputStream getInputStream(String path) throws IOException {
		return ((AbstractFileResourcePackAccessor) fabric_mcJarPack).openFile(path);
	}

	/**
	 * @author FabricMC
	 * @reason Gets rid of classpath scanning.
	 */
	@Nullable
	@Overwrite
	public InputStream findInputStream(ResourceType type, Identifier id) throws IOException {
		return fabric_mcJarPack.open(type, id);
	}
}
