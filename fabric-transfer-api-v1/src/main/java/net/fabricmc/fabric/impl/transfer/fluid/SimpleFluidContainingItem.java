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
import net.fabricmc.fabric.api.transfer.v1.base.FixedDenominatorStorageFunction;
import net.fabricmc.fabric.api.transfer.v1.base.FixedDenominatorStorageView;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageFunction;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class SimpleFluidContainingItem implements Storage<Fluid>, FixedDenominatorStorageView<Fluid> {
	private final Fluid fluid;
	private final long numerator;
	private final long denominator;
	private final StorageFunction<Fluid> extractionFunction;

	public SimpleFluidContainingItem(ContainerItemContext ctx, ItemKey sourceKey, Item emptyVariant, Fluid fluid, long numerator, long denominator) {
		this.fluid = fluid;
		this.numerator = numerator;
		this.denominator = denominator;
		ItemKey targetKey = ItemKey.of(emptyVariant, sourceKey.copyTag());
		this.extractionFunction = new FixedDenominatorStorageFunction<Fluid>() {
			@Override
			public long denominator() {
				return denominator;
			}

			@Override
			public long applyFixedDenominator(Fluid resource, long maxAmount, Transaction tx) {
				FluidPreconditions.notEmptyNotNegative(resource, maxAmount);

				if (maxAmount >= numerator && resource == fluid && ctx.getCount(tx) > 0) {
					if (ctx.transform(1, targetKey, tx)) {
						return numerator;
					}
				}

				return 0;
			}
		};
	}

	@Override
	public Fluid resource() {
		return fluid;
	}

	@Override
	public long denominator() {
		return denominator;
	}

	@Override
	public long amountFixedDenominator() {
		return numerator;
	}

	@Override
	public StorageFunction<Fluid> extractionFunction() {
		return extractionFunction;
	}

	@Override
	public boolean forEach(Visitor<Fluid> visitor) {
		// note: fluid may not be empty, so no need to check
		return visitor.visit(this);
	}
}
