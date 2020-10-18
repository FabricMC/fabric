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

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.content.registry.v1.util.ContentRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

public abstract class ContentRegistryImpl<K, V> implements ContentRegistry<K, V>, SimpleSynchronousResourceReloadListener {
	private final Identifier reloadIdentifier;
	private static final Collection<Identifier> RELOAD_DEPS = Collections.singletonList(ResourceReloadListenerKeys.TAGS);

	private boolean tagsPresent = false;
	private final List<Runnable> processor = new LinkedList<>(); // This is to preserve the order of how things are added and removed. This would be a lot simpler if we did not have to deal with Tags
	private final Map<K, V> restorer = new HashMap<>(); // Stores the state of the vanilla map without any fabric modifications so everything can be undone on resource reload

	protected ContentRegistryImpl(String name) {
		reloadIdentifier = new Identifier("fabric", "private/" + name);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this);
	}

	protected abstract void remover(K key);
	protected abstract void putter(K key, V value);
	protected abstract V getter(K key);

	@Override
	public void apply(ResourceManager manager) {
		reload();
		tagsPresent = true;
	}

	private void reload() {
		restorer.forEach(this::putter);
		processor.forEach(Runnable::run);
	}

	@Override
	public V get(K key) {
		return getter(key);
	}

	@Override
	public void add(K key, V value) {
		Runnable adder = () -> {
			restorer.computeIfAbsent(key, ContentRegistryImpl.this::get);
			putter(key, value);
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
				putter(key, value);
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
			remover(key);
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
				remover(key);
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
