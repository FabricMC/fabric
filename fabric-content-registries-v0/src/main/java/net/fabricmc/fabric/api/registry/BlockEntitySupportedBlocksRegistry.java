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

package net.fabricmc.fabric.api.registry;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

import net.fabricmc.fabric.impl.content.registry.BlockEntityTypeHooks;

/**
 * A registry that allows mods to modify this list of supported blocks per block entity type. For example, a custom sign
 * block will not work because Minecraft uses a hardcoded list of blocks that are allowed to host a sign block entity.
 * Using this registry, custom sign blocks can be registered as valid blocks for a sign block entity.
 */
public final class BlockEntitySupportedBlocksRegistry {
	private BlockEntitySupportedBlocksRegistry() {
	}

	/**
	 * Adds additional blocks that the given block entity type supports.
	 *
	 * @param type            The block entity type to add support for.
	 * @param supportedBlocks The blocks to add as additional supported blocks.
	 */
	public static void register(BlockEntityType<?> type, Block... supportedBlocks) {
		Objects.requireNonNull(type, "Block entity type cannot be null!");
		Objects.requireNonNull(supportedBlocks, "Supported blocks cannot be null!");

		for (Block block : supportedBlocks) {
			Objects.requireNonNull(block, "A supported block cannot be null!");
		}

		((BlockEntityTypeHooks) type).fabric$addSupportedBlocks(supportedBlocks);
	}
}
