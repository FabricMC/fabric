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

package net.fabricmc.fabric.api.transfer.v1.fluid.base;

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.nbt.NbtCompound;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;

/**
 * A storage that can store a single fluid variant at any given time.
 * Implementors should at least override {@link #getCapacity(TransferVariant) getCapacity(FluidVariant)},
 * and probably {@link #onFinalCommit} as well for {@code markDirty()} and similar calls.
 *
 * <p>This is a convenient specialization of {@link SingleVariantStorage} for fluids that additionally offers methods
 * to read the contents of the storage from NBT.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
public abstract class SingleFluidStorage extends SingleVariantStorage<FluidVariant> {
	/**
	 * Create a fluid storage with a fixed capacity and a change handler.
	 *
	 * @param capacity Fixed capacity of the fluid storage. Must be non-negative.
	 * @param onChange Change handler, generally for {@code markDirty()} or similar calls. May not be null.
	 */
	public static SingleFluidStorage withFixedCapacity(long capacity, Runnable onChange) {
		StoragePreconditions.notNegative(capacity);
		Objects.requireNonNull(onChange, "onChange may not be null");

		return new SingleFluidStorage() {
			@Override
			protected long getCapacity(FluidVariant variant) {
				return capacity;
			}

			@Override
			protected void onFinalCommit() {
				onChange.run();
			}
		};
	}

	@Override
	protected final FluidVariant getBlankVariant() {
		return FluidVariant.blank();
	}

	/**
	 * Simple implementation of reading from NBT, to match what is written by {@link #writeNbt}.
	 * Other formats are allowed, this is just a suggestion.
	 */
	public void readNbt(NbtCompound nbt) {
		variant = FluidVariant.fromNbt(nbt.getCompound("variant"));
		amount = nbt.getLong("amount");
	}
}
