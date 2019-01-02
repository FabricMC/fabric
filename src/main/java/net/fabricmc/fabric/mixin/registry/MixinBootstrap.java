/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.registry;

import net.fabricmc.fabric.impl.registry.*;
import net.fabricmc.fabric.registry.ListenableRegistry;
import net.minecraft.Bootstrap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bootstrap.class)
public class MixinBootstrap {
	@Inject(method = "setOutputStreams", at = @At("RETURN"))
	private static void initialize(CallbackInfo info) {
		// access Blocks, Items, ...
		Object o0 = Biomes.THE_END;
		Object o1 = Blocks.AIR;
		Object o3 = Fluids.EMPTY;
		Object o2 = Items.AIR;
		Object o4 = DimensionType.THE_END;

		((ListenableRegistry<Biome>) Registry.BIOME).registerListener(new BootstrapBiomeRegistryListener());
		((ListenableRegistry<Block>) Registry.BLOCK).registerListener(new BootstrapBlockRegistryListener());
		((ListenableRegistry<Fluid>) Registry.FLUID).registerListener(new BootstrapFluidRegistryListener());
		((ListenableRegistry<Item>) Registry.ITEM).registerListener(new BootstrapItemRegistryListener());
		((ListenableRegistry<DimensionType>) Registry.DIMENSION).registerListener(new BootstrapDimensionRegistryListener());
	}
}