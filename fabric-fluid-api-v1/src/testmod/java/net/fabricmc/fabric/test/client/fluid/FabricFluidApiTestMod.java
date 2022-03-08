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

package net.fabricmc.fabric.test.client.fluid;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public class FabricFluidApiTestMod implements ModInitializer {
	public static final CustomFluid CUSTOM = Registry.register(Registry.FLUID, "fabric-fluid-api-v1-testmod:custom", new CustomFluid.Still());
	public static final CustomFluid CUSTOM_FLOWING = Registry.register(Registry.FLUID, "fabric-fluid-api-v1-testmod:custom_flowing", new CustomFluid.Flowing());

	public static final FluidBlock CUSTOM_BLOCK = Registry.register(Registry.BLOCK, "fabric-fluid-api-v1-testmod:custom",
			new FluidBlock(CUSTOM, AbstractBlock.Settings.copy(Blocks.WATER)) { });

	public static final BucketItem CUSTOM_BUCKET = Registry.register(Registry.ITEM, "fabric-fluid-api-v1-testmod:custom_bucket",
			new BucketItem(CUSTOM, new FabricItemSettings().recipeRemainder(Items.BUCKET)));

	@Override
	public void onInitialize() {
	}
}
