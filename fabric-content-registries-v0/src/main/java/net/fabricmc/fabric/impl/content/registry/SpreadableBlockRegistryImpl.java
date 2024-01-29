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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.registry.SpreadableBlockRegistry;

public final class SpreadableBlockRegistryImpl implements SpreadableBlockRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpreadableBlockRegistryImpl.class);

	private static final Map<Block, Identifier> SPREADABLE_TYPE = new IdentityHashMap<>();
	private static final Map<Identifier, SpreadableBlockRegistry> REGISTRIES = new HashMap<>();

	static {
		SPREADABLE_TYPE.put(Blocks.GRASS_BLOCK, SpreadableBlockRegistry.GRASS);
		SPREADABLE_TYPE.put(Blocks.MYCELIUM, SpreadableBlockRegistry.MYCELIUM);

		REGISTRIES.put(SpreadableBlockRegistry.GRASS, new SpreadableBlockRegistryImpl(SpreadableBlockRegistry.GRASS));
		REGISTRIES.put(SpreadableBlockRegistry.MYCELIUM, new SpreadableBlockRegistryImpl(SpreadableBlockRegistry.MYCELIUM));
	}

	public static SpreadableBlockRegistry getOrCreateInstance(Identifier type) {
		return REGISTRIES.computeIfAbsent(type, SpreadableBlockRegistryImpl::new);
	}

	public static @Nullable SpreadableBlockRegistry getInstanceBySpreadable(BlockState blockState) {
		return getInstanceBySpreadable(blockState.getBlock());
	}

	public static @Nullable SpreadableBlockRegistry getInstanceBySpreadable(Block block) {
		if (!SPREADABLE_TYPE.containsKey(block)) {
			return null;
		}

		return REGISTRIES.get(SPREADABLE_TYPE.get(block));
	}

	public static ImmutableMap<Block, Identifier> getSpreadableTypes() {
		return ImmutableMap.copyOf(SPREADABLE_TYPE);
	}

	private final Identifier registryId;
	private final Map<Block, BlockState> replacements = new IdentityHashMap<>();

	private SpreadableBlockRegistryImpl(Identifier id) {
		Objects.requireNonNull(id, "registry id cannot be null");

		this.registryId = id;
	}

	@Override
	public @Nullable BlockState get(BlockState bareBlockState) {
		Objects.requireNonNull(bareBlockState, "bare block cannot be null");

		return get(bareBlockState.getBlock());
	}

	@Override
	public @Nullable BlockState get(Block bareBlock) {
		Objects.requireNonNull(bareBlock, "bare block cannot be null");

		return replacements.get(bareBlock);
	}

	@Override
	public void add(Block bareBlock, BlockState spreadBlock) {
		Objects.requireNonNull(bareBlock, "bare block cannot be null");
		Objects.requireNonNull(spreadBlock, "spread block cannot be null");

		Identifier oldRegistryId = SPREADABLE_TYPE.put(spreadBlock.getBlock(), this.registryId);

		if (oldRegistryId != null && oldRegistryId != this.registryId) {
			throw new UnsupportedOperationException(String.format(
					"Spreadable block %s added to two registries; was %s now %s",
					spreadBlock, oldRegistryId, this.registryId
			));
		}

		BlockState oldSpreadBlock = replacements.put(bareBlock, spreadBlock);

		if (oldSpreadBlock != null && oldSpreadBlock != spreadBlock) {
			LOGGER.debug("Replaced spreadable block for bare block {}; was {} now {}", bareBlock, oldSpreadBlock, spreadBlock);
		}
	}

	@Override
	public void add(TagKey<Block> bareBlockTag, BlockState spreadBlock) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}

	@Override
	public void remove(Block bareBlock) {
		Objects.requireNonNull(bareBlock, "bare block cannot be null");

		replacements.remove(bareBlock);
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
