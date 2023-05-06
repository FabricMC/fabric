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

package net.fabricmc.fabric.mixin.resource.loader.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.option.GameOptions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;

import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
	@Shadow
	public List<String> resourcePacks;

	@Shadow
	@Final
	static Logger LOGGER;

	@Inject(method = "load", at = @At("RETURN"))
	private void onLoad(CallbackInfo ci) {
		// Track built-in resource packs if they are enabled by default.
		// - If there is NO value with matching resource pack id, add it to the enabled packs and the tracker file.
		// - If there is a matching value and pack id, do not add it to the enabled packs and let
		//   the options value decides if it is enabled or not.
		// - If there is a value without matching pack id (e.g. because the mod is removed),
		//   remove it from the tracker file so that it would be enabled again if added back later.

		File dataDir = FabricLoader.getInstance().getGameDir().resolve("data").toFile();

		if (!dataDir.exists() && !dataDir.mkdirs()) {
			LOGGER.warn("[Fabric Resource Loader] Could not create data directory: " + dataDir.getAbsolutePath());
		}

		File trackerFile = new File(dataDir, "fabricDefaultResourcePacks.dat");
		Set<String> trackedPacks = new HashSet<>();

		if (trackerFile.exists()) {
			try {
				NbtCompound data = NbtIo.readCompressed(trackerFile);
				NbtList values = data.getList("values", NbtElement.STRING_TYPE);

				for (int i = 0; i < values.size(); i++) {
					trackedPacks.add(values.getString(i));
				}
			} catch (IOException e) {
				LOGGER.warn("[Fabric Resource Loader] Could not read " + trackerFile.getAbsolutePath(), e);
			}
		}

		Set<String> removedPacks = new HashSet<>(trackedPacks);
		Set<String> resourcePacks = new LinkedHashSet<>(this.resourcePacks);

		List<ResourcePackProfile> profiles = new ArrayList<>();
		ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.register(profiles::add);

		for (ResourcePackProfile profile : profiles) {
			// Always add "Fabric Mods" pack to enabled resource packs.
			if (profile.getSource() == ModResourcePackCreator.RESOURCE_PACK_SOURCE) {
				resourcePacks.add(profile.getName());
				continue;
			}

			try (ResourcePack pack = profile.createResourcePack()) {
				if (pack instanceof ModNioResourcePack builtinPack && builtinPack.getActivationType().isEnabledByDefault()) {
					if (trackedPacks.add(builtinPack.getName())) {
						resourcePacks.add(profile.getName());
					} else {
						removedPacks.remove(builtinPack.getName());
					}
				}
			}
		}

		try {
			NbtList values = new NbtList();

			for (String id : trackedPacks) {
				if (!removedPacks.contains(id)) {
					values.add(NbtString.of(id));
				}
			}

			NbtCompound nbt = new NbtCompound();
			nbt.put("values", values);
			NbtIo.writeCompressed(nbt, trackerFile);
		} catch (IOException e) {
			LOGGER.warn("[Fabric Resource Loader] Could not write to " + trackerFile.getAbsolutePath(), e);
		}

		this.resourcePacks = new ArrayList<>(resourcePacks);
	}
}
