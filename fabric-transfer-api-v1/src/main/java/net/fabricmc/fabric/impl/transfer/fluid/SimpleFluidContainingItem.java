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

package net.fabricmc.fabric.impl.transfer.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class SimpleFluidContainingItem implements Storage<Fluid>, StorageView<Fluid> {
	private final Fluid fluid;
	private final long amount;
	private final ItemKey targetKey;
	private final ContainerItemContext ctx;

	public SimpleFluidContainingItem(ContainerItemContext ctx, ItemKey sourceKey, Item emptyVariant, Fluid fluid, long amount) {
		this.fluid = fluid;
		this.amount = amount;
		this.targetKey = ItemKey.of(emptyVariant, sourceKey.copyTag());
		this.ctx = ctx;
	}

	@Override
	public Fluid resource() {
		return fluid;
	}

	@Override
	public long amount() {
		return amount;
	}

	@Override
	public boolean supportsInsertion() {
		return false;
	}

	@Override
	public long insert(Fluid resource, long maxAmount, Transaction transaction) {
		return 0;
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(Fluid resource, long maxAmount, Transaction transaction) {
		FluidPreconditions.notEmptyNotNegative(resource, maxAmount);

		if (maxAmount >= amount && resource == fluid && ctx.getCount(transaction) > 0) {
			if (ctx.transform(1, targetKey, transaction)) {
				return amount;
			}
		}

		return 0;
	}

	@Override
	public boolean forEach(Visitor<Fluid> visitor, Transaction transaction) {
		// note: fluid may not be empty, so no need to check
		return visitor.accept(this);
	}
}
