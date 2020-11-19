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

import java.util.Objects;
import java.util.Set;

import com.mojang.serialization.Lifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;
import net.fabricmc.fabric.api.registry.v1.RegistryAttributes;
import net.fabricmc.fabric.impl.registry.RegistryExtensions;
import net.fabricmc.fabric.impl.registry.sync.FabricRegistry;
import net.fabricmc.fabric.impl.registry.sync.ListenableRegistry;

@Mixin(Registry.class)
public abstract class RegistryMixin<T> implements RegistryAttributeHolder, FabricRegistry {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void initLegacyHooks(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, CallbackInfo ci) {
		final RegistryExtensions<T> extensions = RegistryExtensions.get((Registry<T>) (Object) this);

		// Registry must be listenable in order to dispatch old events.
		// New registry impl has events on every registry, but it is up to the specific registry impl to call those.
		// Fabric API implements the new entry added/removed events on SimpleRegistry which by effect also applies to DefaultedRegistry.
		if (this instanceof ListenableRegistry) {
			// Register old events to be fired by new event, must be lambdas
			extensions.getEntryAddedEvent().register((rawId, id, object) -> {
				RegistryEntryAddedCallback.event((Registry<T>) (Object) this).invoker().onEntryAdded(rawId, id, object);
			});

			extensions.getEntryRemovedEvent().register((rawId, id, object) -> {
				RegistryEntryRemovedCallback.event((Registry<T>) (Object) this).invoker().onEntryRemoved(rawId, id, object);
			});
		}
	}

	@Override
	public RegistryAttributeHolder addAttribute(RegistryAttribute attribute) {
		Objects.requireNonNull(attribute, "Attribute cannot be null");
		RegistryExtensions.get((Registry<T>) (Object) this).addAttribute(attribute.getNewKey());

		return this;
	}

	@Override
	public boolean hasAttribute(RegistryAttribute attribute) {
		Objects.requireNonNull(attribute, "Attribute cannot be null");

		return RegistryAttributes.getAttributes((Registry<T>) (Object) this).contains(attribute.getNewKey());
	}

	@Override
	public void build(Set<RegistryAttribute> attributes) {
		final RegistryExtensions<T> extensions = RegistryExtensions.get((Registry<T>) (Object) this);

		for (RegistryAttribute attribute : attributes) {
			extensions.addAttribute(attribute.getNewKey());
		}
	}
}
