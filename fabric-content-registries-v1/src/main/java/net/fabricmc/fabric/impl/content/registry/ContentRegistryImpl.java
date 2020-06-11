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

import net.fabricmc.fabric.api.content.registry.v1.util.ContentRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

public class ContentRegistryImpl<K, V> implements ContentRegistry<K, V>, SimpleSynchronousResourceReloadListener {
	private final Identifier reloadIdentifier;
	private static final Collection<Identifier> RELOAD_DEPS = Collections.singletonList(ResourceReloadListenerKeys.TAGS);

	private boolean tagsPresent = false;
	private final List<Runnable> processor = new LinkedList<>(); // This is to preserve the order of how things are added and removed. This would be a lot simpler if we did not have to deal with Tags
	private final Map<K, V> restorer = new HashMap<>(); // Stores the state of the vanilla map without any fabric modifications so everything can be undone on resource reload
	private final BiConsumer<K, V> putter; // Used to add registry values to whatever stores them
	private final Consumer<K> remover; // Used to remove registry values from whatever stores them
	private final Function<K, V> getter; // Used to get registry values from whatever stores them

	protected ContentRegistryImpl(String name, BiConsumer<K, V> putter, Consumer<K> remover, Function<K, V> getter) {
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
		processor.forEach(Runnable::run);
	}

	@Override
	public V get(K key) {
		return getter.apply(key);
	}

	@Override
	public void add(K key, V value) {
		Runnable adder = () -> {
			restorer.computeIfAbsent(key, ContentRegistryImpl.this::get);
			putter.accept(key, value);
		};

		processor.add(adder);

		if (tagsPresent) {
			adder.run();
		}
	}

	@Override
	public void add(Tag<K> tag, V value) {
		Runnable adder = () -> {
			for (K key : tag.values()) {
				restorer.computeIfAbsent(key, ContentRegistryImpl.this::get);
				putter.accept(key, value);
			}
		};

		processor.add(adder);

		if (tagsPresent) {
			adder.run();
		}
	}

	@Override
	public void remove(K key) {
		Runnable remover = () -> {
			restorer.computeIfAbsent(key, ContentRegistryImpl.this::get);
			this.remover.accept(key);
		};

		processor.add(remover);

		if (tagsPresent) {
			remover.run();
		}
	}

	@Override
	public void remove(Tag<K> tag) {
		Runnable remover = () -> {
			for (K key : tag.values()) {
				restorer.computeIfAbsent(key, ContentRegistryImpl.this::get);
				this.remover.accept(key);
			}
		};

		processor.add(remover);

		if (tagsPresent) {
			remover.run();
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
