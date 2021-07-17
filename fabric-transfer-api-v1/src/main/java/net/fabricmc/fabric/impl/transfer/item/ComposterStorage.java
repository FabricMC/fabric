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

package net.fabricmc.fabric.impl.transfer.item;

import java.util.Map;

import com.google.common.collect.MapMaker;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.block.InventoryProvider;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * Manages access to storage implementations for composters.
 * For simplicity, querying a new wrapper simply invalidates the previous one.
 * If this becomes an issue in practice, a more involved implementation strategy will be used.
 */
public class ComposterStorage {
	// Record is used for convenient constructor, hashcode and equals implementations.
	private record WorldLocation(World world, BlockPos pos) {
	}

	private static final Map<WorldLocation, SidedInventory> PREVIOUS_INVENTORIES = new MapMaker().weakValues().makeMap();

	@Nullable
	public static Storage<ItemVariant> find(World world, BlockPos pos, Direction direction) {
		// Inside a transaction, we don't know if the previous wrapper is still being used or not.
		if (Transaction.isOpen()) return null;

		// Invalidate the previous wrapper by calling markDirty().
		WorldLocation loc = new WorldLocation(world, pos);
		SidedInventory previousInventory = PREVIOUS_INVENTORIES.get(loc);

		if (previousInventory != null) {
			previousInventory.markDirty();
		}

		// Return a new wrapper.
		SidedInventory newInventory = ((InventoryProvider) Blocks.COMPOSTER).getInventory(world.getBlockState(pos), world, pos);
		PREVIOUS_INVENTORIES.put(loc, newInventory);
		return InventoryStorage.of(newInventory, direction);
	}
}
