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

package net.fabricmc.fabric.api.transfer.v1.fluid;

import com.google.common.base.Preconditions;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookupRegistry;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookupRegistry;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.impl.transfer.fluid.EmptyItemsRegistry;
import net.fabricmc.fabric.impl.transfer.fluid.FluidApiImpl;
import net.fabricmc.fabric.impl.transfer.fluid.SimpleFluidContainingItem;

public final class FluidApi {
	public static final BlockApiLookup<Storage<Fluid>, Direction> SIDED =
			BlockApiLookupRegistry.getLookup(new Identifier("fabric:sided_fluid_api"), Storage.asClass(), Direction.class);

	public static final ItemApiLookup<Storage<Fluid>, ContainerItemContext> ITEM =
			ItemApiLookupRegistry.getLookup(new Identifier("fabric:fluid_api"), Storage.asClass(), ContainerItemContext.class);

	/**
	 * Register an item that contains a fluid and can be emptied of it entirely.
	 * @param fullItem The item that contains the fluid.
	 * @param emptyItem The emptied item.
	 * @param fluid The contained fluid. May not be empty.
	 * @param amount The amount of fluid in the full item. Must be positive.
	 */
	// TODO: document conflicts
	public static void registerFullItem(Item fullItem, Item emptyItem, Fluid fluid, long amount) {
		ItemPreconditions.notEmpty(fullItem);
		FluidPreconditions.notEmpty(fluid);
		Preconditions.checkArgument(amount > 0);

		ITEM.register((key, ctx) -> new SimpleFluidContainingItem(ctx, key, emptyItem, fluid, amount), fullItem);
	}

	/**
	 * Register an item that is empty, and may be filled with some fluid entirely.
	 */
	// TODO: document params and conflicts
	// TODO: pick parameter order, probably the same for both methods?
	public static void registerEmptyItem(Item emptyItem, Item fullItem, Fluid fluid, long amount) {
		ItemPreconditions.notEmpty(emptyItem);
		ItemPreconditions.notEmpty(fullItem);
		FluidPreconditions.notEmpty(fluid);
		Preconditions.checkArgument(amount > 0);

		EmptyItemsRegistry.registerEmptyItem(emptyItem, fullItem, fluid, amount);
	}

	// TODO: potion handling

	private FluidApi() {
	}

	static {
		FluidApiImpl.init();
	}
}
