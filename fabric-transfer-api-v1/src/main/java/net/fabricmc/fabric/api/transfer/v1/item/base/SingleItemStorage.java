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

package net.fabricmc.fabric.api.transfer.v1.item.base;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.nbt.NbtCompound;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;

/**
 * A storage that can store a single item variant at any given time.
 * Implementors should at least override {@link #getCapacity(TransferVariant) getCapacity(ItemVariant)},
 * and probably {@link #onFinalCommit} as well for {@code markDirty()} and similar calls.
 *
 * <p>This is a convenient specialization of {@link SingleVariantStorage} for items that additionally offers methods
 * to read the contents of the storage from NBT.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public abstract class SingleItemStorage extends SingleVariantStorage<ItemVariant> {
	@Override
	protected final ItemVariant getBlankVariant() {
		return ItemVariant.blank();
	}

	/**
	 * Simple implementation of reading from NBT, to match what is written by {@link #writeNbt}.
	 * Other formats are allowed, this is just a suggestion.
	 */
	public void readNbt(NbtCompound nbt) {
		variant = ItemVariant.fromNbt(nbt.getCompound("variant"));
		amount = nbt.getLong("amount");
	}
}
