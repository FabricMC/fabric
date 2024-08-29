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

package net.fabricmc.fabric.test.client.rendering.fluid;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class TestFluids {
	public static final String MOD_ID = "fabric-rendering-fluids-v1-testmod";
	public static final RegistryKey<Block> NO_OVERLAY_KEY = block("no_overlay");
	public static final NoOverlayFluid NO_OVERLAY = Registry.register(Registries.FLUID, NO_OVERLAY_KEY.getValue(), new NoOverlayFluid.Still());
	public static final NoOverlayFluid NO_OVERLAY_FLOWING = Registry.register(Registries.FLUID, id("no_overlay_flowing"), new NoOverlayFluid.Flowing());

	public static final FluidBlock NO_OVERLAY_BLOCK = Registry.register(Registries.BLOCK, NO_OVERLAY_KEY, new FluidBlock(NO_OVERLAY, AbstractBlock.Settings.copy(Blocks.WATER).registryKey(NO_OVERLAY_KEY)) {
	});

	public static final RegistryKey<Block> OVERLAY_KEY = block("overlay");
	public static final OverlayFluid OVERLAY = Registry.register(Registries.FLUID, OVERLAY_KEY.getValue(), new OverlayFluid.Still());
	public static final OverlayFluid OVERLAY_FLOWING = Registry.register(Registries.FLUID, id("overlay_flowing"), new OverlayFluid.Flowing());

	public static final FluidBlock OVERLAY_BLOCK = Registry.register(Registries.BLOCK, OVERLAY_KEY, new FluidBlock(OVERLAY, AbstractBlock.Settings.copy(Blocks.WATER).registryKey(OVERLAY_KEY)) {
	});

	public static final RegistryKey<Block> UNREGISTERED_KEY = block("unregistered");
	public static final UnregisteredFluid UNREGISTERED = Registry.register(Registries.FLUID, UNREGISTERED_KEY.getValue(), new UnregisteredFluid.Still());
	public static final UnregisteredFluid UNREGISTERED_FLOWING = Registry.register(Registries.FLUID, id("unregistered_flowing"), new UnregisteredFluid.Flowing());

	public static final FluidBlock UNREGISTERED_BLOCK = Registry.register(Registries.BLOCK, UNREGISTERED_KEY, new FluidBlock(UNREGISTERED, AbstractBlock.Settings.copy(Blocks.WATER).registryKey(UNREGISTERED_KEY)) {
	});

	public static final RegistryKey<Block> CUSTOM_KEY = block("custom");
	public static final CustomFluid CUSTOM = Registry.register(Registries.FLUID, CUSTOM_KEY.getValue(), new CustomFluid.Still());
	public static final CustomFluid CUSTOM_FLOWING = Registry.register(Registries.FLUID, id("custom_flowing"), new CustomFluid.Flowing());

	public static final FluidBlock CUSTOM_BLOCK = Registry.register(Registries.BLOCK, CUSTOM_KEY, new FluidBlock(CUSTOM, AbstractBlock.Settings.copy(Blocks.WATER).registryKey(CUSTOM_KEY)) {
	});

	private static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	private static RegistryKey<Block> block(String path) {
		return RegistryKey.of(RegistryKeys.BLOCK, id(path));
	}
}
