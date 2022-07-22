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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Identifier;

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
		// - If there is NO line with the resource pack id, add it to the enabled packs and to the tracker file.
		// - If there is a line with the pack id, do not add it to the enabled packs and let
		//   the options value decides if it is enabled or not.
		// - If there is a line without matching pack id (e.g. because the mod is removed),
		//   remove it from the tracker file so that it would be enabled again if added back later.

		File configDir = FabricLoader.getInstance().getConfigDir().resolve("fabric").toFile();

		if (!configDir.exists() && !configDir.mkdirs()) {
			LOGGER.warn("[Fabric Resource Loader] Could not create configuration directory: " + configDir.getAbsolutePath());
		}

		File trackerFile = new File(configDir, "default_resource_pack_tracker.txt");
		Set<Identifier> trackedPacks = new HashSet<>();

		if (trackerFile.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(trackerFile, StandardCharsets.UTF_8))) {
				String line;

				while ((line = reader.readLine()) != null) {
					if (!line.isBlank() && !line.startsWith("#")) {
						trackedPacks.add(new Identifier(line));
					}
				}
			} catch (IOException e) {
				LOGGER.warn("[Fabric Resource Loader] Could not read " + trackerFile.getAbsolutePath(), e);
			}
		}

		Set<Identifier> removedPacks = new HashSet<>(trackedPacks);
		Set<String> resourcePacks = new LinkedHashSet<>(this.resourcePacks);

		List<ResourcePackProfile> profiles = new ArrayList<>();
		ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.register(profiles::add);

		for (ResourcePackProfile profile : profiles) {
			// Always add "Fabric Mods" pack to enabled resource packs.
			if (profile.getSource() == ModResourcePackCreator.RESOURCE_PACK_SOURCE) {
				resourcePacks.add(profile.getName());
				continue;
			}

			ResourcePack pack = profile.createResourcePack();

			if (pack instanceof ModNioResourcePack builtinPack && builtinPack.getActivationType().isEnabledByDefault()) {
				if (trackedPacks.add(builtinPack.getId())) {
					resourcePacks.add(profile.getName());
				} else {
					removedPacks.remove(builtinPack.getId());
				}
			}
		}

		try (FileWriter writer = new FileWriter(trackerFile, StandardCharsets.UTF_8)) {
			writer.write("# DO NOT MODIFY THIS FILE\n");

			for (Identifier id : trackedPacks) {
				if (!removedPacks.contains(id)) {
					writer.write(id.toString());
					writer.write('\n');
				}
			}
		} catch (IOException e) {
			LOGGER.warn("[Fabric Resource Loader] Could not write to " + trackerFile.getAbsolutePath(), e);
		}

		this.resourcePacks = new ArrayList<>(resourcePacks);
	}
}
