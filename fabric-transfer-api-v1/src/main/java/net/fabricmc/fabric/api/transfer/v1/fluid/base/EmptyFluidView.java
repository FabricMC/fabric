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

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A fluid variant storage view that is always empty, but may have a nonzero capacity.
 * This can be used to give capacity hints even if the storage is empty.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public class EmptyFluidView implements StorageView<FluidVariant> {
	private final long capacity;

	public EmptyFluidView(long capacity) {
		StoragePreconditions.notNegative(capacity);

		this.capacity = capacity;
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		return 0; // can't extract
	}

	@Override
	public boolean isResourceBlank() {
		return true; // always blank
	}

	@Override
	public FluidVariant getResource() {
		return FluidVariant.blank(); // always blank
	}

	@Override
	public long getAmount() {
		return 0; // always 0
	}

	@Override
	public long getCapacity() {
		return capacity; // always capacity
	}
}
