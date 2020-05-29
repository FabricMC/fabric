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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback;
import net.fabricmc.fabric.impl.registry.sync.ListenableRegistry;
import net.fabricmc.fabric.impl.registry.sync.RemapException;
import net.fabricmc.fabric.impl.registry.sync.RemapStateImpl;
import net.fabricmc.fabric.impl.registry.sync.RemappableRegistry;

@Mixin(SimpleRegistry.class)
public abstract class MixinIdRegistry<T> implements RemappableRegistry, ListenableRegistry {
	@Shadow
	protected Int2ObjectBiMap<T> indexedEntries;
	@Shadow
	protected BiMap<Identifier, T> entriesById;
	@Shadow
	protected BiMap<RegistryKey<T>, T> entriesByKey;
	@Shadow
	private int nextId;
	@Unique
	private static Logger FABRIC_LOGGER = LogManager.getLogger();

	@Unique
	private final Event<RegistryEntryAddedCallback> fabric_addObjectEvent = EventFactory.createArrayBacked(RegistryEntryAddedCallback.class,
			(callbacks) -> (rawId, id, object) -> {
				for (RegistryEntryAddedCallback callback : callbacks) {
					//noinspection unchecked
					callback.onEntryAdded(rawId, id, object);
				}
			}
	);

	@Unique
	private final Event<RegistryEntryRemovedCallback> fabric_removeObjectEvent = EventFactory.createArrayBacked(RegistryEntryRemovedCallback.class,
			(callbacks) -> (rawId, id, object) -> {
				for (RegistryEntryRemovedCallback callback : callbacks) {
					//noinspection unchecked
					callback.onEntryRemoved(rawId, id, object);
				}
			}
	);

	@Unique
	private final Event<RegistryIdRemapCallback> fabric_postRemapEvent = EventFactory.createArrayBacked(RegistryIdRemapCallback.class,
			(callbacks) -> (a) -> {
				for (RegistryIdRemapCallback callback : callbacks) {
					//noinspection unchecked
					callback.onRemap(a);
				}
			}
	);

	@Unique
	private Object2IntMap<Identifier> fabric_prevIndexedEntries;
	@Unique
	private BiMap<Identifier, T> fabric_prevEntries;

	@Override
	public Event<RegistryEntryAddedCallback<T>> fabric_getAddObjectEvent() {
		//noinspection unchecked
		return (Event) fabric_addObjectEvent;
	}

	@Override
	public Event<RegistryEntryRemovedCallback<T>> fabric_getRemoveObjectEvent() {
		//noinspection unchecked
		return (Event) fabric_removeObjectEvent;
	}

	@Override
	public Event<RegistryIdRemapCallback<T>> fabric_getRemapEvent() {
		//noinspection unchecked
		return (Event) fabric_postRemapEvent;
	}

	// The rest of the registry isn't thread-safe, so this one need not be either.
	@Unique
	private boolean fabric_isObjectNew = false;

