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
		super(prefix + "_flammable_block_registry");

		Preconditions.checkArgument(fireBlock instanceof FireBlock, "Block " + Registry.BLOCK.getId(fireBlock) + " is not a FireBlock and a FlammableBlockRegistry cannot be created for it");
	}

	@Override
	protected void remover(Block key) {
		FireBlockAccessor.registerFlammableBlock(key, 0, 0);
	}

	@Override
	protected void putter(Block key, Entry value) {
		FireBlockAccessor.registerFlammableBlock(key, value.getBurnChance(), value.getSpreadChance());
	}

	@Override
	protected Entry getter(Block key) {
		FireBlockAccessor fireBlockAccessor = (FireBlockAccessor) key;
		return new Entry(fireBlockAccessor.getBurnChances().getInt(key), fireBlockAccessor.getSpreadChances().getInt(key));
	}
}
