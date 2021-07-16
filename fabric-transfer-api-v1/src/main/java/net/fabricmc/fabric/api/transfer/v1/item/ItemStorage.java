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

import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.mixin.transfer.DoubleInventoryAccessor;

/**
 * Access to {@link Storage Storage&lt;ItemVariant&gt;} instances.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public final class ItemStorage { // TODO: update javadoc...
	/**
	 * Sided block access to item variant storages.
	 * The {@code Direction} parameter may never be null.
	 * Refer to {@link BlockApiLookup} for documentation on how to use this field.
	 *
	 * <p>When the operations supported by a storage change,
	 * that is if the return value of {@link Storage#supportsInsertion} or {@link Storage#supportsExtraction} changes,
	 * the storage should notify its neighbors with a block update so that they can refresh their connections if necessary.
	 *
	 * <p>A fallback provider that wraps {@link Inventory} and {@link SidedInventory} implementations is automatically registered by Fabric API.
	 * Blocks implementing {@link InventoryProvider} are automatically supported as well.
	 * In all of these cases, an {@link InventoryStorage} is returned from the query.
	 *
	 * <p>Hoppers and droppers will interact with storages exposed through this lookup, thus implementing one of the vanilla APIs
	 * is NOT NECESSARY anymore, although it can be convenient to implement, for example with {@link SimpleInventory}.
	 * Do note that using the vanilla API may cause issues for complicated inventories:
	 *
	 * <p>Per the remark in {@link InventoryStorage}:
	 * <b>Inventories that don't own their slots or have a dynamic number of slots</b> must ensure they are <b>NOT exposed</b> directly through this lookup,
	 * for example by using {@code Storage<ItemVariant>} directly or by making sure their {@code BlockEntity} does NOT implement {@code Inventory} directly.
	 */
	public static final BlockApiLookup<Storage<ItemVariant>, Direction> SIDED =
			BlockApiLookup.get(new Identifier("fabric:sided_item_storage"), Storage.asClass(), Direction.class);

	private ItemStorage() {
	}

	static {
		// TODO: composter impl

		// Load Inventory fallback.
		ItemStorage.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
			Inventory inventoryToWrap = null;

			if (blockEntity instanceof Inventory inventory) {
				if (blockEntity instanceof ChestBlockEntity && state.getBlock() instanceof ChestBlock chestBlock) {
					inventoryToWrap = ChestBlock.getInventory(chestBlock, state, world, pos, true);

					// For double chests, we need to retrieve a wrapper for each part separately.
					if (inventoryToWrap instanceof DoubleInventoryAccessor accessor) {
						Storage<ItemVariant> first = InventoryStorage.of(accessor.fabric_getFirst(), direction);
						Storage<ItemVariant> second = InventoryStorage.of(accessor.fabric_getSecond(), direction);

						return new CombinedStorage<>(List.of(first, second));
					}
				} else {
					inventoryToWrap = inventory;
				}
			}

			return inventoryToWrap != null ? InventoryStorage.of(inventoryToWrap, direction) : null;
		});
	}
}
