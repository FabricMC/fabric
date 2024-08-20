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

package net.fabricmc.fabric.mixin.content.registry;

import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

import net.fabricmc.fabric.api.registry.FabricFuelRegistryBuilder;

/**
 * Implements the interface injection for {@link FabricFuelRegistryBuilder}, which provides getters for private fields.
 */
@Mixin(FuelRegistry.Builder.class)
public abstract class FuelRegistryBuilderMixin implements FabricFuelRegistryBuilder {
	@Shadow
	@Final
	private RegistryWrapper<Item> itemLookup;

	@Shadow
	@Final
	private FeatureSet featureSet;

	@Shadow
	@Final
	private Object2IntSortedMap<Item> fuelValues;

	@Override
	public FuelRegistry.Builder remove(ItemConvertible item) {
		this.fuelValues.removeInt(item.asItem());
		return (FuelRegistry.Builder) (Object) this;
	}

	@Override
	public RegistryWrapper<Item> getItemLookup() {
		return this.itemLookup;
	}

	@Override
	public FeatureSet getEnabledFeatures() {
		return this.featureSet;
	}
}
