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

import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Contains events to aid in modifying fuels during registration.
 */
public interface FabricFuelRegistryBuilder {
	/**
	 * An event that is called when the fuel registry is being built after vanilla fuels have been registered and before exclusions have been applied.
	 */
	Event<FabricFuelRegistryBuilder.BuildCallback> BUILD = EventFactory.createArrayBacked(FabricFuelRegistryBuilder.BuildCallback.class, listeners -> builder -> {
		for (FabricFuelRegistryBuilder.BuildCallback listener : listeners) {
			listener.build(builder);
		}
	});

	/**
	 * An event that is called when the fuel registry is being built after vanilla exclusions have been applied.
	 */
	Event<FabricFuelRegistryBuilder.ExclusionsCallback> EXCLUSIONS = EventFactory.createArrayBacked(FabricFuelRegistryBuilder.ExclusionsCallback.class, listeners -> builder -> {
		for (FabricFuelRegistryBuilder.ExclusionsCallback listener : listeners) {
			listener.buildExclusions(builder);
		}
	});

	default RegistryWrapper<Item> getItemLookup() {
		throw new AssertionError("Must be implemented via interface injection");
	}

	default FeatureSet getEnabledFeatures() {
		throw new AssertionError("Must be implemented via interface injection");
	}

	/**
	 * Use this event to register custom brewing recipes.
	 */
	@FunctionalInterface
	interface BuildCallback {
		/**
		 * Called when the fuel registry is being built after vanilla fuels have been registered and before exclusions have been applied.
		 *
		 * @param builder the builder being used to construct a {@link FuelRegistry} instance
		 */
		void build(FuelRegistry.Builder builder);
	}

	/**
	 * Use this event to register custom brewing recipes.
	 */
	@FunctionalInterface
	interface ExclusionsCallback {
		/**
		 * Called when the fuel registry is being built after vanilla exclusions have been applied.
		 *
		 * @param builder the builder being used to construct a {@link FuelRegistry} instance
		 */
		void buildExclusions(FuelRegistry.Builder builder);
	}
}
