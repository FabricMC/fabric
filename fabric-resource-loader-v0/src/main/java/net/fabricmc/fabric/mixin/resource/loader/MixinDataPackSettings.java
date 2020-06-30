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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;

@Mixin(DataPackSettings.class)
public class MixinDataPackSettings {
	@Shadow
	@Final
	@Mutable
	private List<String> enabled;

	/*
	This injection takes all instances of this class with an enabled list that only have the vanilla pack enabled,
	and forcibly enables all mod resource packs. This is probably not the best option, but it's the only one that I can
	think of that will work on both existing and new worlds. Is there a better option?
	 */
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(List<String> enabled, List<String> disabled, CallbackInfo info) {
		if (enabled.size() == 1 && enabled.get(0).equals("vanilla")) {
			List<String> newEnabled = new ArrayList<>(enabled);

			for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
				if (container.getMetadata().getType().equals("builtin")) {
					continue;
				}

				Path path = container.getRootPath();

				try (ResourcePack pack = new ModNioResourcePack(container.getMetadata(), path, null)) {
					if (!pack.getNamespaces(ResourceType.SERVER_DATA).isEmpty()) {
						newEnabled.add("fabric/" + container.getMetadata().getId());
					}
				}
			}

			this.enabled = newEnabled;
		}
	}
}
