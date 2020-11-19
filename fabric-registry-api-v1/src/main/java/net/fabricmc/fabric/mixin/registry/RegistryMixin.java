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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.registry.v1.RegistryEvents;
import net.fabricmc.fabric.impl.registry.RegistryExtensions;

@ApiStatus.Internal
@Mixin(Registry.class)
abstract class RegistryMixin<T> implements RegistryExtensions<T> {
	@Shadow
	public abstract RegistryKey<? extends Registry<T>> getKey();

	@Unique
	protected static final Logger FABRIC_LOGGER = LogManager.getLogger("fabric-registry-api-v1");
	@Unique
	private Set<Identifier> attributes = new HashSet<>();
	@Unique
	private final Event<RegistryEvents.EntryAdded<T>> entryAddedEvent = EventFactory.createArrayBacked(RegistryEvents.EntryAdded.class, callbacks -> (rawId, id, object) -> {
		for (RegistryEvents.EntryAdded<T> callback : callbacks) {
			callback.onEntryAdded(rawId, id, object);
		}
	});
	@Unique
	private final Event<RegistryEvents.EntryRemoved<T>> entryRemovedEvent = EventFactory.createArrayBacked(RegistryEvents.EntryRemoved.class, callbacks -> (rawId, id, object) -> {
		for (RegistryEvents.EntryRemoved<T> callback : callbacks) {
			callback.onEntryRemoved(rawId, id, object);
		}
	});

	@Override
	public Event<RegistryEvents.EntryAdded<T>> getEntryAddedEvent() {
		return this.entryAddedEvent;
	}

	@Override
	public Event<RegistryEvents.EntryRemoved<T>> getEntryRemovedEvent() {
		return this.entryRemovedEvent;
	}

	@Override
	public void addAttribute(Identifier id) {
		this.attributes.add(id);
	}

	@Override
	public Set<Identifier> getAttributes() {
		return Collections.unmodifiableSet(this.attributes);
	}
}
