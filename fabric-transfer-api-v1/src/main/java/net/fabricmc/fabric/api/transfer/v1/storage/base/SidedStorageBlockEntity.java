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

package net.fabricmc.fabric.api.transfer.v1.storage.base;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

/**
 * <b>Optional</b> helper class that can be implemented on block entities that wish to provide a {@linkplain FluidStorage#SIDED sided fluid storage}
 * and/or a {@linkplain ItemStorage#SIDED sided item storage} without having to register a provider for each block entity type.
 *
 * <p>How it works is that fabric registers fallback providers for instances of this interface.
 * This can be used for convenient Storage registration, but please always use the SIDED lookups for queries:
 * <pre>{@code
 * Storage<FluidStorage> maybeFluidStorage = FluidStorage.SIDED.find(world, pos, direction);
 * if (maybeFluidStorage != null) {
 *     // use it
 * }
 * Storage<ItemStorage> maybeItemStorage = ItemStorage.SIDED.find(world, pos, direction);
 * if (maybeItemStorage != null) {
 *     // use it
 * }
 * }</pre>
 */
public interface SidedStorageBlockEntity {
	/**
	 * Return a fluid storage if available on the queried side, or null otherwise.
	 */
	@ApiStatus.OverrideOnly
	@Nullable
	default Storage<FluidVariant> getFluidStorage(Direction side) {
		return null;
	}

	/**
	 * Return an item storage if available on the queried side, or null otherwise.
	 */
	@ApiStatus.OverrideOnly
	@Nullable
	default Storage<ItemVariant> getItemStorage(Direction side) {
		return null;
	}
}
