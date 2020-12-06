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

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;

@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin<T extends ResourcePackProfile> {
	@Shadow
	@Final
	@Mutable
	private Set<ResourcePackProvider> providers;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void construct(ResourcePackProfile.Factory arg, ResourcePackProvider[] resourcePackProviders, CallbackInfo info) {
		providers = new HashSet<>(providers);

		// Search resource pack providers to find any server-related pack provider.
		boolean shouldAddServerProvider = false;

		for (ResourcePackProvider provider : this.providers) {
			if (provider instanceof FileResourcePackProvider
					&& (((FileResourcePackProviderAccessor) provider).getResourcePackSource() == ResourcePackSource.PACK_SOURCE_WORLD
					|| ((FileResourcePackProviderAccessor) provider).getResourcePackSource() == ResourcePackSource.PACK_SOURCE_SERVER)) {
				shouldAddServerProvider = true;
				break;
			}
		}

		// On server, add the mod resource pack provider.
		if (shouldAddServerProvider) {
			providers.add(new ModResourcePackCreator(ResourceType.SERVER_DATA));
		}
	}
}
