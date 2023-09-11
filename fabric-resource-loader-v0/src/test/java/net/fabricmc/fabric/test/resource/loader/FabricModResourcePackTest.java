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

package net.fabricmc.fabric.test.resource.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceFilter;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.FabricModResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class FabricModResourcePackTest {
	@BeforeAll
	static void beforeAll() {
		SharedConstants.createGameVersion();
	}

	@Test
	void resourcePackMetadata(@TempDir Path rootPath) throws IOException {
		writeDummyResources("test1", rootPath);

		try (var resourcePack = new FabricModResourcePack(ResourceType.CLIENT_RESOURCES, List.of(
				createModResourcePack("test1", rootPath)
		))) {
			final PackResourceMetadata resourceMetadata = resourcePack.parseMetadata(PackResourceMetadata.SERIALIZER);
			assertNotNull(resourceMetadata);
			assertEquals(resourceMetadata.description().getString(), "Mod resources.");

			final ResourceFilter resourceFilter = resourcePack.parseMetadata(ResourceFilter.SERIALIZER);
			assertNull(resourceFilter);
		}
	}

	@Test
	void resourcePackFiters(@TempDir Path rootPath) throws IOException {
		@Language("json")
		String mcmeta = """
				{
					"pack": {
						"pack_format": 18,
						"description": "My epic test mod"
					},
					"filter": {
						"block": [
							{
								"namespace": "minecraft",
								"path": "recipes/.*"
							}
						]
					}
				}""";

		writeDummyResources("test1", rootPath);
		Files.writeString(rootPath.resolve("pack.mcmeta"), mcmeta);

		try (var resourcePack = new FabricModResourcePack(ResourceType.CLIENT_RESOURCES, List.of(
				createModResourcePack("test1", rootPath)
		))) {
			final ResourceFilter resourceFilter = resourcePack.parseMetadata(ResourceFilter.SERIALIZER);
			assertNotNull(resourceFilter);

			assertTrue(resourceFilter.isNamespaceBlocked("minecraft"));
			assertFalse(resourceFilter.isNamespaceBlocked("fabric"));

			assertTrue(resourceFilter.isPathBlocked("recipes/example.json"));
			assertFalse(resourceFilter.isPathBlocked("blocks/example.json"));
		}
	}

	private static ModResourcePack createModResourcePack(String modId, Path rootDir) {
		final ModMetadata modMetadata = mock(ModMetadata.class);
		when(modMetadata.getId()).thenReturn(modId);

		final ModContainer modContainer = mock(ModContainer.class);
		when(modContainer.getRootPaths()).thenReturn(List.of(rootDir));
		when(modContainer.getMetadata()).thenReturn(modMetadata);

		ModNioResourcePack modNioResourcePack = ModNioResourcePack.create(modId, modContainer, null, ResourceType.CLIENT_RESOURCES, ResourcePackActivationType.ALWAYS_ENABLED);
		assertNotNull(modNioResourcePack);
		return modNioResourcePack;
	}

	// A resource pack will only be loaded when it has either an assets or data directory.
	private static void writeDummyResources(String modId, Path rootPath) throws IOException {
		Files.createDirectories(rootPath.resolve("assets/%s/".formatted(modId)));
		Files.createDirectories(rootPath.resolve("data/%s/".formatted(modId)));
	}
}
