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

package net.fabricmc.fabric.api.item.v1;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.ItemStack;

/**
 * Calculates the amount of space an item stack occupies inside of a {@link net.minecraft.item.BundleItem bundle}.
 *
 * <p>A bundle's maximum occupancy is 64 which corresponds to the maximum stack size of a normal item.
 * If an item's stack size is not 64, then the occupancy of that specific item is scaled by {@code 64 / item.getMaxStackSize()}.
 * If a bundle contains another bundle, then the occupancy of the bundle is 4 plus the current occupancy of the inner bundle.
 *
 * <p>Bundle occupancy providers can be set with {@link FabricItemSettings#bundleOccupancy(BundleOccupancyProvider)}.
 *
 * @deprecated Experimental feature, may be removed or changed without further notice: Snapshot feature.
 */
@ApiStatus.Experimental
@Deprecated
@FunctionalInterface
public interface BundleOccupancyProvider {
	/**
	 * Gets the occupancy size of the item stack.
	 *
	 * @param stack the item stack
	 * @return the occupancy size of the item stack.
	 */
	int getBundleOccupancy(ItemStack stack);
}
