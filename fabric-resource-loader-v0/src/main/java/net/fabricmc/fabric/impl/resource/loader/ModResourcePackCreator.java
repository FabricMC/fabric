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
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

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
	/**
	 * The ID of the root resource pack profile for bundled packs.
	 */
	public static final String FABRIC = "fabric";
	private static final String PROGRAMMER_ART = "programmer_art";
	private static final String HIGH_CONTRAST = "high_contrast";
	public static final Set<String> POST_CHANGE_HANDLE_REQUIRED = Set.of(FABRIC, PROGRAMMER_ART, HIGH_CONTRAST);
	@VisibleForTesting
	public static final Predicate<Set<String>> BASE_PARENT = enabled -> enabled.contains(FABRIC);
	@VisibleForTesting
	public static final Predicate<Set<String>> PROGRAMMER_ART_PARENT = enabled -> enabled.contains(FABRIC) && enabled.contains(PROGRAMMER_ART);
	@VisibleForTesting
	public static final Predicate<Set<String>> HIGH_CONTRAST_PARENT = enabled -> enabled.contains(FABRIC) && enabled.contains(HIGH_CONTRAST);
	/**
	 * This can be used to check if a pack profile is for mod-provided packs.
	 */
	public static final ResourcePackSource RESOURCE_PACK_SOURCE = new ResourcePackSource() {
		@Override
		public Text decorate(Text packName) {
			return Text.translatable("pack.nameAndSource", packName, Text.translatable("pack.source.fabricmod"));
		}

		@Override
		public boolean canBeEnabledLater() {
			return true;
		}
	};
	public static final ModResourcePackCreator CLIENT_RESOURCE_PACK_PROVIDER = new ModResourcePackCreator(ResourceType.CLIENT_RESOURCES);
	private final ResourceType type;

	public ModResourcePackCreator(ResourceType type) {
		this.type = type;
	}

	/**
	 * Registers the resource packs.
	 *
	 * @param consumer The resource pack profile consumer.
	 */
	@Override
	public void register(Consumer<ResourcePackProfile> consumer) {
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

		consumer.accept(ResourcePackProfile.create(
				FABRIC,
				Text.translatable("pack.name.fabricMods"),
				true,
				new PlaceholderResourcePack.Factory(this.type),
				this.type,
				ResourcePackProfile.InsertionPosition.TOP,
				RESOURCE_PACK_SOURCE
		));

		// Build a list of mod resource packs.
		registerModPack(consumer, null, BASE_PARENT);

		if (this.type == ResourceType.CLIENT_RESOURCES) {
			// Programmer Art/High Contrast data packs can never be enabled.
			registerModPack(consumer, PROGRAMMER_ART, PROGRAMMER_ART_PARENT);
			registerModPack(consumer, HIGH_CONTRAST, HIGH_CONTRAST_PARENT);
		}

		// Register all built-in resource packs provided by mods.
		ResourceManagerHelperImpl.registerBuiltinResourcePacks(this.type, consumer);
	}

	private void registerModPack(Consumer<ResourcePackProfile> consumer, @Nullable String subPath, Predicate<Set<String>> parents) {
		List<ModResourcePack> packs = new ArrayList<>();
		ModResourcePackUtil.appendModResourcePacks(packs, this.type, subPath);

		for (ModResourcePack pack : packs) {
			Text displayName = subPath == null
					? Text.translatable("pack.name.fabricMod", pack.getFabricModMetadata().getName())
					: Text.translatable("pack.name.fabricMod.subPack", pack.getFabricModMetadata().getName(), Text.translatable("resourcePack." + subPath + ".name"));
			ResourcePackProfile profile = ResourcePackProfile.create(
					subPath == null ? pack.getName() : pack.getName() + "_" + subPath,
					displayName,
					subPath == null,
					new ModResourcePackFactory(pack),
					this.type,
					ResourcePackProfile.InsertionPosition.TOP,
					RESOURCE_PACK_SOURCE
			);

			if (profile != null) {
				((FabricResourcePackProfile) profile).setParentsPredicate(parents);
				consumer.accept(profile);
			}
		}
	}
}
