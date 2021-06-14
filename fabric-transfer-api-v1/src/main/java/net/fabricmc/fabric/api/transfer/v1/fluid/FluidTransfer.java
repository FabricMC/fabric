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

import net.minecraft.fluid.Fluids;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

/**
 * Access to {@link Storage Storage&lt;FluidKey&gt;} instances.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
public final class FluidTransfer {
	/**
	 * Sided block access to fluid key storages.
	 * Fluid amounts are always expressed in {@linkplain FluidConstants droplets}.
	 * The {@code Direction} parameter may never be null.
	 * Refer to {@link BlockApiLookup} for documentation on how to use this field.
	 *
	 * <p>When the operations supported by a storage change,
	 * that is if the return value of {@link Storage#supportsInsertion} or {@link Storage#supportsExtraction} changes,
	 * the storage should notify its neighbors with a block update so that they can refresh their connections if necessary.
	 */
	public static final BlockApiLookup<Storage<FluidKey>, Direction> SIDED =
			BlockApiLookup.get(new Identifier("fabric:sided_fluid_storage"), Storage.asClass(), Direction.class);

	private FluidTransfer() {
	}

	static {
		// Initialize vanilla cauldron wrappers
		CauldronFluidContent.getForFluid(Fluids.WATER);
	}
}
