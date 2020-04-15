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

package net.fabricmc.fabric.impl.content.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.content.registry.v1.util.Taggable2ObjectMap;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

public class Taggable2ObjectMapRegistryImpl<K, V> implements Taggable2ObjectMap<K, V>, SimpleSynchronousResourceReloadListener {
	private final Identifier reloadIdentifier;
	private static final Collection<Identifier> RELOAD_DEPS = Collections.singletonList(ResourceReloadListenerKeys.TAGS);

	private boolean tagsPresent = false;
	private final List<Runnable> loader = new LinkedList<>();
	private final Map<K, V> restorer = new HashMap<>();
	private final BiConsumer<K, V> putter;
	private final Consumer<K> remover;
	private final Function<K, V> getter;

	Taggable2ObjectMapRegistryImpl(String name, BiConsumer<K, V> putter, Consumer<K> remover, Function<K, V> getter) {
		reloadIdentifier = new Identifier("fabric:private/" + name);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this);
		this.putter = putter;
		this.remover = remover;
		this.getter = getter;
	}

	@Override
	public void apply(ResourceManager manager) {
		reload();
		tagsPresent = true;
	}

	private void reload() {
		restorer.forEach(putter);
		loader.forEach(Runnable::run);
	}

	@Override
	public V get(K key) {
		return getter.apply(key);
	}

	@Override
	public void add(K key, V value) {
		loader.add(
				() -> {
					restorer.put(key, get(key));
					putter.accept(key, value);
				}
		);

		if (tagsPresent) {
			reload();
		}
	}

	@Override
	public void add(Tag<K> tag, V value) {
		loader.add(
				() -> {
					for (K key : tag.values()) {
						restorer.put(key, get(key));
						putter.accept(key, value);
					}
				}
		);

		if (tagsPresent) {
			reload();
		}
	}

	@Override
	public void remove(K key) {
		loader.add(
				() -> {
					restorer.put(key, get(key));
					remover.accept(key);
				}
		);

		if (tagsPresent) {
			reload();
		}
	}

	@Override
	public void remove(Tag<K> tag) {
		loader.add(
				() -> {
					for (K key : tag.values()) {
						restorer.put(key, get(key));
						remover.accept(key);
					}
				}
		);

		if (tagsPresent) {
			reload();
		}
	}

	@Override
	public Identifier getFabricId() {
		return reloadIdentifier;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return RELOAD_DEPS;
	}
}
