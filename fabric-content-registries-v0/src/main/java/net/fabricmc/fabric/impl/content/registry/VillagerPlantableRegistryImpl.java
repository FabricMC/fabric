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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.registry.VillagerPlantableRegistry;

public class VillagerPlantableRegistryImpl implements VillagerPlantableRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(VillagerPlantableRegistry.class);

	private final HashMap<Item, BlockState> plantables = new HashMap<>();

	public VillagerPlantableRegistryImpl() {
		add(Items.WHEAT_SEEDS);
		add(Items.POTATO);
		add(Items.CARROT);
		add(Items.BEETROOT_SEEDS);
	}

	@Override
	public void add(ItemConvertible item) {
		if (!(item.asItem() instanceof BlockItem)) {
			throw new IllegalArgumentException("item is not a BlockItem");
		}

		this.add(item, ((BlockItem) item.asItem()).getBlock().getDefaultState());
	}

	@Override
	public void add(ItemConvertible item, BlockState plantState) {
		this.plantables.put(item.asItem(), plantState);

		if (!(plantState.getBlock() instanceof CropBlock)) {
			LOGGER.info("Registered a plantable block that does not extend CropBlock, this block will not be harvestable by villagers.");
		}
	}

	@Override
	public BlockState remove(ItemConvertible item) {
		return this.plantables.remove(item.asItem());
	}

	@Override
	public boolean contains(ItemConvertible item) {
		return this.plantables.containsKey(item.asItem());
	}

	@Override
	public BlockState getPlantState(ItemConvertible item) {
		return this.plantables.get(item.asItem());
	}

	@Override
	public Set<Item> getItems() {
		return this.plantables.keySet();
	}
}
