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

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

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
 */
public abstract class SingleItemStorage extends SingleVariantStorage<ItemVariant> {
	@Override
	protected final ItemVariant getBlankVariant() {
		return ItemVariant.blank();
	}

	/**
	 * Simple implementation of reading from NBT, to match what is written by {@link #writeNbt}.
	 * Other formats are allowed, this is just a suggestion.
	 */
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
		SingleVariantStorage.readNbt(this, ItemVariant.CODEC, ItemVariant::blank, nbt, wrapperLookup);
	}

	/**
	 * Simple implementation of writing to NBT. Other formats are allowed, this is just a convenient suggestion.
	 */
	public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
		SingleVariantStorage.writeNbt(this, ItemVariant.CODEC, nbt, wrapperLookup);
	}
}
