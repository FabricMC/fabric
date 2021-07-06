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

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * Wrapper around the cursor slot of a screen handler.
 */
public class CursorSlotWrapper extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {
	private static final Map<ScreenHandler, CursorSlotWrapper> WRAPPERS = new MapMaker().weakValues().makeMap();

	public static CursorSlotWrapper get(ScreenHandler screenHandler) {
		return WRAPPERS.computeIfAbsent(screenHandler, CursorSlotWrapper::new);
	}

	private final ScreenHandler screenHandler;

	private CursorSlotWrapper(ScreenHandler screenHandler) {
		this.screenHandler = screenHandler;
	}

	@Override
	public long insert(ItemVariant ItemVariant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(ItemVariant, maxAmount);
		ItemStack stack = screenHandler.getCursorStack();
		int inserted = (int) Math.min(maxAmount, Math.min(64, ItemVariant.getItem().getMaxCount()) - stack.getCount());

		if (stack.isEmpty()) {
			ItemStack keyStack = ItemVariant.toStack(inserted);
			this.updateSnapshots(transaction);
			screenHandler.setCursorStack(keyStack);
			return inserted;
		} else if (ItemVariant.matches(stack)) {
			this.updateSnapshots(transaction);
			stack.increment(inserted);
			return inserted;
		}

		return 0;
	}

	@Override
	public long extract(ItemVariant ItemVariant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(ItemVariant, maxAmount);
		ItemStack stack = screenHandler.getCursorStack();

		if (ItemVariant.matches(stack)) {
			int extracted = (int) Math.min(stack.getCount(), maxAmount);
			this.updateSnapshots(transaction);
			stack.decrement(extracted);
			return extracted;
		}

		return 0;
	}

	@Override
	public ItemVariant getResource() {
		return ItemVariant.of(screenHandler.getCursorStack());
	}

	@Override
	public long getCapacity() {
		return screenHandler.getCursorStack().getMaxCount();
	}

	@Override
	public boolean isResourceBlank() {
		return screenHandler.getCursorStack().isEmpty();
	}

	@Override
	public long getAmount() {
		return screenHandler.getCursorStack().getCount();
	}

	@Override
	protected ItemStack createSnapshot() {
		return screenHandler.getCursorStack().copy();
	}

	@Override
	protected void readSnapshot(ItemStack snapshot) {
		screenHandler.setCursorStack(snapshot);
	}
}
