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

package net.fabricmc.fabric.test.fluid.item;

import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.test.fluid.core.ModCore;
import net.fabricmc.fabric.test.fluid.fluid.MFluids;

public class MItems {
	public static final Identifier BLUE_FLUID_BUCKET_ID = new Identifier(ModCore.ID, "blue_fluid_bucket");
	public static final BucketItem BLUE_FLUID_BUCKET = Registry.register(Registry.ITEM, BLUE_FLUID_BUCKET_ID,
			new BucketItem(MFluids.BLUE_FLUID, new FabricItemSettings().maxCount(1).group(ItemGroup.MISC).recipeRemainder(Items.BUCKET)));

	public static final Identifier CYAN_FLUID_BUCKET_ID = new Identifier(ModCore.ID, "cyan_fluid_bucket");
	public static final BucketItem CYAN_FLUID_BUCKET = Registry.register(Registry.ITEM, CYAN_FLUID_BUCKET_ID,
			new BucketItem(MFluids.CYAN_FLUID, new FabricItemSettings().maxCount(1).group(ItemGroup.MISC).recipeRemainder(Items.BUCKET)));

	public static final Identifier GREEN_FLUID_BUCKET_ID = new Identifier(ModCore.ID, "green_fluid_bucket");
	public static final BucketItem GREEN_FLUID_BUCKET = Registry.register(Registry.ITEM, GREEN_FLUID_BUCKET_ID,
			new BucketItem(MFluids.GREEN_FLUID, new FabricItemSettings().maxCount(1).group(ItemGroup.MISC).recipeRemainder(Items.BUCKET)));

	public static final Identifier RED_FLUID_BUCKET_ID = new Identifier(ModCore.ID, "red_fluid_bucket");
	public static final BucketItem RED_FLUID_BUCKET = Registry.register(Registry.ITEM, RED_FLUID_BUCKET_ID,
			new BucketItem(MFluids.RED_FLUID, new FabricItemSettings().maxCount(1).group(ItemGroup.MISC).recipeRemainder(Items.BUCKET)));

	public static void load() {
	}

	public static void renderItems() {
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? 0x0000ff : -1, BLUE_FLUID_BUCKET);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? 0x00ffff : -1, CYAN_FLUID_BUCKET);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? 0x00ff00 : -1, GREEN_FLUID_BUCKET);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? 0xff0000 : -1, RED_FLUID_BUCKET);
	}
}
