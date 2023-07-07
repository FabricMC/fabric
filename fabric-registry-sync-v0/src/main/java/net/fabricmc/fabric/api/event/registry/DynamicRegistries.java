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

package net.fabricmc.fabric.api.event.registry;

import java.util.List;
import java.util.Objects;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;

import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;

/**
 * Contains methods for registering and accessing dynamic registries.
 *
 * <p>TODO: Add usage example
 */
public final class DynamicRegistries {
	private DynamicRegistries() {
	}

	/**
	 * Returns an unmodifiable list of all dynamic registries, included modded ones.
	 * The list is sorted according to the ordering rules of the registries.
	 *
	 * <p>The list will not reflect any changes caused by later registrations.
	 *
	 * @return an unmodifiable list of all dynamic registries
	 */
	public static @Unmodifiable List<RegistryLoader.Entry<?>> getDynamicRegistries() {
		return DynamicRegistriesImpl.getDynamicRegistries();
	}

	/**
	 * Registers a dynamic registry.
	 *
	 * <p>The entries of the registry will be loaded from data packs at the file path
	 * {@code data/<entry namespace>/<registry namespace>/<registry path>/<entry path>.json}.
	 *
	 * @param key   the unique key of the registry
	 * @param codec the codec used to load registry entries from data packs
	 * @param <T>   the entry type of the registry
	 * @return a settings object to configure the registry
	 */
	public static <T> Settings<T> register(RegistryKey<? extends Registry<T>> key, Codec<T> codec) {
		return DynamicRegistriesImpl.register(key, codec);
	}

	/**
	 * Settings to configure a registered dynamic registry.
	 *
	 * @param <T> the entry type of the dynamic registry
	 */
	@ApiStatus.NonExtendable
	public interface Settings<T> {
		/**
		 * Marks the registry as synced.
		 *
		 * <p>It will be synced from the server to players' clients using the same codec
		 * that is used to load the registry.
		 *
		 * <p>If the object contained in the registry is complex and contains a lot of data
		 * that is not relevant on the client, another codec can be specified with {@link #synced(Codec)}.
		 *
		 * @return this settings object
		 */
		Settings<T> synced();

		/**
		 * Marks the registry as synced.
		 *
		 * <p>It will be synced from the server to players' clients using the given network codec.
		 *
		 * @param networkCodec the network codec
		 * @return this settings object
		 */
		Settings<T> synced(Codec<T> networkCodec);
	}
}
