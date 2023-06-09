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

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import net.fabricmc.fabric.api.resource.loader.v1.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.loader.v1.ResourcePackHelper;
import net.fabricmc.loader.api.ModContainer;

public class BuiltinResourcePackHolder {
	private static final Set<Pair<Text, ModNioResourcePack>> builtinResourcePacks = new HashSet<>();

	/**
	 * Registers a built-in resource pack. Internal implementation.
	 *
	 * @param id             the identifier of the resource pack
	 * @param subPath        the sub path in the mod resources
	 * @param container      the mod container
	 * @param displayName    the display name of the resource pack
	 * @param activationType the activation type of the resource pack
	 * @return {@code true} if successfully registered the resource pack, else {@code false}
	 * @see ResourcePackHelper#registerBuiltinResourcePack(Identifier, ModContainer, Text, ResourcePackActivationType)
	 */
	public static boolean registerBuiltinResourcePack(Identifier id, String subPath, ModContainer container, Text displayName, ResourcePackActivationType activationType) {
		// Assuming the mod has multiple paths, we simply "hope" that the  file separator is *not* different across them
		List<Path> paths = container.getRootPaths();
		String separator = paths.get(0).getFileSystem().getSeparator();
		subPath = subPath.replace("/", separator);
		ModNioResourcePack resourcePack = ModNioResourcePack.create(id.toString(), container, subPath, ResourceType.CLIENT_RESOURCES, activationType);
		ModNioResourcePack dataPack = ModNioResourcePack.create(id.toString(), container, subPath, ResourceType.SERVER_DATA, activationType);
		if (resourcePack == null && dataPack == null) return false;

		if (resourcePack != null) {
			builtinResourcePacks.add(new Pair<>(displayName, resourcePack));
		}

		if (dataPack != null) {
			builtinResourcePacks.add(new Pair<>(displayName, dataPack));
		}

		return true;
	}

	public static void registerBuiltinResourcePacks(ResourceType resourceType, Consumer<ResourcePackProfile> consumer) {
		// Loop through each registered built-in resource packs and add them if valid.
		for (Pair<Text, ModNioResourcePack> entry : builtinResourcePacks) {
			ModNioResourcePack pack = entry.getRight();

			// Add the built-in pack only if namespaces for the specified resource type are present.
			if (!pack.getNamespaces(resourceType).isEmpty()) {
				// Make the resource pack profile for built-in pack, should never be always enabled.
				ResourcePackProfile profile = ResourcePackProfile.create(
						entry.getRight().getName(),
						entry.getLeft(),
						pack.getActivationType() == ResourcePackActivationType.ALWAYS_ENABLED,
						ignored -> entry.getRight(),
						resourceType,
						ResourcePackProfile.InsertionPosition.TOP,
						new BuiltinModResourcePackSource(pack.getFabricModMetadata().getName())
				);
				consumer.accept(profile);
			}
		}
	}
}
