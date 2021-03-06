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

package net.fabricmc.fabric.test.transfer.fluid;

import java.util.Iterator;

import net.minecraft.fluid.Fluids;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class CreativeFluidStorage implements ExtractionOnlyStorage<FluidVariant>, StorageView<FluidVariant> {
	public static final CreativeFluidStorage WATER = new CreativeFluidStorage(FluidVariant.of(Fluids.WATER));
	public static final CreativeFluidStorage LAVA = new CreativeFluidStorage(FluidVariant.of(Fluids.LAVA));

	private final FluidVariant infiniteFluid;

	private CreativeFluidStorage(FluidVariant infiniteFluid) {
		this.infiniteFluid = infiniteFluid;
	}

	@Override
	public boolean isResourceBlank() {
		return infiniteFluid.isBlank();
	}

	@Override
	public FluidVariant getResource() {
		return infiniteFluid;
	}

	@Override
	public long getAmount() {
		return Long.MAX_VALUE;
	}

	@Override
	public long getCapacity() {
		return getAmount();
	}

	@Override
	public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);

		if (resource.equals(infiniteFluid)) {
			return maxAmount;
		} else {
			return 0;
		}
	}

	@Override
	public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction) {
		return SingleViewIterator.create(this, transaction);
	}

	@Override
	public long getVersion() {
		return 0;
	}
}
