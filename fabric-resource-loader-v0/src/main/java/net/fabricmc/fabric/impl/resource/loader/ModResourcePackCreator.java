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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.resource.ModResourcePack;

/**
 * Represents a resource pack provider for mods and built-in mods resource packs.
 */
public class ModResourcePackCreator implements ResourcePackProvider {
	public static final ResourcePackSource RESOURCE_PACK_SOURCE = text -> Text.translatable("pack.nameAndSource", text, Text.translatable("pack.source.fabricmod"));
	public static final ModResourcePackCreator CLIENT_RESOURCE_PACK_PROVIDER = new ModResourcePackCreator(ResourceType.CLIENT_RESOURCES);
	private final ResourcePackProfile.Factory factory;
	private final ResourceType type;

	public ModResourcePackCreator(ResourceType type) {
		this.type = type;
		this.factory = (name, text, bl, supplier, metadata, initialPosition, source) ->
				new ResourcePackProfile(name, text, bl, supplier, metadata, type, initialPosition, source);
	}

	/**
	 * Registers the resource packs.
	 *
	 * @param consumer The resource pack profile consumer.
	 */
	public void register(Consumer<ResourcePackProfile> consumer) {
		this.register(consumer, this.factory);
	}

	@Override
	public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
		/*
			Register order rule in this provider:
			1. Mod resource packs
			2. Mod built-in resource packs

			Register order rule globally:
			1. Default and Vanilla built-in resource packs
			2. Mod resource packs
			3. Mod built-in resource packs
			4. User resource packs
		 */

		// Build a list of mod resource packs.
		List<ModResourcePack> packs = new ArrayList<>();
		ModResourcePackUtil.appendModResourcePacks(packs, type, null);

		if (!packs.isEmpty()) {
			// Make the resource pack profile for mod resource packs.
			// Mod resource packs must always be enabled to avoid issues
			// and inserted on top to ensure that they are applied before user resource packs and after default/programmer art resource pack.
			// @TODO: "inserted on top" comment is deprecated, it does not guarantee the condition "applied before user resource packs".
			ResourcePackProfile resourcePackProfile = ResourcePackProfile.of("Fabric Mods",
					true, () -> new FabricModResourcePack(this.type, packs), factory, ResourcePackProfile.InsertionPosition.TOP,
					RESOURCE_PACK_SOURCE);

			if (resourcePackProfile != null) {
				consumer.accept(resourcePackProfile);
			}
		}

		// Register all built-in resource packs provided by mods.
		ResourceManagerHelperImpl.registerBuiltinResourcePacks(this.type, consumer, factory);
	}
}
