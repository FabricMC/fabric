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

import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.DebugMessages;

/**
 * A wrapper around a single slot of an inventory.
 * We must ensure that only one instance of this class exists for every inventory slot,
 * or the transaction logic will not work correctly.
 * This is handled by the Map in InventoryStorageImpl.
 */
class InventorySlotWrapper extends SingleStackStorage {
	/**
	 * The strong reference to the InventoryStorageImpl ensures that the weak value doesn't get GC'ed when individual slots are still being accessed.
	 */
	private final InventoryStorageImpl storage;
	final int slot;
	private final SpecialLogicInventory specialInv;
	private ItemStack lastReleasedSnapshot = null;

	InventorySlotWrapper(InventoryStorageImpl storage, int slot) {
		this.storage = storage;
		this.slot = slot;
		this.specialInv = storage.inventory instanceof SpecialLogicInventory specialInv ? specialInv : null;
	}

	@Override
	protected ItemStack getStack() {
		return storage.inventory.getStack(slot);
	}

	@Override
	protected void setStack(ItemStack stack) {
		if (specialInv == null) {
			storage.inventory.setStack(slot, stack);
		} else {
			specialInv.fabric_setSuppress(true);

			try {
				storage.inventory.setStack(slot, stack);
			} finally {
				specialInv.fabric_setSuppress(false);
			}
		}
	}

	@Override
	public long insert(ItemVariant insertedVariant, long maxAmount, TransactionContext transaction) {
		if (!canInsert(slot, ((ItemVariantImpl) insertedVariant).getCachedStack())) {
			return 0;
		}

		long ret = super.insert(insertedVariant, maxAmount, transaction);
		if (specialInv != null && ret > 0) specialInv.fabric_onTransfer(slot, transaction);
		return ret;
	}

	private boolean canInsert(int slot, ItemStack stack) {
		if (storage.inventory instanceof ShulkerBoxBlockEntity shulker) {
			// Shulkers override canInsert but not isValid.
			return shulker.canInsert(slot, stack, null);
		} else {
			return storage.inventory.isValid(slot, stack);
		}
	}

	@Override
	public long extract(ItemVariant variant, long maxAmount, TransactionContext transaction) {
		long ret = super.extract(variant, maxAmount, transaction);
		if (specialInv != null && ret > 0) specialInv.fabric_onTransfer(slot, transaction);
		return ret;
	}

	/**
	 * Special cases because vanilla checks the current stack in the following functions (which it shouldn't):
	 * <ul>
	 *     <li>{@link AbstractFurnaceBlockEntity#isValid(int, ItemStack)}.</li>
	 *     <li>{@link BrewingStandBlockEntity#isValid(int, ItemStack)}.</li>
	 * </ul>
	 */
	@Override
	public int getCapacity(ItemVariant variant) {
		// Special case to limit buckets to 1 in furnace fuel inputs.
		if (storage.inventory instanceof AbstractFurnaceBlockEntity && slot == 1 && variant.isOf(Items.BUCKET)) {
			return 1;
		}

		// Special case to limit brewing stand "bottle inputs" to 1.
		if (storage.inventory instanceof BrewingStandBlockEntity && slot < 3) {
			return 1;
		}

		return Math.min(storage.inventory.getMaxCountPerStack(), variant.getItem().getMaxCount());
	}

	// We override updateSnapshots to also schedule a markDirty call for the backing inventory.
	@Override
	public void updateSnapshots(TransactionContext transaction) {
		storage.markDirtyParticipant.updateSnapshots(transaction);
		super.updateSnapshots(transaction);

		// For chests: also schedule a markDirty call for the other half
		if (storage.inventory instanceof ChestBlockEntity chest && chest.getCachedState().get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
			BlockPos otherChestPos = chest.getPos().offset(ChestBlock.getFacing(chest.getCachedState()));

			if (chest.getWorld().getBlockEntity(otherChestPos) instanceof ChestBlockEntity otherChest) {
				((InventoryStorageImpl) InventoryStorageImpl.of(otherChest, null)).markDirtyParticipant.updateSnapshots(transaction);
			}
		}
	}

	@Override
	protected void releaseSnapshot(ItemStack snapshot) {
		lastReleasedSnapshot = snapshot;
	}

	@Override
	protected void onFinalCommit() {
		// Try to apply the change to the original stack
		ItemStack original = lastReleasedSnapshot;
		ItemStack currentStack = getStack();

		if (storage.inventory instanceof SpecialLogicInventory specialLogicInv) {
			specialLogicInv.fabric_onFinalCommit(slot, original, currentStack);
		}

		if (!original.isEmpty() && original.getItem() == currentStack.getItem()) {
			// None is empty and the items match: just update the amount and NBT, and reuse the original stack.
			original.setCount(currentStack.getCount());
			original.setNbt(currentStack.hasNbt() ? currentStack.getNbt().copy() : null);
			setStack(original);
		} else {
			// Otherwise assume everything was taken from original so empty it.
			original.setCount(0);
		}
	}

	@Override
	public String toString() {
		return "InventorySlotWrapper[%s#%d]".formatted(DebugMessages.forInventory(storage.inventory), slot);
	}
}
