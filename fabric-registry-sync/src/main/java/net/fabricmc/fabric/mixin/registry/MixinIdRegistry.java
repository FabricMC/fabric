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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.impl.registry.ListenableRegistry;
import net.fabricmc.fabric.impl.registry.RegistryListener;
import net.fabricmc.fabric.impl.registry.RemapException;
import net.fabricmc.fabric.impl.registry.RemappableRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(SimpleRegistry.class)
public abstract class MixinIdRegistry<T> implements RemappableRegistry, ListenableRegistry<T>, RegistryListener<T> {
	@Shadow
	protected static Logger LOGGER;
	@Shadow
	protected Int2ObjectBiMap<T> indexedEntries;
	@Shadow
	protected BiMap<Identifier, T> entries;
	@Shadow
	private int nextId;

	private Object2IntMap<Identifier> fabric_prevIndexedEntries;
	private BiMap<Identifier, T> fabric_prevEntries;
	private RegistryListener[] fabric_listeners;

	@Override
	public void registerListener(RegistryListener<T> listener) {
		if (fabric_listeners == null) {
			fabric_listeners = new RegistryListener[]{listener};
		} else {
			RegistryListener[] newListeners = new RegistryListener[fabric_listeners.length + 1];
			System.arraycopy(fabric_listeners, 0, newListeners, 0, fabric_listeners.length);
			newListeners[fabric_listeners.length] = listener;
			fabric_listeners = newListeners;
		}
	}

	@SuppressWarnings({"unchecked", "ConstantConditions"})
	@Inject(method = "set", at = @At("HEAD"))
	public void setPre(int id, Identifier identifier, Object object, CallbackInfoReturnable info) {
		SimpleRegistry<Object> registry = (SimpleRegistry<Object>) (Object) this;
		if (fabric_listeners != null) {
			for (RegistryListener listener : fabric_listeners) {
				listener.beforeRegistryRegistration(registry, id, identifier, object, !entries.containsKey(identifier));
			}
		}
	}

	@SuppressWarnings({"unchecked", "ConstantConditions"})
	@Inject(method = "set", at = @At("RETURN"))
	public void setPost(int id, Identifier identifier, Object object, CallbackInfoReturnable info) {
		SimpleRegistry<Object> registry = (SimpleRegistry<Object>) (Object) this;
		if (fabric_listeners != null) {
			for (RegistryListener listener : fabric_listeners) {
				listener.afterRegistryRegistration(registry, id, identifier, object);
			}
		}
	}

	@Override
	public void remap(Object2IntMap<Identifier> remoteIndexedEntries, RemapMode mode) throws RemapException {
		//noinspection unchecked, ConstantConditions
		SimpleRegistry<Object> registry = (SimpleRegistry<Object>) (Object) this;

		// Throw on invalid conditions.
		switch (mode) {
			case AUTHORITATIVE:
				break;
			case REMOTE:
				if (!registry.getIds().containsAll(remoteIndexedEntries.keySet())) {
					throw new RemapException("Received ID map contains IDs unknown to the receiver!");
				}
				break;
			case EXACT:
				if (!registry.getIds().equals(remoteIndexedEntries.keySet())) {
					throw new RemapException("Local and remote ID sets do not match!");
				}
				break;
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
					LOGGER.warn("Adding " + id + " to registry.");
					remoteIndexedEntries.put(id, ++maxValue);
				}
			}
		} else if (mode == RemapMode.REMOTE) {
			// TODO: Is this what mods really want?
			Set<Identifier> droppedIds = new HashSet<>();

			for (Identifier id : registry.getIds()) {
				if (!remoteIndexedEntries.containsKey(id)) {
					droppedIds.add(id);
				}
			}

			entries.keySet().removeAll(droppedIds);
		}

		// Inform about registry clearing.
		if (fabric_listeners != null) {
			for (RegistryListener listener : fabric_listeners) {
				listener.beforeRegistryCleared(registry);
			}
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
					LOGGER.warn(identifier + " missing from registry, but requested!");
				}
				continue;
			}

			// Add the new object, increment nextId to match.
			indexedEntries.put(object, id);
			if (nextId <= id) {
				nextId = id + 1;
			}

			// Notify listeners about the ID change.
			if (fabric_listeners != null) {
				for (RegistryListener listener : fabric_listeners) {
					listener.beforeRegistryRegistration(registry, id, identifier, object, false);
				}
			}
		}
	}

	@Override
	public void unmap() throws RemapException {
		if (fabric_prevIndexedEntries != null) {
			entries.clear();
			entries.putAll(fabric_prevEntries);

			remap(fabric_prevIndexedEntries, RemapMode.AUTHORITATIVE);

			fabric_prevIndexedEntries = null;
			fabric_prevEntries = null;
		}
	}
}
