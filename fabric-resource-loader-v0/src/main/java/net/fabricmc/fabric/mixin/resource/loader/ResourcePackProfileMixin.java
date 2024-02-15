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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.llamalad7.mixinextras.sugar.Local;

import net.fabricmc.fabric.impl.resource.loader.OverlayConditionsMetadata;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;

import net.fabricmc.fabric.impl.resource.loader.FabricResourcePackProfile;
import net.fabricmc.fabric.impl.resource.loader.ResourcePackSourceTracker;

/**
 * Implements resource pack source tracking (for {@link net.fabricmc.fabric.impl.resource.loader.FabricResource}).
 * {@link ResourcePack} doesn't hold a reference to its {@link ResourcePackSource}
 * so we store the source in a global tracker when the resource packs are created.
 *
 * @see ResourcePackSourceTracker
 */
@Mixin(ResourcePackProfile.class)
abstract class ResourcePackProfileMixin implements FabricResourcePackProfile {
	@Unique
	private static final Predicate<Set<String>> DEFAULT_PARENT_PREDICATE = parents -> true;
	@Shadow
	@Final
	private ResourcePackSource source;
	@Unique
	private Predicate<Set<String>> parentsPredicate = DEFAULT_PARENT_PREDICATE;

	@Inject(method = "createResourcePack", at = @At("RETURN"))
	private void onCreateResourcePack(CallbackInfoReturnable<ResourcePack> info) {
		ResourcePackSourceTracker.setSource(info.getReturnValue(), source);
	}

	@Override
	public boolean fabric_isHidden() {
		return parentsPredicate != DEFAULT_PARENT_PREDICATE;
	}

	@Override
	public boolean fabric_parentsEnabled(Set<String> enabled) {
		return parentsPredicate.test(enabled);
	}

	@Override
	public void fabric_setParentsPredicate(Predicate<Set<String>> predicate) {
		this.parentsPredicate = predicate;
	}

	@ModifyVariable(method = "loadMetadata", at = @At("STORE"))
	private static List<String> fabric_applyOverlayConditions(List<String> overlays, @Local ResourcePack resourcePack) throws IOException {
		List<String> appliedOverlays = new ArrayList<>(overlays);
		OverlayConditionsMetadata overlayConditionsMetadata = resourcePack.parseMetadata(OverlayConditionsMetadata.SERIALIZER);

		if (overlayConditionsMetadata != null) {
			appliedOverlays.addAll(overlayConditionsMetadata.getAppliedOverlays());
		}

		return appliedOverlays;
	}
}
