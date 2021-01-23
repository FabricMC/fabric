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

package net.fabricmc.fabric.api.transfer.v1.item;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookupRegistry;
import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public final class ItemApi {
	public static final BlockApiLookup<Storage<ItemKey>, Direction> SIDED =
			BlockApiLookupRegistry.getLookup(new Identifier("fabric:sided_item_api"), Storage.asClass(), Direction.class);

	private ItemApi() {
	}

	static {
		// Load generic vanilla api fallback
		ItemApi.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
			Block block = state.getBlock();
			Inventory inventory = null;

			if (block instanceof InventoryProvider) {
				inventory = ((InventoryProvider) block).getInventory(state, world, pos);
			} else if (blockEntity instanceof Inventory) {
				inventory = (Inventory) blockEntity;

				if (blockEntity instanceof ChestBlockEntity && block instanceof ChestBlock) {
					inventory = ChestBlock.getInventory((ChestBlock) block, state, world, pos, true);
				}
			}

			return inventory == null ? null : InventoryWrappers.of(inventory, direction);
		});
	}
}
