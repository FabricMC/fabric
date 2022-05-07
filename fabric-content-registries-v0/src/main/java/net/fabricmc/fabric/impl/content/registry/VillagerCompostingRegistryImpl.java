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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.VillagerCompostingRegistry;
import net.fabricmc.fabric.mixin.content.registry.FarmerWorkTaskAccessor;

public class VillagerCompostingRegistryImpl implements VillagerCompostingRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(VillagerCompostingRegistry.class);

	@Override
	public boolean contains(ItemConvertible item) {
		return FarmerWorkTaskAccessor.getCompostables().contains(item.asItem());
	}

	@Override
	public void add(ItemConvertible item) {
		makeListMutable();

		if (CompostingChanceRegistry.INSTANCE.get(item) <= 0.0) {
			LOGGER.warn("Registering non-compostable item {} as a villager compostable.", item.asItem().toString());
		}

		FarmerWorkTaskAccessor.getCompostables().add(item.asItem());
	}

	@Override
	public boolean remove(ItemConvertible item) {
		makeListMutable();

		return FarmerWorkTaskAccessor.getCompostables().remove(item.asItem());
	}

	private static void makeListMutable() {
		List<Item> compostables = FarmerWorkTaskAccessor.getCompostables();

		if (!(compostables instanceof ArrayList)) {
			FarmerWorkTaskAccessor.setCompostables(new ArrayList<>(compostables));
		}
	}
}
