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
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.registry.RegistryAddObjectCallback;
import net.fabricmc.fabric.api.event.registry.RegistryRemapCallback;
import net.fabricmc.fabric.api.event.registry.RegistryRemoveObjectCallback;
import net.fabricmc.fabric.impl.registry.ListenableRegistry;
import net.fabricmc.fabric.impl.registry.RemapStateImpl;
import net.fabricmc.fabric.impl.registry.RemapException;
import net.fabricmc.fabric.impl.registry.RemappableRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(SimpleRegistry.class)
public abstract class MixinIdRegistry<T> implements RemappableRegistry, ListenableRegistry {
	@Shadow
	protected Int2ObjectBiMap<T> indexedEntries;
	@Shadow
	protected BiMap<Identifier, T> entries;
	@Shadow
	private int nextId;
	@Unique
	private static Logger FABRIC_LOGGER = LogManager.getLogger();

	private final Event<RegistryAddObjectCallback> fabric_addObjectEvent = EventFactory.createArrayBacked(RegistryAddObjectCallback.class,
		(callbacks) -> (rawId, id, object) -> {
			for (RegistryAddObjectCallback callback : callbacks) {
				//noinspection unchecked
				callback.onAddObject(rawId, id, object);
			}
		}
	);

	private final Event<RegistryRemoveObjectCallback> fabric_removeObjectEvent = EventFactory.createArrayBacked(RegistryRemoveObjectCallback.class,
		(callbacks) -> (rawId, id, object) -> {
			for (RegistryRemoveObjectCallback callback : callbacks) {
				//noinspection unchecked
				callback.onRemoveObject(rawId, id, object);
			}
		}
	);

	private final Event<RegistryRemapCallback> fabric_postRemapEvent = EventFactory.createArrayBacked(RegistryRemapCallback.class,
		(callbacks) -> (a) -> {
			for (RegistryRemapCallback callback : callbacks) {
				//noinspection unchecked
				callback.remap(a);
			}
		}
	);

	private Object2IntMap<Identifier> fabric_prevIndexedEntries;
	private BiMap<Identifier, T> fabric_prevEntries;

	@Override
	public Event<RegistryAddObjectCallback<T>> fabric_getAddObjectEvent() {
		//noinspection unchecked
		return (Event<RegistryAddObjectCallback<T>>) (Event) fabric_addObjectEvent;
	}

	@Override
	public Event<RegistryRemoveObjectCallback<T>> fabric_getRemoveObjectEvent() {
		//noinspection unchecked
		return (Event<RegistryRemoveObjectCallback<T>>) (Event) fabric_removeObjectEvent;
	}

	@Override
	public Event<RegistryRemapCallback<T>> fabric_getRemapEvent() {
		//noinspection unchecked
		return (Event<RegistryRemapCallback<T>>) (Event) fabric_postRemapEvent;
	}

	@SuppressWarnings({"unchecked", "ConstantConditions"})
	@Inject(method = "set", at = @At("HEAD"))
	public void setPre(int id, Identifier identifier, Object object, CallbackInfoReturnable info) {
		if (!entries.containsKey(identifier)) {
			fabric_addObjectEvent.invoker().onAddObject(id, identifier, object);
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
				List<String> strings = new ArrayList<>();
				for (Identifier remoteId : remoteIndexedEntries.keySet()) {
					if (!registry.getIds().contains(remoteId)) {
						strings.add(" - " + remoteId);
					}
				}

				if (!strings.isEmpty()) {
					StringBuilder builder = new StringBuilder("Received ID map for " + name + " contains IDs unknown to the receiver!");
					for (String s : strings) {
						builder.append('\n').append(s);
					}
					throw new RemapException(builder.toString());
				}
			} break;
			case EXACT: {
				if (!registry.getIds().equals(remoteIndexedEntries.keySet())) {
					List<String> strings = new ArrayList<>();
					for (Identifier remoteId : remoteIndexedEntries.keySet()) {
						if (!registry.getIds().contains(remoteId)) {
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
			} break;
		}

		// Make a copy of the previous maps.
		// For now, only one is necessary - on an integrated server scenario,
		// AUTHORITATIVE == CLIENT, which is fine.
		// The reason we preserve the first one is because it contains the
		// vanilla order of IDs before mods, which is crucial for vanilla server
		// compatibility.
		if (fabric_prevIndexedEntries == null) {
			fabric_prevIndexedEntries = new Object2IntOpenHashMap<>();
			fabric_prevEntries = HashBiMap.create(entries);
			for (Identifier id : registry.getIds()) {
				//noinspection unchecked
				fabric_prevIndexedEntries.put(id, registry.getRawId(registry.get(id)));
			}
		}

		// If we're AUTHORITATIVE, we append entries which only exist on the
		// local side to the new entry list. For REMOTE, we instead drop them.
		if (mode == RemapMode.AUTHORITATIVE) {
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
		} else if (mode == RemapMode.REMOTE) {
			// TODO: Is this what mods really want?
			Set<Identifier> droppedIds = new HashSet<>();

			for (Identifier id : registry.getIds()) {
				if (!remoteIndexedEntries.containsKey(id)) {
					droppedIds.add(id);
					Object object = registry.get(id);

					// Emit RemoveObject events for removed objects.
					//noinspection unchecked
					fabric_getRemoveObjectEvent().invoker().onRemoveObject(registry.getRawId(object), id, (T) object);
				}
			}

			entries.keySet().removeAll(droppedIds);
		}

		Int2IntMap idMap = new Int2IntOpenHashMap();
		for (Object o : indexedEntries) {
			Identifier id = registry.getId(o);
			idMap.put(registry.getRawId(o), remoteIndexedEntries.getInt(id));
		}

		// entries was handled above, if it was necessary.
		indexedEntries.clear();
		nextId = 0;

		List<Identifier> orderedRemoteEntries = new ArrayList<>(remoteIndexedEntries.keySet());
		orderedRemoteEntries.sort(Comparator.comparingInt(remoteIndexedEntries::getInt));

		for (Identifier identifier : orderedRemoteEntries) {
			int id = remoteIndexedEntries.getInt(identifier);
			T object = entries.get(identifier);

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
		fabric_getRemapEvent().invoker().remap(new RemapStateImpl(registry, idMap));
	}

	@Override
	public void unmap(String name) throws RemapException {
		if (fabric_prevIndexedEntries != null) {
			List<Identifier> addedIds = new ArrayList<>();

			// Emit AddObject events for previously culled objects.
			for (Identifier id : fabric_prevEntries.keySet()) {
				if (!entries.containsKey(id)) {
					assert fabric_prevIndexedEntries.containsKey(id);
					addedIds.add(id);
				}
			}

			entries.clear();
			entries.putAll(fabric_prevEntries);

			remap(name, fabric_prevIndexedEntries, RemapMode.AUTHORITATIVE);

			for (Identifier id : addedIds) {
				fabric_getAddObjectEvent().invoker().onAddObject(indexedEntries.getId(entries.get(id)), id, entries.get(id));
			}

			fabric_prevIndexedEntries = null;
			fabric_prevEntries = null;
		}
	}
}
