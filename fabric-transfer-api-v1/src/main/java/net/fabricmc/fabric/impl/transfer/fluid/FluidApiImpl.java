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

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidApi;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.mixin.transfer.BucketItemAccessor;

public class FluidApiImpl {
	public static void init() {
		// load static, called by the mod initializer
	}

	private static void onFluidRegistered(Fluid fluid) {
		if (fluid == null) return;
		Item item = fluid.getBucketItem();

		if (item instanceof BucketItem) {
			BucketItem bucketItem = (BucketItem) item;
			Fluid bucketFluid = ((BucketItemAccessor) bucketItem).getFluid();

			if (fluid == bucketFluid) {
				FluidApi.registerEmptyAndFullItems(Items.BUCKET, fluid, FluidConstants.BUCKET, bucketItem);
			}
		}
	}

	static {
		// register bucket compat
		Registry.FLUID.forEach(FluidApiImpl::onFluidRegistered);
		RegistryEntryAddedCallback.event(Registry.FLUID).register((rawId, id, fluid) -> onFluidRegistered(fluid));
		// register cauldron compat
		FluidApi.SIDED.registerForBlocks((world, pos, state, context) -> CauldronWrapper.get(world, pos), Blocks.CAULDRON);
	}
}