	@SuppressWarnings({"unchecked", "ConstantConditions"})
	@Inject(method = "set", at = @At("HEAD"))
	public void setPre(int id, RegistryKey<T> registryId, Object object, CallbackInfoReturnable info) {
		int indexedEntriesId = indexedEntries.getId((T) object);

		if (indexedEntriesId >= 0) {
			throw new RuntimeException("Attempted to register object " + object + " twice! (at raw IDs " + indexedEntriesId + " and " + id + " )");
		}

		if (!entriesById.containsKey(registryId.getValue())) {
			fabric_isObjectNew = true;
		} else {
			T oldObject = entriesById.get(registryId.getValue());

			if (oldObject != null && oldObject != object) {
				int oldId = indexedEntries.getId(oldObject);

				if (oldId != id) {
					throw new RuntimeException("Attempted to register ID " + registryId + " at different raw IDs (" + oldId + ", " + id + ")! If you're trying to override an item, use .set(), not .register()!");
				}

				fabric_removeObjectEvent.invoker().onEntryRemoved(oldId, registryId.getValue(), oldObject);
				fabric_isObjectNew = true;
			} else {
				fabric_isObjectNew = false;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "set", at = @At("RETURN"))
	public void setPost(int id, RegistryKey<T> registryId, Object object, CallbackInfoReturnable info) {
		if (fabric_isObjectNew) {
			fabric_addObjectEvent.invoker().onEntryAdded(id, registryId.getValue(), object);
		}
	}

	@Override
	public void remap(String name, Object2IntMap<Identifier> remoteIndexedEntries, RemapMode mode) throws RemapException {
		//noinspection unchecked, ConstantConditions
		SimpleRegistry<Object> registry = (SimpleRegistry<Object>) (Object) this;

		// Throw on invalid conditions.
		switch (mode) {
		case AUTHORITATIVE:
			break;
		case REMOTE: {
			List<String> strings = null;

			for (Identifier remoteId : remoteIndexedEntries.keySet()) {
				if (!entriesById.keySet().contains(remoteId)) {
					if (strings == null) {
						strings = new ArrayList<>();
					}

					strings.add(" - " + remoteId);
				}
			}

			if (strings != null) {
				StringBuilder builder = new StringBuilder("Received ID map for " + name + " contains IDs unknown to the receiver!");

				for (String s : strings) {
					builder.append('\n').append(s);
				}

				throw new RemapException(builder.toString());
			}

			break;
		}
		case EXACT: {
			if (!entriesById.keySet().equals(remoteIndexedEntries.keySet())) {
				List<String> strings = new ArrayList<>();

				for (Identifier remoteId : remoteIndexedEntries.keySet()) {
					if (!entriesById.keySet().contains(remoteId)) {
						strings.add(" - " + remoteId + " (missing on local)");
					}
				}

				for (Identifier localId : registry.getIds()) {
					if (!remoteIndexedEntries.keySet().contains(localId)) {
						strings.add(" - " + localId + " (missing on remote)");
					}
				}

				StringBuilder builder = new StringBuilder("Local and remote ID sets for " + name + " do not match!");

				for (String s : strings) {
					builder.append('\n').append(s);
				}

				throw new RemapException(builder.toString());
			}

			break;
		}
		}

		// Make a copy of the previous maps.
		// For now, only one is necessary - on an integrated server scenario,
		// AUTHORITATIVE == CLIENT, which is fine.
		// The reason we preserve the first one is because it contains the
		// vanilla order of IDs before mods, which is crucial for vanilla server
		// compatibility.
		if (fabric_prevIndexedEntries == null) {
			fabric_prevIndexedEntries = new Object2IntOpenHashMap<>();
			fabric_prevEntries = HashBiMap.create(entriesById);

			for (Object o : registry) {
				fabric_prevIndexedEntries.put(registry.getId(o), registry.getRawId(o));
			}
		}

		Int2ObjectMap<Identifier> oldIdMap = new Int2ObjectOpenHashMap<>();

		for (Object o : registry) {
			oldIdMap.put(registry.getRawId(o), registry.getId(o));
		}

		// If we're AUTHORITATIVE, we append entries which only exist on the
		// local side to the new entry list. For REMOTE, we instead drop them.
		switch (mode) {
		case AUTHORITATIVE: {
			int maxValue = 0;

			Object2IntMap<Identifier> oldRemoteIndexedEntries = remoteIndexedEntries;
			remoteIndexedEntries = new Object2IntOpenHashMap<>();

			for (Identifier id : oldRemoteIndexedEntries.keySet()) {
				int v = oldRemoteIndexedEntries.getInt(id);
				remoteIndexedEntries.put(id, v);
				if (v > maxValue) maxValue = v;
			}

			for (Identifier id : registry.getIds()) {
				if (!remoteIndexedEntries.containsKey(id)) {
					FABRIC_LOGGER.warn("Adding " + id + " to saved/remote registry.");
					remoteIndexedEntries.put(id, ++maxValue);
				}
			}

			break;
		}
		case REMOTE: {
			// TODO: Is this what mods really want?
			Set<Identifier> droppedIds = new HashSet<>();

			for (Identifier id : registry.getIds()) {
				if (!remoteIndexedEntries.containsKey(id)) {
					Object object = registry.get(id);
					int rid = registry.getRawId(object);

					droppedIds.add(id);

					// Emit RemoveObject events for removed objects.
					//noinspection unchecked
					fabric_getRemoveObjectEvent().invoker().onEntryRemoved(rid, id, (T) object);
				}
			}

			// note: indexedEntries cannot be safely remove()d from
			entriesById.keySet().removeAll(droppedIds);
			entriesByKey.keySet().removeIf(registryKey -> droppedIds.contains(registryKey.getValue()));

			break;
		}
		}

		Int2IntMap idMap = new Int2IntOpenHashMap();

		for (Object o : indexedEntries) {
			Identifier id = registry.getId(o);
			int rid = registry.getRawId(o);

			// see above note
			if (remoteIndexedEntries.containsKey(id)) {
				idMap.put(rid, remoteIndexedEntries.getInt(id));
			}
		}

		// entries was handled above, if it was necessary.
		indexedEntries.clear();
		nextId = 0;

		List<Identifier> orderedRemoteEntries = new ArrayList<>(remoteIndexedEntries.keySet());
		orderedRemoteEntries.sort(Comparator.comparingInt(remoteIndexedEntries::getInt));

		for (Identifier identifier : orderedRemoteEntries) {
			int id = remoteIndexedEntries.getInt(identifier);
			T object = entriesById.get(identifier);

			// Warn if an object is missing from the local registry.
			// This should only happen in AUTHORITATIVE mode, and as such we
			// throw an exception otherwise.
			if (object == null) {
				if (mode != RemapMode.AUTHORITATIVE) {
					throw new RemapException(identifier + " missing from registry, but requested!");
				} else {
					FABRIC_LOGGER.warn(identifier + " missing from registry, but requested!");
				}

				continue;
			}

			// Add the new object, increment nextId to match.
			indexedEntries.put(object, id);

			if (nextId <= id) {
				nextId = id + 1;
			}
		}

		//noinspection unchecked
		fabric_getRemapEvent().invoker().onRemap(new RemapStateImpl(registry, oldIdMap, idMap));
	}

	@Override
	public void unmap(String name) throws RemapException {
		if (fabric_prevIndexedEntries != null) {
			List<Identifier> addedIds = new ArrayList<>();

			// Emit AddObject events for previously culled objects.
			for (Identifier id : fabric_prevEntries.keySet()) {
				if (!entriesById.containsKey(id)) {
					assert fabric_prevIndexedEntries.containsKey(id);
					addedIds.add(id);
				}
			}

			entriesById.clear();
			entriesByKey.clear();

			entriesById.putAll(fabric_prevEntries);

			for (Map.Entry<Identifier, T> entry : fabric_prevEntries.entrySet()) {
				//noinspection unchecked
				entriesByKey.put(RegistryKey.of(RegistryKey.ofRegistry(((Registry) Registry.REGISTRIES).getId(this)), entry.getKey()), entry.getValue());
			}

			remap(name, fabric_prevIndexedEntries, RemapMode.AUTHORITATIVE);

			for (Identifier id : addedIds) {
				fabric_getAddObjectEvent().invoker().onEntryAdded(indexedEntries.getId(entriesById.get(id)), id, entriesById.get(id));
			}

			fabric_prevIndexedEntries = null;
			fabric_prevEntries = null;
		}
	}
}
