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

import com.google.common.base.Preconditions;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidPreconditions;
import net.fabricmc.fabric.api.transfer.v1.item.ItemPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.impl.transfer.fluid.SimpleFluidContainingItem;

public final class FluidContainingItems {
	public static ItemApiLookup.ItemApiProvider<Storage<Fluid>, ContainerItemContext> getFullItemProvider(
			Item emptyVariant, Fluid fluid, long numerator, long denominator) {
		ItemPreconditions.notEmpty(emptyVariant);
		FluidPreconditions.notEmpty(fluid);
		Preconditions.checkArgument(numerator > 0);
		Preconditions.checkArgument(denominator > 0);
		return (itemKey, ctx) -> new SimpleFluidContainingItem(ctx, itemKey, emptyVariant, fluid, numerator, denominator);
	}
}
