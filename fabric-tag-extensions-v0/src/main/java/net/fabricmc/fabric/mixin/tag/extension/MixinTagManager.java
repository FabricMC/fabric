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

package net.fabricmc.fabric.mixin.tag.extension;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.impl.tag.extension.FabricTagManagerHooks;

@Mixin(TagManager.class)
public class MixinTagManager implements FabricTagManagerHooks {
	@Shadow
	@Mutable
	@Final
	private Map<RegistryKey<? extends Registry<?>>, TagGroup<?>> tagGroups;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(Map<RegistryKey<? extends Registry<?>>, TagGroup<?>> tagGroups, CallbackInfo ci) {
		// Make it mutable so we can add dynamic registry tags later.
		this.tagGroups = new HashMap<>(tagGroups);
	}

	@Override
	public void fabric_addTagGroup(RegistryKey<? extends Registry<?>> registryKey, TagGroup<?> tagGroup) {
		tagGroups.put(registryKey, tagGroup);
	}
}
