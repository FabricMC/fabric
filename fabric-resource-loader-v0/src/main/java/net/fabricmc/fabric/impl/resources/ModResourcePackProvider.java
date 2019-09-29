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

package net.fabricmc.fabric.impl.resources;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackCreator;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModResourcePackProvider implements ResourcePackCreator {
	private final ResourceType type;

	public ModResourcePackProvider(ResourceType type) {
		this.type = type;
	}

	@Override
	public <T extends ResourcePackContainer> void registerContainer(Map<String, T> map, ResourcePackContainer.Factory<T> factory) {
		// TODO: "vanilla" does not emit a message; neither should a modded datapack
		List<ModNioResourcePack> packs = new ArrayList<>();
		ModResourcePackUtil.appendModResourcePacks(packs, type);

		for (ModNioResourcePack pack : packs) {
			if (pack.requestsStandaloneProfile()) {
				String modId = pack.getFabricModMetadata().getId();
				T modProfile = ResourcePackContainer.of("fabric/" + modId, true, () -> pack, factory, ResourcePackContainer.InsertionPosition.BOTTOM);
				if (modProfile == null)
					throw new IllegalStateException("Failed to load mod resource pack for \"" + modId + "\"!");

				if (modProfile instanceof CustomImageResourcePackProfile) {
					ModResourcePackUtil.setPackIcon(pack, (CustomImageResourcePackProfile) modProfile);
				}

				map.put(modProfile.getName(), modProfile);
			}
		}
		
		packs.removeIf(ModResourcePack::requestsStandaloneProfile);

		ResourcePack fabricGeneralPack = new FabricCombinedResourcePack(new LiteralText("Fabric pack"), packs);
		T fabricProfile = ResourcePackContainer.of("fabric", true, () -> fabricGeneralPack, factory, ResourcePackContainer.InsertionPosition.BOTTOM);
		if (fabricProfile == null)
			throw new IllegalStateException("Failed to load Fabric resource pack!");

		if (fabricProfile instanceof CustomImageResourcePackProfile) {
			ModResourcePackUtil.setPackIcon(FabricLoader.getInstance().getModContainer("fabric-resource-loader-v0")
				.orElseThrow(() -> new IllegalStateException("Fabric resource loader not present!")), (CustomImageResourcePackProfile) fabricProfile);
		}

		map.put(fabricProfile.getName(), fabricProfile);
	}
}
