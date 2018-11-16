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

package net.fabricmc.fabric.registry.listeners;

import net.fabricmc.fabric.registry.ExtendedIdList;
import net.fabricmc.fabric.registry.RegistryListener;
import net.minecraft.item.Item;
import net.minecraft.item.block.ItemBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BootstrapItemRegistryListener implements RegistryListener<Item> {
	@Override
	public void beforeCleared(Registry<Item> registry) {
		Item.BLOCK_ITEM_MAP.clear();
	}

	@Override
	public void beforeRegistered(Registry<Item> registry, int id, Identifier identifier, Item object, boolean isNew) {
		// refer net.minecraft.item.Items
		if (object instanceof ItemBlock) {
			((ItemBlock) object).method_7713(Item.BLOCK_ITEM_MAP, object);
		}
	}
}
