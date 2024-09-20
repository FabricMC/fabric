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

package net.fabricmc.fabric.api.registry;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

/**
 * Contains events to aid in modifying fuels during registration.
 */
@ApiStatus.NonExtendable
public interface FabricFuelRegistryBuilder {
	/**
	 * Remove an @{link ItemConvertible} from the fuel registry.
	 *
	 * @param item the item to remove
	 * @return the @{link FuelRegistry.Builder} instance
	 */
	default FuelRegistry.Builder remove(ItemConvertible item) {
		throw new AssertionError("Must be implemented via interface injection");
	}

	/**
	 * Get the {@link RegistryWrapper} for {@link Item}.
	 * @return the item lookup
	 */
	default RegistryWrapper<Item> getItemLookup() {
		throw new AssertionError("Must be implemented via interface injection");
	}

	/**
	 * Get the {@link RegistryWrapper.WrapperLookup} for all registries.
	 * @return the registry lookup
	 */
	default RegistryWrapper.WrapperLookup getRegistries() {
		throw new AssertionError("Must be implemented via interface injection");
	}

	/**
	 * Get the currently enabled feature set.
	 * @return the {@link FeatureSet} instance
	 */
	default FeatureSet getEnabledFeatures() {
		throw new AssertionError("Must be implemented via interface injection");
	}
}
