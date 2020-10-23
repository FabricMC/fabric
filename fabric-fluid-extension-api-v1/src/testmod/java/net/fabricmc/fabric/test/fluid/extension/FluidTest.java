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

package net.fabricmc.fabric.test.fluid.extension;

import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.fluid.extension.v1.FabricFlowableFluidBlock;

public class FluidTest implements ModInitializer {
	public static String MOD_ID = "fabric-fluid-extensions-v1-testmod";

	public static LemonadeFluid LEMONADE_FLUID;
	public static BucketItem LEMONADE_BUCKET;
	public static FabricFlowableFluidBlock LEMONADE_BLOCK;

	@Override
	public void onInitialize() {
		LEMONADE_FLUID = Registry.register(Registry.FLUID, new Identifier(MOD_ID, "lemonade"), new LemonadeFluid());
		LEMONADE_BUCKET = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "lemonade_bucket"), new BucketItem(LEMONADE_FLUID, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
		LEMONADE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "lemonade"), new LemonadeFluidBlock());
	}
}
