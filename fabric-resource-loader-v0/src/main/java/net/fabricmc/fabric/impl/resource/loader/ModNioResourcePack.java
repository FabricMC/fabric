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

package net.fabricmc.fabric.impl.resource.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class ModNioResourcePack extends AbstractFileResourcePack implements ModResourcePack {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModNioResourcePack.class);
	private static final Pattern RESOURCE_PACK_PATH = Pattern.compile("[a-z0-9-_.]+");

	private final String name;
	private final ModMetadata modInfo;
	private final List<Path> basePaths;
	private final ResourceType type;
	private final AutoCloseable closer;
	private final ResourcePackActivationType activationType;
	private final Map<ResourceType, Set<String>> namespaces;

	public static ModNioResourcePack create(String name, ModContainer mod, String subPath, ResourceType type, ResourcePackActivationType activationType) {
		List<Path> rootPaths = mod.getRootPaths();
		List<Path> paths;

		if (subPath == null) {
			paths = rootPaths;
		} else {
			paths = new ArrayList<>(rootPaths.size());

			for (Path path : rootPaths) {
				path = path.toAbsolutePath().normalize();
				Path childPath = path.resolve(subPath.replace("/", path.getFileSystem().getSeparator())).normalize();

				if (!childPath.startsWith(path) || !Files.exists(childPath)) {
					continue;
				}

				paths.add(childPath);
			}
		}

		if (paths.isEmpty()) return null;

		ModNioResourcePack ret = new ModNioResourcePack(name, mod.getMetadata(), paths, type, null, activationType);

		return ret.getNamespaces(type).isEmpty() ? null : ret;
	}

	private ModNioResourcePack(String name, ModMetadata modInfo, List<Path> paths, ResourceType type, AutoCloseable closer, ResourcePackActivationType activationType) {
		super(null);

		this.name = name;
		this.modInfo = modInfo;
		this.basePaths = paths;
		this.type = type;
		this.closer = closer;
		this.activationType = activationType;
		this.namespaces = readNamespaces(paths, modInfo.getId());
	}

	private static Map<ResourceType, Set<String>> readNamespaces(List<Path> paths, String modId) {
		Map<ResourceType, Set<String>> ret = new EnumMap<>(ResourceType.class);

		for (ResourceType type : ResourceType.values()) {
			Set<String> namespaces = null;

			for (Path path : paths) {
				Path dir = path.resolve(type.getDirectory());
				if (!Files.isDirectory(dir)) continue;

				String separator = path.getFileSystem().getSeparator();

				try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
					for (Path p : ds) {
						if (!Files.isDirectory(p)) continue;

						String s = p.getFileName().toString();
						// s may contain trailing slashes, remove them
						s = s.replace(separator, "");

						if (!RESOURCE_PACK_PATH.matcher(s).matches()) {
							LOGGER.warn("Fabric NioResourcePack: ignored invalid namespace: {} in mod ID {}", s, modId);
							continue;
						}

						if (namespaces == null) namespaces = new HashSet<>();

						namespaces.add(s);
					}
				} catch (IOException e) {
					LOGGER.warn("getNamespaces in mod " + modId + " failed!", e);
				}
			}

			ret.put(type, namespaces != null ? namespaces : Collections.emptySet());
		}

		return ret;
	}

	private Path getPath(String filename) {
		if (hasAbsentNs(filename)) return null;

		for (Path basePath : basePaths) {
			Path childPath = basePath.resolve(filename.replace("/", basePath.getFileSystem().getSeparator())).toAbsolutePath().normalize();

			if (childPath.startsWith(basePath) && Files.exists(childPath)) {
				return childPath;
			}
		}

		return null;
	}

	private static final String resPrefix = ResourceType.CLIENT_RESOURCES.getDirectory()+"/";
	private static final String dataPrefix = ResourceType.SERVER_DATA.getDirectory()+"/";

	private boolean hasAbsentNs(String filename) {
		int prefixLen;
		ResourceType type;

		if (filename.startsWith(resPrefix)) {
			prefixLen = resPrefix.length();
			type = ResourceType.CLIENT_RESOURCES;
		} else if (filename.startsWith(dataPrefix)) {
			prefixLen = dataPrefix.length();
			type = ResourceType.SERVER_DATA;
		} else {
			return false;
		}

		int nsEnd = filename.indexOf('/', prefixLen);
		if (nsEnd < 0) return false;

		return !namespaces.get(type).contains(filename.substring(prefixLen, nsEnd));
	}

	@Override
	protected InputStream openFile(String filename) throws IOException {
		InputStream stream;

		Path path = getPath(filename);

		if (path != null && Files.isRegularFile(path)) {
			return Files.newInputStream(path);
		}

		stream = ModResourcePackUtil.openDefault(this.modInfo, this.type, filename);

		if (stream != null) {
			return stream;
		}

		// ReloadableResourceManagerImpl gets away with FileNotFoundException.
		throw new FileNotFoundException("\"" + filename + "\" in Fabric mod \"" + modInfo.getId() + "\"");
	}

	@Override
	protected boolean containsFile(String filename) {
		if (ModResourcePackUtil.containsDefault(modInfo, filename)) {
			return true;
		}

		Path path = getPath(filename);
		return path != null && Files.isRegularFile(path);
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String path, Predicate<Identifier> predicate) {
		if (!namespaces.getOrDefault(type, Collections.emptySet()).contains(namespace)) {
			return Collections.emptyList();
		}

		List<Identifier> ids = new ArrayList<>();

		for (Path basePath : basePaths) {
			String separator = basePath.getFileSystem().getSeparator();
			Path nsPath = basePath.resolve(type.getDirectory()).resolve(namespace);
			Path searchPath = nsPath.resolve(path.replace("/", separator)).normalize();
			if (!Files.exists(searchPath)) continue;

			try {
				Files.walkFileTree(searchPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						String fileName = file.getFileName().toString();
						if (fileName.endsWith(".mcmeta")) return FileVisitResult.CONTINUE;

						try {
							Identifier id = new Identifier(namespace, nsPath.relativize(file).toString().replace(separator, "/"));
							if (predicate.test(id)) ids.add(id);
						} catch (InvalidIdentifierException e) {
							LOGGER.error(e.getMessage());
						}

						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				LOGGER.warn("findResources at " + path + " in namespace " + namespace + ", mod " + modInfo.getId() + " failed!", e);
			}
		}

		return ids;
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return namespaces.getOrDefault(type, Collections.emptySet());
	}

	@Override
	public void close() {
		if (closer != null) {
			try {
				closer.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public ModMetadata getFabricModMetadata() {
		return modInfo;
	}

	public ResourcePackActivationType getActivationType() {
		return this.activationType;
	}

	@Override
	public String getName() {
		return name;
	}
}
