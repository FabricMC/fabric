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

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.options.GameOptions;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;

import net.fabricmc.fabric.impl.resource.loader.ModNioResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
	@Shadow
	public List<String> resourcePacks;

	@Inject(method = "load", at = @At("RETURN"))
	private void onLoad(CallbackInfo ci) {
		// Add built-in resource packs if they are enabled by default only if the options file is blank.
		if (this.resourcePacks.isEmpty()) {
			List<ResourcePackProfile> profiles = new ArrayList<>();
			ModResourcePackCreator.CLIENT_RESOURCE_PACK_PROVIDER.register(profiles::add, ResourcePackProfile::new);
			this.resourcePacks = new ArrayList<>();

			for (ResourcePackProfile profile : profiles) {
				ResourcePack pack = profile.createResourcePack();
				if (profile.getSource() == ModResourcePackCreator.RESOURCE_PACK_SOURCE
						|| (pack instanceof ModNioResourcePack && ((ModNioResourcePack) pack).shouldBeEnabledByDefault())) {
					this.resourcePacks.add(profile.getName());
				}
			}
		}
	}
}
