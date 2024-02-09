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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.llamalad7.mixinextras.sugar.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.impl.resource.loader.FabricResourcePackProfile;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;

@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin {
	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger("ResourcePackManagerMixin");

	@Shadow
	@Final
	@Mutable
	public Set<ResourcePackProvider> providers;

	@Shadow
	private Map<String, ResourcePackProfile> profiles;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void construct(ResourcePackProvider[] resourcePackProviders, CallbackInfo info) {
		// Use a LinkedHashSet to preserve ordering
		providers = new LinkedHashSet<>(providers);

		// Search resource pack providers to find any server-related pack provider.
		boolean shouldAddServerProvider = false;

		for (ResourcePackProvider provider : this.providers) {
			if (provider instanceof FileResourcePackProvider
					&& (((FileResourcePackProvider) provider).source == ResourcePackSource.WORLD
					|| ((FileResourcePackProvider) provider).source == ResourcePackSource.SERVER)) {
				shouldAddServerProvider = true;
				break;
			}
		}

		// On server, add the mod resource pack provider.
		if (shouldAddServerProvider) {
			providers.add(new ModResourcePackCreator(ResourceType.SERVER_DATA));
		}
	}

	@Inject(method = "buildEnabledProfiles", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;", shift = At.Shift.BEFORE))
	private void handleAutoEnableDisable(Collection<String> enabledNames, CallbackInfoReturnable<List<ResourcePackProfile>> cir, @Local List<ResourcePackProfile> enabledAfterFirstRun) {
		ModResourcePackUtil.refreshAutoEnabledPacks(enabledAfterFirstRun, this.profiles);
	}

	@Inject(method = "enable", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER))
	private void handleAutoEnable(String profile, CallbackInfoReturnable<Boolean> cir, @Local List<ResourcePackProfile> newlyEnabled) {
		if (ModResourcePackCreator.POST_CHANGE_HANDLE_REQUIRED.contains(profile)) {
			ModResourcePackUtil.refreshAutoEnabledPacks(newlyEnabled, this.profiles);
		}
	}

	@Inject(method = "disable", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z"))
	private void handleAutoDisable(String profile, CallbackInfoReturnable<Boolean> cir, @Local List<ResourcePackProfile> enabled) {
		if (ModResourcePackCreator.POST_CHANGE_HANDLE_REQUIRED.contains(profile)) {
			Set<String> currentlyEnabled = enabled.stream().map(ResourcePackProfile::getId).collect(Collectors.toSet());
			enabled.removeIf(p -> !((FabricResourcePackProfile) p).fabric_parentsEnabled(currentlyEnabled));
			LOGGER.debug("[Fabric] Internal pack auto-removed upon disabling {}, result: {}", profile, enabled.stream().map(ResourcePackProfile::getId).toList());
		}
	}
}
