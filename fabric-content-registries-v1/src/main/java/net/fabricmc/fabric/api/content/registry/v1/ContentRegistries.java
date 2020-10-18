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

package net.fabricmc.fabric.api.content.registry.v1;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;

import net.fabricmc.fabric.api.content.registry.v1.util.ContentRegistry;
import net.fabricmc.fabric.api.content.registry.v1.util.ItemContentRegistry;
import net.fabricmc.fabric.impl.content.registry.CompostableItemRegistryImpl;
import net.fabricmc.fabric.impl.content.registry.FlammableBlockRegistryImpl;
import net.fabricmc.fabric.impl.content.registry.FlattenableBlockRegistryImpl;
import net.fabricmc.fabric.impl.content.registry.FuelItemRegistryImpl;
import net.fabricmc.fabric.impl.content.registry.StrippableBlockRegistryImpl;
import net.fabricmc.fabric.impl.content.registry.TillableBlockRegistryImpl;

public final class ContentRegistries {
	private ContentRegistries() {
	}

	/**
	 * A registry of items that may be composed by a {@link net.minecraft.block.ComposterBlock composter}.
	 *
	 * <p>When registering an item that is considered compostable, the registry entry specifies a value between {@code 0.0F} and {@code 1.0F}
	 * that determines the chance of increasing a composter's {@link net.minecraft.state.property.Properties#LEVEL_8 level}.
	 */
	public static final ItemContentRegistry<Float> COMPOSTABLE_ITEM = CompostableItemRegistryImpl.INSTANCE;

	/**
	 * Registry of Blocks that can be set on fire by {@link net.minecraft.block.Blocks#FIRE}.
	 *
	 * <p>Note: blocks wanting to be a base block for {@link net.minecraft.block.Blocks#SOUL_FIRE} should use the
	 * {@link net.minecraft.tag.BlockTags#SOUL_FIRE_BASE_BLOCKS} tag.
	 */
	public static final FlammableBlockRegistry FLAMMABLE_FROM_FIRE = FlammableBlockRegistryImpl.FIRE_INSTANCE;

	/**
	 * Registry of Blocks that when flattened (right-clicked with shovel) turn into a specific BlockState.
	 */
	public static final ContentRegistry<Block, BlockState> FLATTENABLE_BLOCK = FlattenableBlockRegistryImpl.INSTANCE;

	/**
	 * Registry of Items that can burn as a fuel for 0-32767 in-game ticks.
	 */
	public static final ItemContentRegistry<Integer> FUEL_ITEM = FuelItemRegistryImpl.INSTANCE;

	/**
	 * Registry of Blocks that when stripped turn into a specific Block.
	 *
	 * <p>Note: Both KEY and VALUE must have the {@link PillarBlock#AXIS} property!
	 */
	public static final ContentRegistry<Block, Block> STRIPPABLE_BLOCK = StrippableBlockRegistryImpl.INSTANCE;

	/**
	 * Registry of Blocks that when tilled turn into a specific BlockState.
	 */
	public static final ContentRegistry<Block, BlockState> TILLABLE_BLOCK = TillableBlockRegistryImpl.INSTANCE;
}
