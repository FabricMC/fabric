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

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.registry.SpreadableBlockRegistry;

public final class SpreadableBlockRegistryImpl implements SpreadableBlockRegistry {
	private static final Map<Block, Identifier> SPREADABLE_TYPE;
	private static final Map<Identifier, SpreadableBlockRegistry> REGISTRIES;

	static {
		SPREADABLE_TYPE = new ConcurrentHashMap<>();
		REGISTRIES = new ConcurrentHashMap<>();

		SPREADABLE_TYPE.put(Blocks.GRASS_BLOCK, SpreadableBlockRegistry.GRASS);
		SPREADABLE_TYPE.put(Blocks.MYCELIUM, SpreadableBlockRegistry.MYCELIUM);

		REGISTRIES.put(SpreadableBlockRegistry.GRASS, new SpreadableBlockRegistryImpl(SpreadableBlockRegistry.GRASS));
		REGISTRIES.put(SpreadableBlockRegistry.MYCELIUM, new SpreadableBlockRegistryImpl(SpreadableBlockRegistry.MYCELIUM));
	}

	public static SpreadableBlockRegistry getOrCreateInstance(Identifier type) {
		return REGISTRIES.computeIfAbsent(type, SpreadableBlockRegistryImpl::new);
	}

	public static SpreadableBlockRegistry getInstanceBySpreadable(BlockState blockState) {
		if (blockState == null) {
			return null;
		}

		return getInstanceBySpreadable(blockState.getBlock());
	}

	public static SpreadableBlockRegistry getInstanceBySpreadable(Block block) {
		if (block != null && SPREADABLE_TYPE.containsKey(block)) {
			return REGISTRIES.get(SPREADABLE_TYPE.get(block));
		}

		return null;
	}

	private final Identifier registryId;
	private final Map<Block, BlockState> replaceMap = new ConcurrentHashMap<>();

	public SpreadableBlockRegistryImpl(Identifier id) {
		Objects.requireNonNull(id, "registry id cannot be null");
		this.registryId = id;
	}

	public BlockState get(BlockState bareBlockState) {
		if (bareBlockState == null) {
			return null;
		}

		return replaceMap.get(bareBlockState.getBlock());
	}

	@Override
	public BlockState get(Block bareBlock) {
		if (bareBlock == null) {
			return null;
		}

		return replaceMap.get(bareBlock);
	}

	@Override
	public void add(Block bareBlock, BlockState spreadBlock) {
		Objects.requireNonNull(bareBlock, "bare block cannot be null");
		Objects.requireNonNull(spreadBlock, "spread block cannot be null");

		SPREADABLE_TYPE.putIfAbsent(spreadBlock.getBlock(), this.registryId);
		replaceMap.put(bareBlock, spreadBlock);
	}

	@Override
	public void add(TagKey<Block> bareBlockTag, BlockState spreadBlock) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}

	@Override
	public void remove(Block bareBlock) {
		Objects.requireNonNull(bareBlock, "bare block cannot be null");
		replaceMap.remove(bareBlock);
	}

	@Override
	public void remove(TagKey<Block> bareBlockTag) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}

	@Override
	public void clear(Block bareBlock) {
		remove(bareBlock);
	}

	@Override
	public void clear(TagKey<Block> bareBlockTag) {
		remove(bareBlockTag);
	}
}
