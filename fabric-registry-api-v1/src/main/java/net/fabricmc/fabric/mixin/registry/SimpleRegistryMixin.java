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

package net.fabricmc.fabric.mixin.registry;

import com.google.common.collect.BiMap;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.api.registry.v1.RegistryAttributes;
import net.fabricmc.fabric.impl.registry.RegistryAttributeTracking;

@ApiStatus.Internal
@Mixin(SimpleRegistry.class)
abstract class SimpleRegistryMixin<T> extends RegistryMixin<T> {
	@Shadow
	@Final
	private Object2IntMap<T> entryToRawId;
	@Shadow
	@Final
	private BiMap<Identifier, T> idToEntry;

	// Stub variable used to track whether the current entry being added is new.
	// Minecraft's Registries are inherently not thread safe, so making this thread local is overkill
	@Unique
	private boolean fabric_isAddedObjectNew = false;

	// No need to inject into `add` as `set` is called by `add` and `replace`
	@Inject(method = "set(ILnet/minecraft/util/registry/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;Z)Ljava/lang/Object;", at = @At("HEAD"))
	private <V extends T> void handleAdditionToRegistry(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle, boolean checkDuplicateKeys, CallbackInfoReturnable<V> cir) {
		// This injection exists primarily to install some safety mechanisms into registries for mods and registry sync.
		// Mojang does check for some these changes we make already but will not fail; Mojang has opted to just log the changes
		//
		// The following scenarios should fail without any change to the contents of the registry:
		// - Registering an object twice in the Registry; registry entries should not share the same object instance
		// - Registering two different objects under the same Identifier; for obvious reason of replacing an entry

		// First let's validate the entry being registered is already not present in the registry
		final int indexedEntriesRawId = this.entryToRawId.getInt(entry);

		// The default return value of `entryToRawId` is `-1`.
		// If the value is 0 or greater, the object is already present in the registry
		if (indexedEntriesRawId >= 0) {
			throw new UnsupportedOperationException("Attempted to register object " + entry + " twice! (at raw IDs " + indexedEntriesRawId + " and " + rawId + " )");
		}

		final Identifier registryId = key.getValue();

		if (!this.idToEntry.containsKey(registryId)) {
			// We are not registry replacing anything, so no further checks are needed
			this.fabric_isAddedObjectNew = true;
			return;
		}

		// Check for possible registry replacement
		final T oldObject = this.idToEntry.get(registryId);

		// Verify the tracked entry is not a null value; don't know why you would put null values in the registry.
		// Also verify the replacement object is not the same as the old instance.

		// FIXME: The second condition should always be true?
		// This is since we check that we fail earlier if we register an object twice?
		if (oldObject != null && oldObject != entry) {
			final int oldObjectRawId = this.entryToRawId.getInt(oldObject);

			// When replacing a registry entry, both the Identifier and rawId must be the same.
			// If the raw ids don't match, likely due to just calling `add` naively to replace an entry, we need to fail.
			if (oldObjectRawId != rawId) {
				throw new UnsupportedOperationException("Attempted to register ID " + registryId + " at different raw IDs (" + oldObjectRawId + ", " + rawId + ")! If you're trying to override a registry entry, use .set(), not .register()!");
			}

			// Fire the remove event
			this.getEntryRemovedEvent().invoker().onEntryRemoved(oldObjectRawId, registryId, oldObject);
			this.fabric_isAddedObjectNew = true;
		} else {
			// We are not replacing any registry entry
			this.fabric_isAddedObjectNew = false;
		}

		// Everything past here in vanilla codepath will add the object to all the required maps
	}

	@Inject(method = "set(ILnet/minecraft/util/registry/RegistryKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;Z)Ljava/lang/Object;", at = @At("TAIL"))
	private <V extends T> void callEntryAdded(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle, boolean checkDuplicateKeys, CallbackInfoReturnable<V> cir) {
		// Called after the object has been placed in the correct maps in the registry
		if (this.fabric_isAddedObjectNew) {
			this.getEntryAddedEvent().invoker().onEntryAdded(rawId, key.getValue(), entry);
		}

		// Mark registry as modded if we are past bootstrap
		this.onChange(key);
	}

	/**
	 * Marks a registry as modded if the registry has been changed.
	 *
	 * @param registryKey the registry entry which has changed
	 */
	@Unique
	private void onChange(RegistryKey<T> registryKey) {
		// Check if we are past bootstrap or if the added registry entry is not in the minecraft namespace
		if (RegistryAttributeTracking.isBootstrapped() || !registryKey.getValue().getNamespace().equals("minecraft")) {
			// Check if the registry is already modded
			if (!RegistryAttributes.getAttributes((Registry<T>) (Object) this).contains(RegistryAttributes.MODDED)) {
				Identifier id = this.getKey().getValue();
				FABRIC_LOGGER.debug("Registry {} has been marked as modded, registry entry {} was changed", id, registryKey.getValue());
				RegistryAttributes.addAttribute((Registry<T>) (Object) this, RegistryAttributes.MODDED);
			}
		}
	}
}
