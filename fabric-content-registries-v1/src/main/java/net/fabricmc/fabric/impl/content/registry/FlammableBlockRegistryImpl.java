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

import com.google.common.base.Preconditions;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.content.registry.v1.FlammableBlockRegistry;
import net.fabricmc.fabric.mixin.content.registry.FireBlockAccessor;

public class FlammableBlockRegistryImpl extends ContentRegistryImpl<Block, FlammableBlockRegistry.Entry> implements FlammableBlockRegistry {
	public static final FlammableBlockRegistry FIRE_INSTANCE = new FlammableBlockRegistryImpl(Blocks.FIRE, "fire");

	public FlammableBlockRegistryImpl(Block fireBlock, String prefix) {
		super(prefix + "_flammable_block_registry", (block, entry) -> ((FireBlock) fireBlock).registerFlammableBlock(block, entry.getBurnChance(), entry.getSpreadChance()), block -> ((FireBlock) fireBlock).registerFlammableBlock(block, 0, 0), block -> new Entry(((FireBlockAccessor) fireBlock).getBurnChances().getInt(block), ((FireBlockAccessor) fireBlock).getSpreadChances().getInt(block)));
		Preconditions.checkArgument(fireBlock instanceof FireBlock, "Block " + Registry.BLOCK.getId(fireBlock) + " is not a FireBlock and a FlammablcBlockRegistry cannot be created for it");
	}
}
