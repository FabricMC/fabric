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

import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookupRegistry;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookupRegistry;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public class FluidApi {
	public static final BlockApiLookup<Storage<Fluid>, Direction> SIDED =
			BlockApiLookupRegistry.getLookup(new Identifier("fabric:sided_fluid_api"), Storage.asClass(), Direction.class);

	public static final ItemApiLookup<Storage<Fluid>, ContainerItemContext> ITEM =
			ItemApiLookupRegistry.getLookup(new Identifier("fabric:fluid_api"), Storage.asClass(), ContainerItemContext.class);

	private FluidApi() {
	}

	static {
		// TODO compat with cauldrons and fluid containing items (and use it for buckets and bottles)
	}
}
