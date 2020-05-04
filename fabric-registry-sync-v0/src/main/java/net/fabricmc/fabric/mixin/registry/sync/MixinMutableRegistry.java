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

package net.fabricmc.fabric.mixin.registry.sync;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.registry.sync.HashedRegistry;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.impl.registry.sync.FabricRegistry;

@Mixin(MutableRegistry.class)
public abstract class MixinMutableRegistry<T> extends Registry<T> implements HashedRegistry {
	@Unique
	private int preBootstrapHash = -1;

	@Unique
	private static final Logger FARBIC_LOGGER = LogManager.getLogger("FabricRegistrySync");

	@Inject(method = "add", at = @At("RETURN"))
	private <V extends T> void add(Identifier id, V entry, CallbackInfoReturnable<V> info) {
		onChange(id);
		checkEntry(id, entry);
	}

	@Inject(method = "set", at = @At("RETURN"))
	private <V extends T> void set(int rawId, Identifier id, V entry, CallbackInfoReturnable<V> info) {
		onChange(id);
		checkEntry(id, entry);
	}

	@Unique
	private void onChange(Identifier id) {
		if (RegistrySyncManager.postBootstrap || !id.getNamespace().equals("minecraft")) {
			RegistryAttributeHolder holder = RegistryAttributeHolder.get(this);

			if (!holder.hasAttribute(RegistryAttribute.MODDED)) {
				//noinspection unchecked
				FARBIC_LOGGER.debug("Registry {} has been marked as modded, registry entry {} was changed", Registry.REGISTRIES.getId((MutableRegistry<T>) (Object) this), id);
				RegistryAttributeHolder.get(this).addAttribute(RegistryAttribute.MODDED);
			}
		}
	}

	@Unique
	private <V extends T> void checkEntry(Identifier id, V entry) {
		// Detect legacy modded registries, this is deprecated as they should use the builder from now on.
		if (entry instanceof FabricRegistry) {
			if (!id.getNamespace().equals("minecraft")) {
				FabricRegistry fabricRegistry = (FabricRegistry) entry;

				if (!fabricRegistry.builtByBuilder()) {
					FARBIC_LOGGER.warn("Registry {} was not built with FabricRegistryBuilder this is deprecated! It has been given default attributes.", id);
					fabricRegistry
							.addAttribute(RegistryAttribute.SYNCED)
							.addAttribute(RegistryAttribute.PERSISTED)
							.addAttribute(RegistryAttribute.MODDED);
				}
			}
		}
	}

	@Override
	public int getStoredHash() {
		return preBootstrapHash;
	}

	@Override
	public int storeHash() {
		return preBootstrapHash = getIds().hashCode();
	}
}
