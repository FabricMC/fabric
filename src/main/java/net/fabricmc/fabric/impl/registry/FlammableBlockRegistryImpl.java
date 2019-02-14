/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.registry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.block.Block;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FlammableBlockRegistryImpl implements FlammableBlockRegistry, IdentifiableResourceReloadListener<Void> {
	private static final FlammableBlockRegistry.Entry REMOVED = new FlammableBlockRegistry.Entry(0, 0);
	private static final Map<Block, FlammableBlockRegistryImpl> REGISTRIES = new HashMap<>();
	private static final Collection<Identifier> RELOAD_DEPS = Collections.singletonList(ResourceReloadListenerKeys.TAGS);
	private static int idCounter = 0;

	private final Map<Block, FlammableBlockRegistry.Entry> registeredEntriesBlock = new HashMap<>();
	private final Map<Tag<Block>, FlammableBlockRegistry.Entry> registeredEntriesTag = new HashMap<>();
	private final Map<Block, FlammableBlockRegistry.Entry> computedEntries = new HashMap<>();
	private final Identifier id;
	private final Block key;
	private boolean tagsPresent = false;

	private FlammableBlockRegistryImpl(Block key) {
		ResourceManagerHelper.get(ResourceType.DATA).addReloadListener(this);
		this.id = new Identifier("fabric:private/fire_registry_" + (++idCounter));
		this.key = key;
	}

	@Override
	public CompletableFuture prepare(ResourceManager var1, Profiler var2) {
		return CompletableFuture.completedFuture(null);
	}

	// TODO: Asynchronous?
	@Override
	public void apply(ResourceManager var1, Void var2, Profiler var3) {
		reload();
		tagsPresent = true;
	}

	private void reload() {
		computedEntries.clear();
		// tags take precedence before blocks
		for (Tag<Block> tag : registeredEntriesTag.keySet()) {
			FlammableBlockRegistry.Entry entry = registeredEntriesTag.get(tag);
			for (Block block : tag.values()) {
				computedEntries.put(block, entry);
			}
		}
		computedEntries.putAll(registeredEntriesBlock);

		/* computedBurnChances.clear();
		computedSpreadChances.clear();

		for (Block block : computedEntries.keySet()) {
			FlammableBlockRegistry.Entry entry = computedEntries.get(block);
			computedBurnChances.put(block, entry.getBurnChance());
			computedSpreadChances.put(block, entry.getSpreadChance());
		} */
	}

	// User-facing fire registry interface - queries vanilla fire block
	@Override
	public Entry get(Block block) {
		Entry entry = computedEntries.get(block);
		if (entry != null) {
			return entry;
		} else {
			return ((FireBlockHooks) key).fabric_getVanillaEntry(block);
		}
	}

	public Entry getFabric(Block block) {
		return computedEntries.get(block);
	}

	@Override
	public void add(Block block, Entry value) {
		registeredEntriesBlock.put(block, value);

		if (tagsPresent) {
			reload();
		}
	}

	@Override
	public void add(Tag<Block> tag, Entry value) {
		registeredEntriesTag.put(tag, value);

		if (tagsPresent) {
			reload();
		}
	}

	@Override
	public void remove(Block block) {
		add(block, REMOVED);
	}

	@Override
	public void remove(Tag<Block> tag) {
		add(tag, REMOVED);
	}

	@Override
	public void clear(Block block) {
		registeredEntriesBlock.remove(block);

		if (tagsPresent) {
			reload();
		}
	}

	@Override
	public void clear(Tag<Block> tag) {
		registeredEntriesTag.remove(tag);

		if (tagsPresent) {
			reload();
		}
	}

	public static FlammableBlockRegistryImpl getInstance(Block block) {
		if (!(block instanceof FireBlockHooks)) {
			throw new RuntimeException("Not a hookable fire block: " + block);
		}

		return REGISTRIES.computeIfAbsent(block, FlammableBlockRegistryImpl::new);
	}

	@Override
	public Identifier getFabricId() {
		return id;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return RELOAD_DEPS;
	}
}
