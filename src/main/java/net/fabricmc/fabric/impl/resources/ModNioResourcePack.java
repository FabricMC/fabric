/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.resources;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.resource.AbstractFilenameResourcePack;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModNioResourcePack extends AbstractFilenameResourcePack implements ModResourcePack {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ModMetadata modInfo;
	private final Path basePath;
	private final boolean cacheable;
	private final AutoCloseable closer;

	public ModNioResourcePack(ModMetadata modInfo, Path path, AutoCloseable closer) {
		super(new File(path.toString()));
		this.modInfo = modInfo;
		this.basePath = path.toAbsolutePath();
		this.cacheable = false; /* TODO */
		this.closer = closer;
	}

	private Path getPath(String filename) {
		Path childPath = basePath.resolve(filename.replaceAll("/", basePath.getFileSystem().getSeparator())).toAbsolutePath();

		if (childPath.startsWith(basePath) && Files.exists(childPath)) {
			return childPath;
		} else {
			return null;
		}
	}

	@Override
	protected InputStream openFilename(String filename) throws IOException {
		Path path = getPath(filename);
		if (path != null && Files.isRegularFile(path)) {
			return Files.newInputStream(path);
		}

		InputStream stream = ModResourcePackUtil.openDefault(modInfo, filename);
		if (stream == null) {
			throw new ResourceNotFoundException(this.base, filename);
		}
		return stream;
	}

	@Override
	protected boolean containsFilename(String filename) {
		if (ModResourcePackUtil.containsDefault(modInfo, filename)) {
			return true;
		}

		Path path = getPath(filename);
		return path != null && Files.isRegularFile(path);
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String path, int depth, Predicate<String> predicate) {
		List<Identifier> ids = new ArrayList<>();
		String nioPath = path.replaceAll("/", basePath.getFileSystem().getSeparator());

		for (String namespace : getNamespaces(type)) {
			Path namespacePath = getPath(type.getName() + "/" + namespace);
			if (namespacePath != null) {
				Path searchPath = namespacePath.resolve(nioPath).toAbsolutePath();

				if (Files.exists(searchPath)) {
					try {
						Files.walk(searchPath, depth)
							.filter((p) -> Files.isRegularFile(p))
							.filter((p) -> {
								String filename = p.getFileName().toString();
								return !filename.endsWith(".mcmeta") && predicate.test(filename);
							})
							.map(namespacePath::relativize)
							.map((p) -> p.toString().replaceAll(p.getFileSystem().getSeparator(), "/"))
							.forEach((s) -> {
								try {
									ids.add(new Identifier(namespace, s));
								} catch (InvalidIdentifierException e) {
									LOGGER.error(e.getMessage());
								}
							});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return ids;
	}

	private Set<String> namespaceCache;

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		if (namespaceCache != null) {
			return namespaceCache;
		}

		try {
			Path typePath = getPath(type.getName());
			if (typePath == null) {
				return Collections.emptySet();
			}

			Set<String> namespaces = new HashSet<>();
			for (Path path : Files.newDirectoryStream(typePath, (p) -> Files.isDirectory(p))) {
				String s = path.getFileName().toString();

				if (s.equals(s.toLowerCase(Locale.ROOT))) {
					namespaces.add(s);
				} else {
					this.warnNonLowercaseNamespace(s);
				}
			}

			if (cacheable) {
				namespaceCache = namespaces;
			}
			return namespaces;
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptySet();
		}
	}

	@Override
	public void close() throws IOException {
		if (closer != null) {
			try {
				closer.close();
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	@Override
	public ModMetadata getFabricModMetadata() {
		return modInfo;
	}

	@Override
	public String getName() {
		return ModResourcePackUtil.getName(modInfo);
	}
}
